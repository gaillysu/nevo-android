package com.medcorp.nevo.ble.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.ble.GattAttributes;
import com.medcorp.nevo.ble.kernel.NevoBT;
import com.medcorp.nevo.ble.model.packet.SensorData;
import com.medcorp.nevo.ble.model.request.SensorRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.ble.util.Optional;

import java.lang.reflect.Method;
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

    //fixed by Gailly, from 1s to 1.5s, disconnect watch, defer 1.5s and reconnect it, it will easily get connected.
    private final static int[] mReConnectTimerPattern = new int[]{1500,
            10000,10000,10000,10000,10000,10000, /*1min*/
            10000,10000,10000,10000,10000,10000, /*1min*/
            10000,10000,10000,10000,10000,10000, /*1min*/
            0x7FFFFFFF/*not triggered timer*/
            };

    //This boolean is the only reliable way to know if we are connected or not
    private boolean mIsConnected = false;

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

        //This timer will retry to connect at given intervals
        restartAutoReconnectTimer();
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
        Log.w(NevoBT.TAG,"servicelist:"+servicelist.get(0) + ",address:"+ (preferredAddress.isEmpty()?"null":preferredAddress.get()));
        NevoBT.Singleton.getInstance(mContext).startScan(servicelist, preferredAddress);

    }

    @Override
    public void reconnect()
    {
        List<GattAttributes.SupportedService> servicelist = new ArrayList<GattAttributes.SupportedService>();
        servicelist.add(GattAttributes.SupportedService.nevo);
        Optional<String> preferredAddress = new Optional<String>();
        if(hasSavedAddress()) preferredAddress.set(getSaveAddress());
        NevoBT.Singleton.getInstance(mContext).startScan(servicelist, preferredAddress);
    }

   /*package*/ void destroy()
    {
        NevoBT.Singleton.getInstance(mContext).disconnect();
    }

    @Override
    public void sendRequest(SensorRequest request) {
        NevoBT.Singleton.getInstance(mContext).sendRequest(request);
    }

    private void currentlyConnected(boolean isConnected) {
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

        if(!address.equals("") && connected == true)
        {
            //firstly connected this nevo: such as: first run app, forget this nevo
            boolean firstConnected = !hasSavedAddress();
            setSaveAddress(address);

            //http://stackoverflow.com/questions/21398766/android-ble-connection-time-interval
            //fix a bug:when BLE OTA done,need repair nevo, if not, must twice connect nevo that nevo can work fine, here use code do repair working or twice connection
            //call pairDevice() after every connected, if call it within connect() before startScan() invoke,
            //some smartphone will popup message ,this message comes from Android OS, such as samsung...
            if((firstConnected || needPair())&& !getOTAMode()) pairDevice();
        }

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
        //whennever OTA mode true or false, keep the auto reconnect timer always on
        /**
        if(otaMode)
        {
            //cancel reconnect timer, make sure OTA can do connect by OTAcontroller;
            if(mAutoReconnectTimer!=null) {mAutoReconnectTimer.cancel();mAutoReconnectTimer=null;}
            if(mCheckConnectionTimer!=null) {mCheckConnectionTimer.cancel();mCheckConnectionTimer=null;}
        }
        else
        */
        {
            //restart timer and ping Timer
            mTimerIndex = 0; //after 1s ,do connect
            restartAutoReconnectTimer();
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

    @Override
    public void pairDevice()
    {
        if(!hasSavedAddress()) return;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) return;

        BluetoothDevice   device = bluetoothAdapter.getRemoteDevice(getSaveAddress());
        int state = device.getBondState();
        Log.i(NevoBT.TAG,"pairDevice(),current bind state: " + state);
        if(state != BluetoothDevice.BOND_BONDED)
        {
            boolean ret = false;
            try {
                ret =  createBond(BluetoothDevice.class,device);
                state = device.getBondState();
                Log.i(NevoBT.TAG, "bind state: " + state +",createBond() return:" + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void unPairDevice()
    {
        if(!hasSavedAddress()) return;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) return;

        BluetoothDevice   device = bluetoothAdapter.getRemoteDevice(getSaveAddress());
        int state = device.getBondState();
        Log.i(NevoBT.TAG,"unPairDevice(),current bind state: " + state);
        if(state == BluetoothDevice.BOND_BONDED)
        {
            boolean ret = false;
            try {
                ret = removeBond(BluetoothDevice.class,device);
                state = device.getBondState();
                Log.i(NevoBT.TAG, "bind state: " + state + ",removeBond() return:" + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(state == BluetoothDevice.BOND_BONDING)
        {
            boolean ret = false;
            try {
                ret = cancelBondProcess(BluetoothDevice.class,device);
                state = device.getBondState();
                Log.i(NevoBT.TAG, "bind state: " + state + ",cancelBondProcess() return:" + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }
    private boolean removeBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }
    private boolean cancelBondProcess(Class btClass,
                                      BluetoothDevice device)
            throws Exception {
        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }
    private boolean needPair()
    {
        if(getOTAMode()) return false;
        if(!hasSavedAddress()) return false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) return false;

        BluetoothDevice   device = bluetoothAdapter.getRemoteDevice(getSaveAddress());
        int state = device.getBondState();
        Log.i(NevoBT.TAG,"needPair(),current bind state: " + state);
        if(state != BluetoothDevice.BOND_BONDED) return true;
        return false;
    }

}
