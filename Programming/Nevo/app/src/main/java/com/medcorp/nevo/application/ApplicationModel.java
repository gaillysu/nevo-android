package com.medcorp.nevo.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.controller.SyncControllerImpl;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.request.SensorRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.model.DailySleep;
import com.medcorp.nevo.model.DailySteps;

import java.util.List;

/**
 * Created by Karl on 10/15/15.
 */
public class ApplicationModel extends Application  implements OnSyncControllerListener {

    private SyncController  mSyncController;
    //private NetworkController mNetworkController;
    private DatabaseHelper mDatabaseHelper;
    private Optional<ActivityObservable> observableActivity;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("Karl", "On create app model");
        observableActivity = new Optional<>();
        mSyncController = new SyncControllerImpl(this);
        mDatabaseHelper =  DatabaseHelper.getInstance(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void packetReceived(NevoPacket packet) {
        // TODO @Gailly save all the new data which comes in with the mDatabaseHelper (that's what I assume)
        if(observableActivity.notEmpty()) {
            observableActivity.get().notifyDatasetChanged();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void connectionStateChanged(boolean isConnected) {
        if(observableActivity.notEmpty()) {
            if (isConnected) {
                observableActivity.get().notifyOnConnected();
            } else {
                observableActivity.get().notifyOnDisconnected();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

    }

    /**
     * set current Observable activity
     * @param
     */
    public void setActiveActivity(ActivityObservable observable)
    {
        this.observableActivity.set(observable);
    }

    /**
     * send request to nevo
     * @param request
     */
    public void sendRequest(SensorRequest request)
    {
        mSyncController.sendRequest(request);
    }

    /**
     * send request to MED server, such as upload data/login/register profile
     */
   // public void sendRequest(NetworkRequest request)
   // {
   // }

    public SyncController getSyncController(){return mSyncController;}

    public void startConnectToWatch(boolean forceScan) {
        mSyncController.startConnect(forceScan,this);
    }

    public boolean isWatchConnected() {
        return mSyncController.isConnected();
    }

    public void blinkWatch(){
        mSyncController.findDevice();
    }

    public void getDailyInfo(boolean syncAll) {
        mSyncController.getDailyTrackerInfo(syncAll);
    }

    public void getBatteryLevelOfWatch() {
        mSyncController.getBatteryLevel();
    }

    public String getWatchSoftware() {
        return mSyncController.getSoftwareVersion();
    }

    public String getWatchFirmware() {
        return mSyncController.getSoftwareVersion();
    }

    public void forgetDevice() {
        mSyncController.forgetDevice();
    }

    public List<DailySteps> getAllSteps(){
        return mDatabaseHelper.getAllSteps();
    }

    public List<DailySleep> getAllSleep(){
        return mDatabaseHelper.getAllSleep();
    }
}
