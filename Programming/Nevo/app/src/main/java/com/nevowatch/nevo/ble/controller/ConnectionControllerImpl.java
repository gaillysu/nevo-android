package com.nevowatch.nevo.ble.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.nevowatch.nevo.R;
import com.nevowatch.nevo.ble.ble.GattAttributes;
import com.nevowatch.nevo.ble.kernel.BLEConnectTimeoutException;
import com.nevowatch.nevo.ble.kernel.BLENotSupportedException;
import com.nevowatch.nevo.ble.kernel.BluetoothDisabledException;
import com.nevowatch.nevo.ble.kernel.NevoBT;
import com.nevowatch.nevo.ble.model.packet.SensorData;
import com.nevowatch.nevo.ble.model.request.SensorRequest;
import com.nevowatch.nevo.ble.util.Constants;
import com.nevowatch.nevo.ble.util.Optional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
public class ConnectionControllerImpl implements ConnectionController, NevoBT.Delegate {

    private Timer mAutoReconnectTimer;
    private int  mTimerIndex = 0;
    private final static int[] mReConnectTimerPattern = new int[]{1000, 10000,10000,10000,
            30000,30000,30000,30000,30000,30000,30000,30000,30000,30000,/*5min*/
            60000,60000,60000,60000,60000,60000,60000,60000,60000,60000,/*10min*/
            120000,120000,120000,120000,120000,120000,120000,120000,120000,/*20min*/
            240000,3600000};

    //This boolean is the only reliable way to know if we are connected or not
    private boolean mIsConnected = false;

    //Every 2 seconds we will ping the device.
    //If there's no response, we are disconnected
    private int CHECK_CONNECTION_INTERVAL = 6000;
    private Timer mCheckConnectionTimer;
    private Date mLastPacket = new Date();
    Optional<ConnectionController.Delegate> mDelegate = new Optional<>();
    Context mContext;

    /**
     this parameter saved old BLE 's  address, when doing BLE OTA, the address has been changed to another one
     so, after finisned BLE ota, must restore it to normal 's address
     */
    private String mSavedAddress = "";
    private boolean isOTAmode = false;


