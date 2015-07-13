package com.nevowatch.nevo.ble.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import android.app.NotificationManager;
import android.app.Notification;
import android.content.Intent;
import android.app.PendingIntent;
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
import com.nevowatch.nevo.MainActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
/*package*/ class ConnectionControllerImpl implements ConnectionController, NevoBT.Delegate {

    private Timer mAutoReconnectTimer = null;
    private int  mTimerIndex = 0;
    //when disconnect nevo more than 2min, nevo BT will power off, if app scan it follow current pattern
    //perhaps it is no useful,so app start scanning every 10s that is a good idea
    //so I fix the pattern as this {1000,10000}

    //private final static int[] mReConnectTimerPattern = new int[]{1000, 10000,10000,10000,
    //        30000,30000,30000,30000,30000,30000,30000,30000,30000,30000,/*5min*/
    //        60000,60000,60000,60000,60000,60000,60000,60000,60000,60000,/*10min*/
    //        120000,120000,120000,120000,120000,120000,120000,120000,120000,/*20min*/
    //        240000,3600000};

    private final static int[] mReConnectTimerPattern = new int[]{1000,
            10000,10000,10000,10000,10000,10000, /*1min*/
            10000,10000,10000,10000,10000,10000, /*1min*/
            10000,10000,10000,10000,10000,10000, /*1min*/
            0x7FFFFFFF/*not triggered timer*/
            };

    //This boolean is the only reliable way to know if we are connected or not
    private boolean mIsConnected = false;

    //Every 2 seconds we will ping the device.
    //If there's no response, we are disconnected
    private int CHECK_CONNECTION_INTERVAL = 6000;
    private Timer mCheckConnectionTimer = null;
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
        startCheckConnectionTimer();

        //This timer will retry to connect at given intervals
        restartAutoReconnectTimer();
    }

    private void startCheckConnectionTimer()
    {
        /* //sorry I remove these code, due to on Samsung smartphone, readCharacteristic will lead to BT got disconnect about perhaps 5min,10min,30min,... BT protocol stack will auto maintain the connection, no need  use the heartbeat  packets
        //first stop the timer thread!
        if(mCheckConnectionTimer!=null)mCheckConnectionTimer.cancel();
        mCheckConnectionTimer = new Timer();
        mCheckConnectionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                NevoBT.Singleton.getInstance(mContext).ping();
                //If we havne't received any packets for more than 10 sec, it means that we are not connected
                if(new Date().getTime() - mLastPacket.getTime() > CHECK_CONNECTION_INTERVAL * 1.5) currentlyConnected(false);
            }
        }, CHECK_CONNECTION_INTERVAL, CHECK_CONNECTION_INTERVAL);
        */
    }

    private void restartAutoReconnectTimer() {
        //first stop the timer thread!
        if(mAutoReconnectTimer!=null)mAutoReconnectTimer.cancel();
        mAutoReconnectTimer = new Timer();
        mAutoReconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(mIsConnected) {
                    //Yes, we're connected ! Let's retry in 1 sec.
                    mTimerIndex = 0;

                    // !!!need start new Timer every 1s???, perhaps run long time,
                    // the app will become very slowly. it is better that use 10s timer,
                    // here should set mTimerIndex 1, means every 10s to check connection status
                    //mTimerIndex = 1;
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
        //BEFORE EVERY CONNECT, NEED I DO PAIR DEVICE??? WHEN OTA DONE, FIRSTLY CONNECTED NEVO, ALWAYS CAN'T GET RESPONSE WITHOUT PAIR NEVO
        //http://stackoverflow.com/questions/21398766/android-ble-connection-time-interval
        //if(hasSavedAddress()) doPairDevice()
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

        sendNotification(connected);

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
        //save it for OTA using, add if() for avoid many times invoke "forgetSavedAddress"
        if(!getSaveAddress().equals(""))  mSavedAddress = getSaveAddress();
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
        if (getOTAMode() != otaMode )
            isOTAmode = otaMode;

        if (disConnect)
        {
            NevoBT.Singleton.getInstance(mContext).disconnect();
        }

        if(otaMode)
        {
            //cancel reconnect timer, make sure OTA can do connect by OTAcontroller;
            if(mAutoReconnectTimer!=null) {mAutoReconnectTimer.cancel();mAutoReconnectTimer=null;}
            if(mCheckConnectionTimer!=null) {mCheckConnectionTimer.cancel();mCheckConnectionTimer=null;}
        }
        else
        {
            //restart timer and ping Timer
            mTimerIndex = 0; //after 1s ,do connect
            restartAutoReconnectTimer();
            mLastPacket = new Date(); //from now, waiting 6s to do checking
            startCheckConnectionTimer();
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
        currentlyConnected(true);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(mDelegate.notEmpty()) mDelegate.get().firmwareVersionReceived(whichfirmware,version);
            }
        });
    }
    @Override
    public void newScan()
    {
        if(getOTAMode()) return;
       //restart timer and ping Timer
       mTimerIndex = 0; //after 1s ,do connect
       restartAutoReconnectTimer();
       mLastPacket = new Date(); //from now, waiting 6s to do checking
       startCheckConnectionTimer();
    }

    /**
     * how to check the Nevo always keep connection , invoke the function when  connection changed.
     * @param connected
     */
    private void sendNotification(boolean connected)
    {
       // Sorry, this feature isn't ready for prime time, I won't publish it
        NotificationManager nftm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        String  title = connected?mContext.getResources().getString(R.string.notification_connect_title):mContext.getResources().getString(R.string.notification_disconnect_title);
        String  content = connected?mContext.getResources().getString(R.string.notification_connect_content):mContext.getResources().getString(R.string.notification_disconnect_content);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, title, when);
        //notification.defaults = Notification.DEFAULT_VIBRATE;
        notification.setLatestEventInfo(mContext, title,content, null);
        //use hardcode message ID
        nftm.notify(connected?1:2, notification);

    }
}
