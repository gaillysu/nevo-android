package com.medcorp.nevo.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.controller.SyncControllerImpl;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.request.GetBatteryLevelNevoRequest;
import com.medcorp.nevo.ble.model.request.GetStepsGoalNevoRequest;
import com.medcorp.nevo.ble.model.request.SensorRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.entry.HeartbeatDatabaseHelper;
import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.database.entry.StepsDatabaseHelper;
import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.Steps;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Karl on 10/15/15.
 */
public class ApplicationModel extends Application  implements OnSyncControllerListener {

    private static ApplicationModel  mApplicationModel;
    private SyncController  mSyncController;
    //private OtaController   mOtaController;
    //private NetworkController mNetworkController;
    //private DatabaseHelper mDatabaseHelper;
    private UserDatabaseHelper mUserDatabaseHelper;
    private HeartbeatDatabaseHelper mHeartbeatDatabaseHelper;
    private StepsDatabaseHelper mStepsDatabaseHelper;
    private SleepDatabaseHelper mSleepDatabaseHelper;
    private Optional<ActivityObservable> observableActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("Karl", "On create app model");
        mApplicationModel = this;
        observableActivity = new Optional<>();
        //mOtaController  = new OtaControllerImpl(this,false);
        mSyncController = new SyncControllerImpl(this);
        mSyncController.setSyncControllerListenser(this);
        //mDatabaseHelper =  DatabaseHelper.getInstance(this);
        mUserDatabaseHelper = new UserDatabaseHelper();
        mHeartbeatDatabaseHelper = new HeartbeatDatabaseHelper();
        mStepsDatabaseHelper = new StepsDatabaseHelper();
        mSleepDatabaseHelper = new SleepDatabaseHelper();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void packetReceived(NevoPacket packet) {
        if(observableActivity.notEmpty()) {
            if (packet.getHeader() == (byte) GetStepsGoalNevoRequest.HEADER) {
                observableActivity.get().notifyDatasetChanged();
            }
            else if((byte) GetBatteryLevelNevoRequest.HEADER == packet.getHeader()) {
                observableActivity.get().batteryInfoReceived(new Battery(packet.newBatteryLevelNevoPacket().getBatteryLevel()));
            }
            else if((byte) 0xF0 == packet.getHeader()) {
                observableActivity.get().findWatchSuccess();
            }
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
    public static Application getApplicationModel()
    {
        return mApplicationModel;
    }

    public SyncController getSyncController(){return mSyncController;}
    //public OtaController getOtaController(){return mOtaController;}

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
        return mSyncController.getFirmwareVersion();
    }

    public void forgetDevice() {
        mSyncController.forgetDevice();
    }

    public List<Steps> getAllSteps(){
        return mStepsDatabaseHelper.getAll();
    }

    public List<Sleep> getAllSleep(){
        return mSleepDatabaseHelper.getAll();
    }

    public void saveDailySteps(Steps steps)
    {
        mStepsDatabaseHelper.update(steps);
    }

    /**
     * userid: which one's steps, date:which date's steps
     * @param userid,date
     * @return
     */
    public Steps getDailySteps(int userid,Date date)
    {
        return mStepsDatabaseHelper.get(userid,date);
    }
    public void saveDailySleep(Sleep sleep)
    {
        mSleepDatabaseHelper.update(sleep);
    }
    /**
     * userid: which one's sleep, date:which date's sleep
     * @param userid,date
     * @return
     */
    public Sleep getDailySleep(int userid,Date date)
    {
        return mSleepDatabaseHelper.get(userid,date);
    }

    public Date getDateFromDate(Date date)
    {
        //set the Day from 00:00:00
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(date);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date today = calBeginning.getTime();
        return today;
    }
}