    /*package*/ ConnectionControllerImpl(Context ctx){
        mContext = ctx;
        NevoBT.Singleton.getInstance(mContext).setDelegate(this);

        //This timer will check if there's a conenction at regular intervals
        mCheckConnectionTimer = new Timer();
        mCheckConnectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                NevoBT.Singleton.getInstance(mContext).ping();
                //If we havne't received any packets for more than 10 sec, it means that we are not connected
                if(new Date().getTime() - mLastPacket.getTime() > CHECK_CONNECTION_INTERVAL * 1.5) currentlyConnected(false);
            }
        }, CHECK_CONNECTION_INTERVAL, CHECK_CONNECTION_INTERVAL);


        //This timer will retry to connect at given intervals
        restartAutoReconnectTimer();
    }

    private void restartAutoReconnectTimer() {
        mAutoReconnectTimer = new Timer();
        mAutoReconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(mIsConnected) {
                    //Yes, we're connected ! Let's retry in 1 sec.
                    mTimerIndex = 0;
                } else {
                    //Ouch we're not connected, we have to try to connect, let's increment the timer index
                    mTimerIndex++;
                    if(mTimerIndex>=mReConnectTimerPattern.length) mTimerIndex = mReConnectTimerPattern.length - 1;
                    Log.w(NevoBT.TAG, "Connection lost, reconnecting in "+mReConnectTimerPattern[mTimerIndex]/1000+"s");
                    connect();
                }

                restartAutoReconnectTimer();
            }
        }, mReConnectTimerPattern[mTimerIndex] );
    }

    @Override
    public void connect() {

        List<GattAttributes.SupportedService> servicelist = new ArrayList<GattAttributes.SupportedService>();

        if(getOTAMode())
        {
            //when go to OTA mode, MAC address will be changed to new one, so here must forget the old noe
            //and scan the device which DFU service is opened (@see: GattAttributes.NEVO_OTA_SERVICE)
            forgetSavedAddress();
            servicelist.add(GattAttributes.SupportedService.nevo_ota);
        }
        else {
            servicelist.add(GattAttributes.SupportedService.nevo);
        }
        Optional<String> preferredAddress = new Optional<String>();

        if(hasSavedAddress()) preferredAddress.set(getSaveAddress());

        NevoBT.Singleton.getInstance(mContext).startScan(servicelist, preferredAddress);

    }


    @Override
    public void sendRequest(SensorRequest request) {
        NevoBT.Singleton.getInstance(mContext).sendRequest(request);
    }

    private void currentlyConnected(boolean isConnected) {
        if(isConnected) mLastPacket = new Date();

        if(isConnected!=mIsConnected) {

            mIsConnected = isConnected;

            //Callback are usually called on the main thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Log.w("Nevo BT SDK","Connected : "+mIsConnected);
                    if(mDelegate.notEmpty()) mDelegate.get().onConnectionStateChanged(mIsConnected,"");
                }
            });
        }
    }

    @Override
    public void onConnectionStateChanged(final boolean connected, final String address) {

        if(!address.equals("") && connected == true) setSaveAddress(address);

        currentlyConnected(connected);

    }

    @Override
    public void onException(final Exception e) {
        //Callback are usually called on the main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                if(mDelegate.notEmpty()) mDelegate.get().onException(e);
            }
        });

    }

    @Override
    public void onDataReceived(final SensorData data) {

        currentlyConnected(true);
        //Callback are usually called on the main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                if(mDelegate.notEmpty()) mDelegate.get().onDataReceived(data);
            }
        });

    }
    @Override
    public ConnectionController.Delegate setDelegate(Delegate delegate) {
        ConnectionController.Delegate old_deledgate = mDelegate.notEmpty()?mDelegate.get():null;
        mDelegate.set(delegate);
        return old_deledgate;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public boolean isConnected() {
        return mIsConnected;
    }

    @Override
    public void forgetSavedAddress() {
        //save it for OTA using
        mSavedAddress = getSaveAddress();
        setSaveAddress("");
    }

    @Override
    public boolean hasSavedAddress() {
        if(!getSaveAddress().equals(""))
        {
            return true;
        }
        return false;
    }

    private void setSaveAddress(String address)
    {
        mContext.getSharedPreferences(Constants.PREF_NAME, 0).edit().putString(Constants.SAVE_MAC_ADDRESS, address).commit();
    }

    private String getSaveAddress()
    {
        return mContext.getSharedPreferences(Constants.PREF_NAME, 0).getString(Constants.SAVE_MAC_ADDRESS, "");
    }

    @Override
    public String getFirmwareVersion() {
        return NevoBT.Singleton.getInstance(mContext).getFirmwareVersion();
    }

    @Override
    public String getSoftwareVersion() {
        return NevoBT.Singleton.getInstance(mContext).getSoftwareVersion();
    }

    @Override
    public void setOTAMode(boolean otaMode, boolean disConnect) {

        //No need to change the mode if we are already in OTA Mode
        if (getOTAMode() == otaMode ) return;
        isOTAmode = otaMode;
        if (disConnect)
        {
            //cancel reconnect timer, make sure OTA can do connect by OTAcontroller;
            mAutoReconnectTimer.cancel();
            mCheckConnectionTimer.cancel();
            NevoBT.Singleton.getInstance(mContext).disconnect();
        }
    }

    @Override
    public boolean getOTAMode() {
        return isOTAmode;
    }

    @Override
    public void restoreSavedAddress() {
        if(!mSavedAddress.equals(""))
        {
            setSaveAddress(mSavedAddress);
        }
    }


    @Override
    public void firmwareVersionReceived(final Constants.DfuFirmwareTypes whichfirmware, final String version) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(mDelegate.notEmpty()) mDelegate.get().firmwareVersionReceived(whichfirmware,version);
            }
        });
    }
}
