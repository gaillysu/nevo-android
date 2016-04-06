package com.medcorp.nevo.application;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.gson.Gson;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.DfuActivity;
import com.medcorp.nevo.ble.controller.OtaControllerImpl;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.controller.SyncControllerImpl;
import com.medcorp.nevo.ble.model.request.NumberOfStepsGoal;
import com.medcorp.nevo.cloud.CloudSyncManager;
import com.medcorp.nevo.database.entry.AlarmDatabaseHelper;
import com.medcorp.nevo.database.entry.GoalDatabaseHelper;
import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.database.entry.StepsDatabaseHelper;
import com.medcorp.nevo.database.entry.UserDatabaseHelper;
import com.medcorp.nevo.event.GoogleApiClientConnectionFailedEvent;
import com.medcorp.nevo.event.GoogleApiClientConnectionSuspendedEvent;
import com.medcorp.nevo.event.GoogleFitUpdateEvent;
import com.medcorp.nevo.event.LittleSyncEvent;
import com.medcorp.nevo.event.OnSyncEvent;
import com.medcorp.nevo.googlefit.GoogleFitManager;
import com.medcorp.nevo.googlefit.GoogleFitStepsDataHandler;
import com.medcorp.nevo.googlefit.GoogleFitTaskCounter;
import com.medcorp.nevo.googlefit.GoogleHistoryUpdateTask;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.Goal;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.model.User;
import com.medcorp.nevo.network.listener.ResponseListener;
import com.medcorp.nevo.util.Common;
import com.medcorp.nevo.util.Preferences;
import com.medcorp.nevo.validic.ValidicManager;
import com.medcorp.nevo.validic.ValidicMedManager;
import com.medcorp.nevo.validic.model.NevoUserModel;
import com.medcorp.nevo.validic.model.ValidicUser;
import com.medcorp.nevo.validic.model.VerifyCredentialModel;
import com.medcorp.nevo.validic.model.routine.RoutineGoal;
import com.medcorp.nevo.validic.model.routine.ValidicDeleteRoutineRecordModel;
import com.medcorp.nevo.validic.model.routine.ValidicReadMoreRoutineRecordsModel;
import com.medcorp.nevo.validic.model.routine.ValidicRoutineRecord;
import com.medcorp.nevo.validic.model.routine.ValidicRoutineRecordModel;
import com.medcorp.nevo.validic.model.routine.ValidicRoutineRecordModelBase;
import com.medcorp.nevo.validic.model.sleep.NevoHourlySleepData;
import com.medcorp.nevo.validic.model.sleep.ValidicDeleteSleepRecordModel;
import com.medcorp.nevo.validic.model.sleep.ValidicReadMoreSleepRecordsModel;
import com.medcorp.nevo.validic.model.sleep.ValidicSleepRecord;
import com.medcorp.nevo.validic.model.sleep.ValidicSleepRecordModel;
import com.medcorp.nevo.validic.model.sleep.ValidicSleepRecordModelBase;
import com.medcorp.nevo.validic.request.CreateUserRequestObject;
import com.medcorp.nevo.validic.request.CreateUserRequestObjectUser;
import com.medcorp.nevo.validic.request.CreateUserRetroRequest;
import com.medcorp.nevo.validic.request.NevoUserLoginRequest;
import com.medcorp.nevo.validic.request.NevoUserRegisterRequest;
import com.medcorp.nevo.validic.request.VerifyCredentialsRetroRequest;
import com.medcorp.nevo.validic.request.routine.AddRoutineRecordRequest;
import com.medcorp.nevo.validic.request.routine.DeleteRoutineRecordRequest;
import com.medcorp.nevo.validic.request.routine.GetMoreRoutineRecordsRequest;
import com.medcorp.nevo.validic.request.routine.UpdateRoutineRecordRequest;
import com.medcorp.nevo.validic.request.sleep.AddSleepRecordRequest;
import com.medcorp.nevo.validic.request.sleep.DeleteSleepRecordRequest;
import com.medcorp.nevo.validic.request.sleep.GetMoreSleepRecordsRequest;
import com.medcorp.nevo.view.ToastHelper;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.medcorp.library.ble.controller.OtaController;
import net.medcorp.library.ble.event.BLEFirmwareVersionReceivedEvent;
import net.medcorp.library.ble.util.Constants;
import net.medcorp.library.ble.util.Optional;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Karl on 10/15/15.
 */
