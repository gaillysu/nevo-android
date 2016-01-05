package com.medcorp.nevo.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.util.Log;

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
import com.medcorp.nevo.ble.model.request.SensorRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.entry.AlarmDatabaseHelper;
import com.medcorp.nevo.database.entry.PresetsDatabaseHelper;
import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.database.entry.StepsDatabaseHelper;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.model.Goal;
import com.medcorp.nevo.model.Preset;
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

    private SyncController syncController;
    private OtaController  otaController;
    private StepsDatabaseHelper stepsDatabaseHelper;
    private SleepDatabaseHelper sleepDatabaseHelper;
    private AlarmDatabaseHelper alarmDatabaseHelper;
    private PresetsDatabaseHelper presetsDatabaseHelper;
    private Optional<ActivityObservable> observableActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("Karl", "On create app model");
        observableActivity = new Optional<>();
        syncController = new SyncControllerImpl(this);
        syncController.setSyncControllerListenser(this);
        otaController = new OtaControllerImpl(this);
        stepsDatabaseHelper = new StepsDatabaseHelper(this);
        sleepDatabaseHelper = new SleepDatabaseHelper(this);
        alarmDatabaseHelper = new AlarmDatabaseHelper(this);
        presetsDatabaseHelper = new PresetsDatabaseHelper(this);
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

    public void sendRequest(SensorRequest request)
    {
        syncController.sendRequest(request);
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

    public void getDailyInfo(boolean syncAll) {
        syncController.getDailyTrackerInfo(syncAll);
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

    public void setPreset(Preset preset){
        syncController.setGoal(new NumberOfStepsGoal(preset.getSteps()));
    }
    public void setAlarm(List<Alarm> list)
    {
        syncController.setAlarm(list);
    }
    public void setNotification() {
        syncController.setNotification();
    }

    public void forgetDevice() {
        syncController.forgetDevice();
    }

    public List<Steps> getAllSteps(){
        return stepsDatabaseHelper.convertToNormalList(stepsDatabaseHelper.getAll());
    }

    public List<Sleep> getAllSleep(){
        return sleepDatabaseHelper.convertToNormalList(sleepDatabaseHelper.getAll());
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

    public Sleep getDailySleep(int userid,Date date)
    {   Optional<Sleep> sleep = sleepDatabaseHelper.get(userid, date);
        if(sleep.notEmpty()) {
            return sleep.get();
        }
        return new Sleep(0);
    }

    public Date getDateFromDate(Date date)
    {
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(date);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date today = calBeginning.getTime();
        return today;
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

    public List<Preset> getAllPreset(){
        return presetsDatabaseHelper.convertToNormalList(presetsDatabaseHelper.getAll());
    }
    public Preset addPreset(Preset preset){
        return presetsDatabaseHelper.add(preset).get();
    }

    public boolean updatePreset(Preset preset) {
        return presetsDatabaseHelper.update(preset);
    }

    public Preset getPresetById(int id){
        return presetsDatabaseHelper.get(id,null).isEmpty()?null:presetsDatabaseHelper.get(id,null).get();
    }

    public boolean deleteAlarm(Preset preset){
        return  presetsDatabaseHelper.remove(preset.getId(), null);
    }


}
