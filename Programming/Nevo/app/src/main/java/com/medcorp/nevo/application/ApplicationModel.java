package com.medcorp.nevo.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.medcorp.nevo.activity.DfuActivity;
import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.ble.controller.OtaController;
import com.medcorp.nevo.ble.controller.OtaControllerImpl;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.controller.SyncControllerImpl;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.request.GetBatteryLevelNevoRequest;
import com.medcorp.nevo.ble.model.request.GetStepsGoalNevoRequest;
import com.medcorp.nevo.ble.model.request.NumberOfStepsGoal;
import com.medcorp.nevo.ble.model.request.SetAlarmNevoRequest;
import com.medcorp.nevo.ble.model.request.SetGoalNevoRequest;
import com.medcorp.nevo.ble.model.request.SetNotificationNevoRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.entry.AlarmDatabaseHelper;
import com.medcorp.nevo.database.entry.GoalDatabaseHelper;
import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.database.entry.StepsDatabaseHelper;
import com.medcorp.nevo.googlefit.GoogleFitDataHandler;
import com.medcorp.nevo.googlefit.GoogleFitManager;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.model.Goal;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.util.Common;
import java.util.ArrayList;
import com.medcorp.nevo.util.Preferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Karl on 10/15/15.
 */
public class ApplicationModel extends Application  implements OnSyncControllerListener {

    private SyncController syncController;
    private OtaController  otaController;
    private StepsDatabaseHelper stepsDatabaseHelper;
    private SleepDatabaseHelper sleepDatabaseHelper;
    private AlarmDatabaseHelper alarmDatabaseHelper;
    private GoalDatabaseHelper goalDatabaseHelper;
    private Optional<ActivityObservable> observableActivity;
    private boolean firmwareUpdateAlertDailog = false;
    private int mcuFirmwareVersion = -1;//if it is -1, means mcu version hasn't be read
    private int bleFirmwareVersion = -1;//if it is -1, means ble version hasn't be read
    private GoogleFitManager googleFitManager;