public class ApplicationModel extends Application {

    public final int GOOGLE_FIT_OATH_RESULT = 1001;
    private SyncController syncController;
    private OtaController otaController;
    private StepsDatabaseHelper stepsDatabaseHelper;
    private SleepDatabaseHelper sleepDatabaseHelper;
    private AlarmDatabaseHelper alarmDatabaseHelper;
    private GoalDatabaseHelper goalDatabaseHelper;
    private UserDatabaseHelper userDatabaseHelper;
    private boolean firmwareUpdateAlertDailog = false;
    //if it is -1, means mcu version hasn't be read
    private int mcuFirmwareVersion = -1;
    private int bleFirmwareVersion = -1;
    private GoogleFitManager googleFitManager;
    private GoogleFitTaskCounter googleFitTaskCounter;
    private ValidicManager validicManager;
    private ValidicMedManager validicMedManager;
    private CloudSyncManager cloudSyncManager;
    private User  nevoUser;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        EventBus.getDefault().register(this);
        syncController = new SyncControllerImpl(this);
        otaController = new OtaControllerImpl(this);
        stepsDatabaseHelper = new StepsDatabaseHelper(this);
        sleepDatabaseHelper = new SleepDatabaseHelper(this);
        alarmDatabaseHelper = new AlarmDatabaseHelper(this);
        goalDatabaseHelper = new GoalDatabaseHelper(this);
        userDatabaseHelper = new UserDatabaseHelper(this);
        updateGoogleFit();
        validicManager = new ValidicManager(this);
        validicMedManager = new ValidicMedManager(this);
        cloudSyncManager = new CloudSyncManager(this);
        Optional<User> user = userDatabaseHelper.getLoginUser();
        if(user.isEmpty()) {
            nevoUser = new User(0);
            nevoUser.setNevoUserID("0"); //"0" means anonymous user login
        }
        else {
            nevoUser = user.get();
        }
        verifyValidicCredential();
    }

    @Subscribe
    public void onEvent(BLEFirmwareVersionReceivedEvent event){
        //in tutorial steps, don't popup this alert dialog
        if(!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG,true))
        {
            if(event.getFirmwareTypes() == Constants.DfuFirmwareTypes.MCU)
            {
                mcuFirmwareVersion = Integer.parseInt(event.getVersion());
            }
            if(event.getFirmwareTypes() == Constants.DfuFirmwareTypes.BLUETOOTH)
            {
                bleFirmwareVersion = Integer.parseInt(event.getVersion());
            }
            //both MCU and BLE version all be read done. and make sure this dialog only popup once.
            if(!firmwareUpdateAlertDailog && mcuFirmwareVersion>=0 && bleFirmwareVersion>=0)
            {
                final ArrayList<String> needOTAFirmwareList = (ArrayList<String>)Common.needOTAFirmwareURLs(this,mcuFirmwareVersion,bleFirmwareVersion);
                if(!needOTAFirmwareList.isEmpty())
                {
                    new MaterialDialog.Builder(this)
                            .title(R.string.dfu_update_positive)
                            .content(R.string.dfu_update_available)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    firmwareUpdateAlertDailog = true;
                                    Intent intent = new Intent(ApplicationModel.this, DfuActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putStringArrayList(getString(R.string.key_firmwares), needOTAFirmwareList);
                                    bundle.putBoolean(getString(R.string.key_back_to_settings),false);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtras(bundle);
                                    ApplicationModel.this.startActivity(intent);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    firmwareUpdateAlertDailog = true;
                                }
                            })
                            .positiveText(R.string.dfu_update_positive)
                            .negativeText(R.string.dfu_update_negative)
                            .cancelable(false)
                            .show();
                }
            }
        }
    }


    @Subscribe
    public void onEvent(OnSyncEvent event){
        if(event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED) {
            updateGoogleFit();
            getCloudSyncManager().launchSyncWeekly();
        }
    }

    @Subscribe
    public void onEvent(LittleSyncEvent event) {
        if (event.isSuccess()) {
            getCloudSyncManager().launchSyncDaily();
        }
    }

    public SyncController getSyncController(){return syncController;}

    public OtaController getOtaController(){return otaController;}

    public void startConnectToWatch(boolean forceScan) {
        syncController.startConnect(forceScan);
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
        return stepsDatabaseHelper.convertToNormalList(stepsDatabaseHelper.getAll(nevoUser.getNevoUserID()));
    }

    public List<Alarm> getAllAlarm(){
        return alarmDatabaseHelper.convertToNormalList(alarmDatabaseHelper.getAll());
    }

    public void saveNevoUser(User user)
    {
        userDatabaseHelper.update(user);
    }
    public void saveDailySteps(Steps steps)
    {
        stepsDatabaseHelper.update(steps);
    }

    public Steps getDailySteps(String userid,Date date)
    {   Optional<Steps> steps = stepsDatabaseHelper.get(userid, Common.removeTimeFromDate(date));
        if (steps.notEmpty()) {
            return steps.get();
        }
        return new Steps(0);
    }

    public void saveDailySleep(Sleep sleep)
    {
        sleepDatabaseHelper.update(sleep);
    }
    public Optional<Sleep> getDailySleep(String userid,Date date)
    {
        return sleepDatabaseHelper.get(userid, Common.removeTimeFromDate(date));
    }

    public List<Steps> getNeedSyncSteps(String userid)
    {
        return stepsDatabaseHelper.getNeedSyncSteps(userid);
    }
    public boolean isFoundInLocalSteps(int activity_id)
    {
        return stepsDatabaseHelper.isFoundInLocalSteps(activity_id);
    }
    public boolean isFoundInLocalSleep(int activity_id)
    {
        return sleepDatabaseHelper.isFoundInLocalSleep(activity_id);
    }
    public void saveStepsFromValidic(ValidicRoutineRecordModelBase routine)
    {
        Date createDate = Common.getLocalDateFromUTCTimestamp(routine.getTimestamp(),routine.getUtc_offset());

        Steps steps = new Steps(createDate.getTime());
        steps.setDate(Common.removeTimeFromDate(createDate).getTime());
        steps.setSteps((int) routine.getSteps());
        steps.setNevoUserID(getNevoUser().getNevoUserID());
        steps.setValidicRecordID(routine.get_id());
        steps.setiD(Integer.parseInt(routine.getActivity_id()));
        if(routine.getExtras()!=null)
        {
            steps.setGoal(routine.getExtras().getGoal());
        }
        else
        {
            steps.setGoal(7000);
        }
        saveDailySteps(steps);
    }

    public void saveSleepFromValidic(ValidicSleepRecordModelBase validicSleepRecord)
    {
        Date createDate = Common.getLocalDateFromUTCTimestamp(validicSleepRecord.getTimestamp(),validicSleepRecord.getUtc_offset());

        Sleep sleep = new Sleep(createDate.getTime());
        sleep.setiD(Integer.parseInt(validicSleepRecord.getActivity_id()));
        sleep.setDate(Common.removeTimeFromDate(createDate).getTime());
        if(validicSleepRecord.getExtras()!=null)
        {
            int lightSleep = 0;
            int deepSleep = 0;
            int wake = 0;
            List<Integer> hourlySleepList = new ArrayList<Integer>();

            sleep.setHourlyWake(validicSleepRecord.getExtras().getHourlyWake());
            sleep.setHourlyLight(validicSleepRecord.getExtras().getHourlyLight());
            sleep.setHourlyDeep(validicSleepRecord.getExtras().getHourlyDeep());

            try {
                JSONArray hourlyWake = new JSONArray(sleep.getHourlyWake());
                for (int i = 0; i < hourlyWake.length(); i++) {
                    wake += Integer.parseInt(hourlyWake.getString(i));
                    hourlySleepList.add(Integer.parseInt(hourlyWake.getString(i)));
                }

                JSONArray hourlyLight = new JSONArray(sleep.getHourlyLight());
                for (int i = 0; i < hourlyLight.length(); i++) {
                    lightSleep += Integer.parseInt(hourlyLight.getString(i));
                    hourlySleepList.set(i,hourlySleepList.get(i) + Integer.parseInt(hourlyLight.getString(i)));
                }

                JSONArray hourlyDeep = new JSONArray(sleep.getHourlyDeep());
                for (int i = 0; i <hourlyDeep.length(); i++) {
                    deepSleep += Integer.parseInt(hourlyDeep.getString(i));
                    hourlySleepList.set(i,hourlySleepList.get(i)+Integer.parseInt(hourlyDeep.getString(i)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            sleep.setHourlySleep(hourlySleepList.toString());
            sleep.setTotalSleepTime(wake+deepSleep+lightSleep);
            sleep.setTotalWakeTime(wake);
            sleep.setTotalLightTime(deepSleep);
            sleep.setTotalDeepTime(lightSleep);
        }
        //firstly reset sleep start/end time is 0, it means the day hasn't been calculate sleep analysis.
        sleep.setStart(0);
        sleep.setEnd(0);
        sleep.setNevoUserID(getNevoUser().getNevoUserID());
        sleep.setValidicRecordID(validicSleepRecord.get_id());
        try {
            sleep.setRemarks(new JSONObject().put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date(sleep.getDate()))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveDailySleep(sleep);
    }


    public List<Sleep> getNeedSyncSleep(String userid)
    {
        return sleepDatabaseHelper.getNeedSyncSleep(userid);
    }

    public Alarm addAlarm(Alarm alarm){
        return alarmDatabaseHelper.add(alarm).get();
    }

    public boolean updateAlarm(Alarm alarm) {
        return alarmDatabaseHelper.update(alarm);
    }

    public Alarm getAlarmById(int id){
        return alarmDatabaseHelper.get(id).isEmpty()?null:alarmDatabaseHelper.get(id).get(0).get();
    }

    public boolean deleteAlarm(Alarm alarm){
        return  alarmDatabaseHelper.remove(alarm.getId());
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
        return goalDatabaseHelper.get(id).isEmpty()?null: goalDatabaseHelper.get(id).get(0).get();
    }

    public boolean deleteAlarm(Goal goal){
        return  goalDatabaseHelper.remove(goal.getId());
    }

    public boolean isBluetoothOn(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()){
            return true;
        }
        return false;
    }

    public void initGoogleFit(AppCompatActivity appCompatActivity) {
        if (Preferences.isGoogleFitSet(this)) {
            googleFitTaskCounter  = new GoogleFitTaskCounter(3);
            googleFitManager = new GoogleFitManager(this);
            if (appCompatActivity != null) {
                googleFitManager.setActivityForResults(appCompatActivity);
            }
            googleFitManager.connect();
        }
    }

    public void disconnectGoogleFit(){
        if (googleFitManager != null){
            googleFitManager.disconnect();
        }
    }

    @Subscribe
    public void onEvent(GoogleApiClientConnectionFailedEvent event){
        if (event.getConnectionResult().getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED ||
                event.getConnectionResult().getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                if (googleFitManager.getActivity()!= null) {
                    event.getConnectionResult().startResolutionForResult(googleFitManager.getActivity(), GOOGLE_FIT_OATH_RESULT);
                }
            } catch (IntentSender.SendIntentException e) {
                ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_could_not_login);
            }
        } else {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_connecting);
        }
    }

    @Subscribe
    public void onEvent(GoogleApiClientConnectionSuspendedEvent event){
        if (event.getState() == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_network_lost);
        } else if (event.getState() == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            ToastHelper.showShortToast(ApplicationModel.this,R.string.google_fit_service_disconnected);
        }else{
            ToastHelper.showShortToast(ApplicationModel.this,R.string.google_fit_unknown_network);
        }
    }

    @Subscribe
    public void onEvent(GoogleFitUpdateEvent event){
        if (event.isSuccess()){
            googleFitTaskCounter.incrementSuccessAndFinish();
            if (googleFitTaskCounter.allSucces()) {
                ToastHelper.showLongToast(ApplicationModel.this, "Updated Google Fit");
                googleFitTaskCounter.reset();
            }
        }else{
            googleFitTaskCounter.incrementFinish();
            if(googleFitTaskCounter.areTasksDone()) {
                ToastHelper.showLongToast(ApplicationModel.this, "Couldn't updated Google Fit");
                googleFitTaskCounter.reset();
            }
        }
    }

    public void updateGoogleFit(){
        if(Preferences.isGoogleFitSet(this)) {
            initGoogleFit(null);
            GoogleFitStepsDataHandler dataHandler = new GoogleFitStepsDataHandler(getAllSteps(), ApplicationModel.this);
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getStepsDataSet());
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getCaloriesDataSet());
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getDistanceDataSet());
        }
    }

    public ValidicManager getValidicManager() {
        return validicManager;
    }

    public ValidicMedManager getValidicMedManager() {
        return validicMedManager;
    }

    public CloudSyncManager getCloudSyncManager() {
        return cloudSyncManager;
    }
    public User getNevoUser(){
        return nevoUser;
    }

    private void processListener(final ResponseListener listener,final Object result)
    {
        if(result!=null) {
            final Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(listener!=null)
                    {
                        if (result instanceof SpiceException) {
                            listener.onRequestFailure((SpiceException) result);
                        } else {
                            listener.onRequestSuccess(result);
                        }
                    }
                }
            });
        }
    }

    public void verifyValidicCredential()
    {
        VerifyCredentialsRetroRequest request = new VerifyCredentialsRetroRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken());
        validicManager.execute(request, new RequestListener<VerifyCredentialModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.w("Karl", "Failure?");
                spiceException.printStackTrace();
            }

            @Override
            public void onRequestSuccess(VerifyCredentialModel model) {
                Log.w("Karl", "Success, model = " + model.toString());
            }
        });
    }
    public void createValidicUser(String pinCode,final ResponseListener listener)
    {
        if(!nevoUser.isLogin()){
            return;
        }
        CreateUserRequestObject object = new CreateUserRequestObject();
        object.setPin(pinCode);
        object.setAccess_token(validicManager.getOrganizationToken());
        CreateUserRequestObjectUser user  = new CreateUserRequestObjectUser();
        user.setUid(nevoUser.getNevoUserToken());
        object.setUser(user);
        Gson gson = new Gson();

        Log.w("Karl",gson.toJson(object).toString());
        CreateUserRetroRequest request = new CreateUserRetroRequest(validicManager.getOrganizationID(), object);

        validicManager.execute(request, new RequestListener<ValidicUser>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("ApplicationModel", "spiceException = " + spiceException.getCause());
                processListener(listener, spiceException);
            }

            @Override
            public void onRequestSuccess(ValidicUser validicUser) {
                Log.i("ApplicationModel", "ValidicUser = " + new Gson().toJson(validicUser));
                nevoUser.setValidicUserID(validicUser.getUser().get_id());
                nevoUser.setValidicUserToken(validicUser.getUser().getAccess_token());
                nevoUser.setIsConnectValidic(true);
                saveNevoUser(nevoUser);
                getSyncController().getDailyTrackerInfo(true);
                getCloudSyncManager().launchSyncAll();
                processListener(listener, validicUser);
            }
        });
    }

    public void nevoUserRegister(String email,String password,final ResponseListener listener)
    {
        NevoUserRegisterRequest nevoUserRegisterRequest = new NevoUserRegisterRequest(email,password);

        validicMedManager.execute(nevoUserRegisterRequest, new RequestListener<NevoUserModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                processListener(listener, spiceException);
            }

            @Override
            public void onRequestSuccess(NevoUserModel nevoUserModel) {
                Log.i("ApplicationModel", "nevo user register: " + nevoUserModel.getState());
                if(nevoUserModel.getState().equals("success"))
                {
                    nevoUser.setNevoUserID(nevoUserModel.getUid());
                    nevoUser.setNevoUserToken(nevoUserModel.getToken());
                    nevoUser.setIsLogin(true);
                    //save to "user" local table
                    saveNevoUser(nevoUser);
                    getSyncController().getDailyTrackerInfo(true);
                    getCloudSyncManager().launchSyncAll();
                }
                processListener(listener, nevoUserModel);
            }
        });
    }

    public void nevoUserLogin(String email,String password,final ResponseListener listener)
    {
        NevoUserLoginRequest nevoUserLoginRequest = new NevoUserLoginRequest(email,password);

        validicMedManager.execute(nevoUserLoginRequest, new RequestListener<NevoUserModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                processListener(listener, spiceException);
            }

            @Override
            public void onRequestSuccess(NevoUserModel nevoUserModel) {
                Log.i("ApplicationModel", "nevo user login: " + nevoUserModel.getState());
                if(nevoUserModel.getState().equals("success"))
                {
                    nevoUser.setNevoUserID(nevoUserModel.getUid());
                    nevoUser.setNevoUserToken(nevoUserModel.getToken());
                    nevoUser.setIsLogin(true);
                    saveNevoUser(nevoUser);
                    getSyncController().getDailyTrackerInfo(true);
                    getCloudSyncManager().launchSyncAll();
                }
                processListener(listener, nevoUserModel);
            }
        });
    }

    public void addValidicRoutineRecord(final String nevoUserID, final Date date, final ResponseListener listener)
    {
        if(!nevoUser.isLogin()||!nevoUser.isConnectValidic()){
            return;
        }
        final Steps steps =  getDailySteps(nevoUserID, Common.removeTimeFromDate(date));
        if(steps.getCreatedDate()==0)
        {
            return;
        }
        ValidicRoutineRecord record = new ValidicRoutineRecord();
        record.setSteps(steps.getSteps());

        String utc_offset = new SimpleDateFormat("z").format(date).substring(3);
        Date theDay = Common.removeTimeFromDate(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00+00:00");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp  = sdf.format(theDay);

        record.setTimestamp(timestamp);
        record.setUtc_offset(utc_offset);
        record.setDistance(steps.getDistance());
        record.setFloors(0);
        record.setElevation(0);
        record.setCalories_burned(steps.getCalories());
        record.setActivity_id("" + steps.getiD());
        RoutineGoal goal = new RoutineGoal();
        goal.setGoal(steps.getGoal());
        record.setExtras(goal);

        AddRoutineRecordRequest addRecordRequest = new AddRoutineRecordRequest(record,validicManager.getOrganizationID(),validicManager.getOrganizationToken(),nevoUser.getValidicUserID());
        validicManager.execute(addRecordRequest, new RequestListener<ValidicRoutineRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                String causeString = spiceException.getCause()==null?"":spiceException.getCause().getLocalizedMessage()+"";
                if(causeString.contains("409") || causeString.contains("422"))
                {
                    //409:Activity is already taken
                    //422:Timestamp is already taken
                    updateValidicRoutineRecord(nevoUserID,date,listener);
                }
                processListener(listener, spiceException);
            }

            @Override
            public void onRequestSuccess(ValidicRoutineRecordModel validicRecordModel) {
                Log.i("ApplicationModel", "validicRecordModel = " + validicRecordModel);
                if (validicRecordModel.getCode().equals("200") || validicRecordModel.getCode().equals("201"))
                {
                    //save validic record ID to local database, for using cloud sync
                    steps.setValidicRecordID(validicRecordModel.getRoutine().get_id());
                    saveDailySteps(steps);
                }
                processListener(listener, validicRecordModel);
            }
        });
    }

    public void updateValidicRoutineRecord(String nevoUserID, Date date, final ResponseListener listener)
    {
        if(!nevoUser.isLogin()||!nevoUser.isConnectValidic()){
            return;
        }

        final Steps steps =  getDailySteps(nevoUserID, Common.removeTimeFromDate(date));

        getValidicRoutineRecord(date, new ResponseListener<ValidicReadMoreRoutineRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ValidicReadMoreRoutineRecordsModel validicReadMoreRoutineRecordsModel) {
                if (validicReadMoreRoutineRecordsModel.getSummary().getResults() > 0) {
                    String validicRecordID = validicReadMoreRoutineRecordsModel.getRoutine()[0].get_id();
                    //if local data == cloud data, save record_id and return
                    if(steps.getSteps()==validicReadMoreRoutineRecordsModel.getRoutine()[0].getSteps())
                    {
                        steps.setValidicRecordID(validicReadMoreRoutineRecordsModel.getRoutine()[0].get_id());
                        saveDailySteps(steps);
                        return;
                    }
                    UpdateRoutineRecordRequest updateRecordRequest = new UpdateRoutineRecordRequest(validicManager.getOrganizationID(), validicManager.getOrganizationToken(), getNevoUser().getValidicUserID(), validicRecordID, steps.getSteps());
                    validicManager.execute(updateRecordRequest, new RequestListener<ValidicRoutineRecordModel>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            spiceException.printStackTrace();
                            processListener(listener, spiceException);
                        }

                        @Override
                        public void onRequestSuccess(ValidicRoutineRecordModel validicRecordModel) {
                            //save validic record ID to local database, for using cloud sync
                            steps.setValidicRecordID(validicRecordModel.getRoutine().get_id());
                            saveDailySteps(steps);
                            processListener(listener, validicRecordModel);
                        }
                    });
                }
            }
        });
    }

    public void getValidicRoutineRecord(Date date,final ResponseListener listener)
    {
        getMoreValidicRoutineRecord(date, date,1,listener);
    }

    public void getMoreValidicRoutineRecord(Date startDate,Date endDate,int page,final ResponseListener listener)
    {
        if(!nevoUser.isLogin()||!nevoUser.isConnectValidic()){
            return;
        }

        String start_timestamp = Common.getUTCTimestampFromLocalDate(startDate);
        String end_timestamp = Common.getUTCTimestampFromLocalDate(endDate);

        GetMoreRoutineRecordsRequest getMoreRecordsRequest = new GetMoreRoutineRecordsRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),getNevoUser().getValidicUserID(),start_timestamp,end_timestamp,page);

        validicManager.execute(getMoreRecordsRequest, new RequestListener<ValidicReadMoreRoutineRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("ApplicationModel", "spiceException = " + spiceException.getCause());
                processListener(listener, spiceException);
            }

            @Override
            public void onRequestSuccess(ValidicReadMoreRoutineRecordsModel validicReadAllRecordsModel) {
                Log.i("ApplicationModel", "validicReadAllRecordsModel total: " + validicReadAllRecordsModel.getSummary().getResults());
                processListener(listener, validicReadAllRecordsModel);
            }
        });
    }

    public void deleteValidicRoutineRecord(final String nevoUserID, final Date date, final ResponseListener listener)
    {
        if(!nevoUser.isLogin()||!nevoUser.isConnectValidic()){
            return;
        }
        final Steps steps =  getDailySteps(nevoUserID, Common.removeTimeFromDate(date));
        String validicRecordID = steps.getValidicRecordID();
        if(validicRecordID.equals("0")) {
            return;
        }

        DeleteRoutineRecordRequest deleteRecordRequest = new DeleteRoutineRecordRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),getNevoUser().getValidicUserID(), validicRecordID);

        validicManager.execute(deleteRecordRequest, new RequestListener<ValidicDeleteRoutineRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("ApplicationModel", "spiceException = " + spiceException.getCause());
                processListener(listener, spiceException);
            }

            @Override
            public void onRequestSuccess(ValidicDeleteRoutineRecordModel validicDeleteRecordModel) {
                Log.i("ApplicationModel", "validicDeleteRecordModel = " + validicDeleteRecordModel);
                if(validicDeleteRecordModel.getCode().equals("200") || validicDeleteRecordModel.getCode().equals("201"))
                {
                    stepsDatabaseHelper.remove(nevoUserID,date);
                }
                processListener(listener, validicDeleteRecordModel);
            }
        });
    }

    //sleep operation functions:
    public void addValidicSleepRecord(String nevoUserID,Date date,final ResponseListener listener)
    {
        if(!nevoUser.isLogin()||!nevoUser.isConnectValidic()){
            return;
        }
        final Optional<Sleep> sleep = getDailySleep(nevoUserID,date);
        if(sleep.isEmpty()) {
            return;
        }
        ValidicSleepRecord record = new ValidicSleepRecord();
        record.setActivity_id("" + sleep.get().getiD());
        //validic sleep object , the value is  in seconds
        record.setAwake(60 * sleep.get().getTotalWakeTime());
        record.setLight(60 * sleep.get().getTotalLightTime());
        record.setDeep(60 * sleep.get().getTotalDeepTime());
        record.setTotal_sleep(60 * sleep.get().getTotalSleepTime());
        //TODO how to caculate the woken times? by hourly wake time is not zero?
        record.setTimes_woken(0);
        //REM value is set 0, nevo doesn't give this data
        record.setRem(0);
        NevoHourlySleepData nevoHourlySleepData = new NevoHourlySleepData();
        nevoHourlySleepData.setHourlyWake(sleep.get().getHourlyWake());
        nevoHourlySleepData.setHourlyLight(sleep.get().getHourlyLight());
        nevoHourlySleepData.setHourlyDeep(sleep.get().getHourlyDeep());
        record.setExtras(nevoHourlySleepData);

        String utc_offset = new SimpleDateFormat("z").format(date).substring(3);
        Date theDay = Common.removeTimeFromDate(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00+00:00");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp  = sdf.format(theDay);
        record.setTimestamp(timestamp);
        record.setUtc_offset(utc_offset);

        AddSleepRecordRequest addSleepRecordRequest = new AddSleepRecordRequest(record,validicManager.getOrganizationID(), validicManager.getOrganizationToken(),nevoUser.getValidicUserID());
        validicManager.execute(addSleepRecordRequest, new RequestListener<ValidicSleepRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                String causeString = spiceException.getCause()==null?"":spiceException.getCause().getLocalizedMessage()+"";
                if(causeString.contains("409")||causeString.contains("422"))
                {
                    //DO NOTHING, sleep record can't be update
                }
                processListener(listener, spiceException);
            }
            @Override
            public void onRequestSuccess(ValidicSleepRecordModel validicSleepRecordModel) {
                Log.i("ApplicationModel", "validicSleepRecordModel = " + validicSleepRecordModel);
                if(validicSleepRecordModel.getCode().equals("200")||validicSleepRecordModel.getCode().equals("201"))
                {
                    sleep.get().setValidicRecordID(validicSleepRecordModel.getSleep().get_id());
                    saveDailySleep(sleep.get());
                }
                processListener(listener, validicSleepRecordModel);
            }
        });
    }

    public void getValidicSleepRecord(Date date,final ResponseListener listener)
    {
        getMoreValidicSleepRecord(date,date,1,listener);
    }

    public void getMoreValidicSleepRecord(Date startDate,Date endDate,int page,final ResponseListener listener)
    {
        if(!nevoUser.isLogin()||!nevoUser.isConnectValidic()){
            return;
        }
        String start_timestamp = Common.getUTCTimestampFromLocalDate(startDate);
        String end_timestamp = Common.getUTCTimestampFromLocalDate(endDate);

        GetMoreSleepRecordsRequest getMoreRecordsRequest = new GetMoreSleepRecordsRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),getNevoUser().getValidicUserID(),start_timestamp,end_timestamp,page);

        validicManager.execute(getMoreRecordsRequest, new RequestListener<ValidicReadMoreSleepRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("ApplicationModel", "spiceException = " + spiceException.getCause());
                processListener(listener, spiceException);
            }

            @Override
            public void onRequestSuccess(ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel) {
                Log.i("ApplicationModel", "validicReadAllRecordsModel total: " + validicReadMoreSleepRecordsModel.getSummary().getResults());
                processListener(listener, validicReadMoreSleepRecordsModel);
            }
        });
    }

    public void deleteValidicSleepRecord(final String nevoUserID, final Date date, final ResponseListener listener)
    {
        if(!nevoUser.isLogin()||!nevoUser.isConnectValidic()){
            return;
        }
        Optional<Sleep> sleep = getDailySleep(nevoUserID, date);
        if(sleep.isEmpty()) {
            return;
        }
        String validicRecordID = sleep.get().getValidicRecordID();
        if(validicRecordID.equals("0")) {
            return;
        }
        DeleteSleepRecordRequest deleteRecordRequest = new DeleteSleepRecordRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),getNevoUser().getValidicUserID(),validicRecordID);

        validicManager.execute(deleteRecordRequest, new RequestListener<ValidicDeleteSleepRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("ApplicationModel", "spiceException = " + spiceException.getCause());
                processListener(listener, spiceException);
            }

            @Override
            public void onRequestSuccess(ValidicDeleteSleepRecordModel validicDeleteSleepRecordModel) {
                Log.i("ApplicationModel", "validicDeleteSleepRecordModel = " + validicDeleteSleepRecordModel);
                if(validicDeleteSleepRecordModel.getCode().equals("200") || validicDeleteSleepRecordModel.getCode().equals("201"))
                {
                    sleepDatabaseHelper.remove(nevoUserID,date);
                }
                processListener(listener, validicDeleteSleepRecordModel);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}