    @Override
    public void onCreate() {
        super.onCreate();
        observableActivity = new Optional<>();
        syncController = new SyncControllerImpl(this);
        syncController.setSyncControllerListenser(this);
        otaController = new OtaControllerImpl(this);
        stepsDatabaseHelper = new StepsDatabaseHelper(this);
        sleepDatabaseHelper = new SleepDatabaseHelper(this);
        alarmDatabaseHelper = new AlarmDatabaseHelper(this);
        goalDatabaseHelper = new GoalDatabaseHelper(this);
        invokeGoogleFit();
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
            else if((byte) SetAlarmNevoRequest.HEADER == packet.getHeader()
                    || (byte) SetNotificationNevoRequest.HEADER == packet.getHeader()
                    || (byte) SetGoalNevoRequest.HEADER == packet.getHeader()) {
                observableActivity.get().onRequestResponse(true);
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
        //in tutorial steps, don't popup this alert dialog
        if(!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG,true))
        {
            int buildinSoftwareVersion = Common.getBuildinSoftwareVersion(this);
            int buildinFirmwareVersion = Common.getBuildinFirmwareVersion(this);
            if(whichfirmware == Constants.DfuFirmwareTypes.SOFTDEVICE)
            {
                mcuFirmwareVersion = Integer.parseInt(version);
            }
            if(whichfirmware == Constants.DfuFirmwareTypes.APPLICATION)
            {
                bleFirmwareVersion = Integer.parseInt(version);
            }
            //both MCU and BLE version all be read done. and make sure this dialog only popup once.
            if(!firmwareUpdateAlertDailog && mcuFirmwareVersion>=0 && bleFirmwareVersion>=0
                        && (mcuFirmwareVersion<buildinSoftwareVersion||bleFirmwareVersion<buildinFirmwareVersion))
            {
                firmwareUpdateAlertDailog = true;

                Intent intent = new Intent(ApplicationModel.this, DfuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("firmwares",(ArrayList<String>)Common.needOTAFirmwareURLs(this,mcuFirmwareVersion,bleFirmwareVersion));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtras(bundle);
                ApplicationModel.this.startActivity(intent);
            }
        }
    }

    @Override
    public void onSearching() {
        if(observableActivity.notEmpty())
        {
            observableActivity.get().onSearching();
        }
    }

    @Override
    public void onSearchSuccess() {
        if(observableActivity.notEmpty())
        {
            observableActivity.get().onSearchSuccess();
        }
    }

    @Override
    public void onSearchFailure() {
        if(observableActivity.notEmpty())
        {
            observableActivity.get().onSearchFailure();
        }
    }

    @Override
    public void onConnecting() {
        if(observableActivity.notEmpty())
        {
            observableActivity.get().onConnecting();
        }
    }

    @Override
    public void onSyncStart() {
        if(observableActivity.notEmpty())
        {
            observableActivity.get().onSyncStart();
        }
    }

    @Override
    public void onSyncEnd() {
        if(observableActivity.notEmpty())
        {
            observableActivity.get().onSyncEnd();
        }
    }

    @Override
    public void onInitializeStart() {
        if(observableActivity.notEmpty())
        {
            observableActivity.get().onInitializeStart();
        }
    }

    @Override
    public void onInitializeEnd() {
        if(observableActivity.notEmpty())
        {
            observableActivity.get().onInitializeEnd();
        }
    }


    public void observableActivity(ActivityObservable observable)
    {
        this.observableActivity.set(observable);
    }

    public SyncController getSyncController(){return syncController;}

    public OtaController getOtaController(){return otaController;}

    public void startConnectToWatch(boolean forceScan) {
        syncController.startConnect(forceScan, this);
    }

    public boolean isWatchConnected() {
        return syncController.isConnected();
    }

    public void blinkWatch(){
        syncController.findDevice();
    }

    public void getBatteryLevelOfWatch() {
        syncController.getBatteryLevel();
    }

    public String getWatchSoftware() {
        return syncController.getSoftwareVersion();
    }

    public String getWatchFirmware() {
        return syncController.getFirmwareVersion();
    }

    public void setGoal(Goal goal){
        syncController.setGoal(new NumberOfStepsGoal(goal.getSteps()));
    }
    public void setAlarm(List<Alarm> list)
    {
        syncController.setAlarm(list, false);
    }

    public void forgetDevice() {
        syncController.forgetDevice();
    }

    public List<Steps> getAllSteps(){
        return stepsDatabaseHelper.convertToNormalList(stepsDatabaseHelper.getAll());
    }

    public List<Alarm> getAllAlarm(){
        return alarmDatabaseHelper.convertToNormalList(alarmDatabaseHelper.getAll());
    }

    public void saveDailySteps(Steps steps)
    {
        stepsDatabaseHelper.update(steps);
    }

    public Steps getDailySteps(int userid,Date date)
    {   Optional<Steps> steps = stepsDatabaseHelper.get(userid, date);
        if (steps.notEmpty()) {
            return steps.get();
        }
        return new Steps(0);
    }
    public void saveDailySleep(Sleep sleep)
    {
        sleepDatabaseHelper.update(sleep);
    }


    public Alarm addAlarm(Alarm alarm){
        return alarmDatabaseHelper.add(alarm).get();
    }

    public boolean updateAlarm(Alarm alarm) {
        return alarmDatabaseHelper.update(alarm);
    }

    public Alarm getAlarmById(int id){
        return alarmDatabaseHelper.get(id,null).isEmpty()?null:alarmDatabaseHelper.get(id,null).get();
    }

    public boolean deleteAlarm(Alarm alarm){
      return  alarmDatabaseHelper.remove(alarm.getId(), null);
    }

    public List<Goal> getAllGoal(){
        return goalDatabaseHelper.convertToNormalList(goalDatabaseHelper.getAll());
    }
    public Goal addGoal(Goal goal){
        return goalDatabaseHelper.add(goal).get();
    }

    public boolean updateGoal(Goal goal) {
        return goalDatabaseHelper.update(goal);
    }

    public Goal getGoalById(int id){
        return goalDatabaseHelper.get(id,null).isEmpty()?null: goalDatabaseHelper.get(id,null).get();
    }

    public boolean deleteAlarm(Goal goal){
        return  goalDatabaseHelper.remove(goal.getId(), null);
    }

    public boolean isBluetoothOn(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()){
            return true;
        }
        return false;
    }

    public void invokeGoogleFit() {
        if (Preferences.isGoogleFitSet(this)) {
            googleFitManager = new GoogleFitManager(this);
            googleFitManager.connect();
        }
    }

    public void disconnectGoogleFit(){
        if (googleFitManager != null){
            googleFitManager.disconnect();
        }
    }

    private void sendData(){
        GoogleFitDataHandler dataHandler = new GoogleFitDataHandler(this,stepsDatabaseHelper.getAll());
    }
}
