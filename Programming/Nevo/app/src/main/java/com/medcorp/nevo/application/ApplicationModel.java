package com.medcorp.nevo.application;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.FitnessStatusCodes;
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
import com.medcorp.nevo.event.LoginEvent;
import com.medcorp.nevo.event.SignUpEvent;
import com.medcorp.nevo.event.bluetooth.LittleSyncEvent;
import com.medcorp.nevo.event.bluetooth.OnSyncEvent;
import com.medcorp.nevo.event.google.api.GoogleApiClientConnectionFailedEvent;
import com.medcorp.nevo.event.google.api.GoogleApiClientConnectionSuspendedEvent;
import com.medcorp.nevo.event.google.fit.GoogleFitUpdateEvent;
import com.medcorp.nevo.event.validic.ValidicAddRoutineRecordEvent;
import com.medcorp.nevo.event.validic.ValidicAddSleepRecordEvent;
import com.medcorp.nevo.event.validic.ValidicCreateUserEvent;
import com.medcorp.nevo.event.validic.ValidicDeleteRoutineRecordEvent;
import com.medcorp.nevo.event.validic.ValidicDeleteSleepRecordModelEvent;
import com.medcorp.nevo.event.validic.ValidicException;
import com.medcorp.nevo.event.validic.ValidicReadMoreRoutineRecordsModelEvent;
import com.medcorp.nevo.event.validic.ValidicReadMoreSleepRecordsModelEvent;
import com.medcorp.nevo.event.validic.ValidicUpdateRoutineRecordsModelEvent;
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
import com.medcorp.nevo.network.med.manager.MedManager;
import com.medcorp.nevo.network.med.model.NevoUserLoginRequest;
import com.medcorp.nevo.network.med.model.NevoUserModel;
import com.medcorp.nevo.network.med.model.NevoUserRegisterRequest;
import com.medcorp.nevo.network.validic.model.ValidicReadMoreSleepRecordsModel;
import com.medcorp.nevo.network.validic.model.ValidicRoutineRecordModelBase;
import com.medcorp.nevo.network.validic.model.ValidicSleepRecordModelBase;
import com.medcorp.nevo.network.validic.model.ValidicUser;
import com.medcorp.nevo.util.Common;
import com.medcorp.nevo.util.Preferences;
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
    private MedManager validicMedManager;
    private CloudSyncManager cloudSyncManager;
    private User nevoUser;

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
        validicMedManager = new MedManager(this);
        cloudSyncManager = new CloudSyncManager(this);
        Optional<User> user = userDatabaseHelper.getLoginUser();
        if (user.isEmpty()) {
            nevoUser = new User(0);
            nevoUser.setNevoUserID("0"); //"0" means anonymous user login
        } else {
            nevoUser = user.get();
        }
        updateGoogleFit();
    }

    @Subscribe
    public void onEvent(BLEFirmwareVersionReceivedEvent event) {
        //in tutorial steps, don't popup this alert dialog
        if (!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, true)) {
            if (event.getFirmwareTypes() == Constants.DfuFirmwareTypes.MCU) {
                mcuFirmwareVersion = Integer.parseInt(event.getVersion());
            }
            if (event.getFirmwareTypes() == Constants.DfuFirmwareTypes.BLUETOOTH) {
                bleFirmwareVersion = Integer.parseInt(event.getVersion());
            }
            //both MCU and BLE version all be read done. and make sure this dialog only popup once.
            if (!firmwareUpdateAlertDailog && mcuFirmwareVersion >= 0 && bleFirmwareVersion >= 0) {
                final ArrayList<String> needOTAFirmwareList = (ArrayList<String>) Common.needOTAFirmwareURLs(this, mcuFirmwareVersion, bleFirmwareVersion);
                if (!needOTAFirmwareList.isEmpty()) {
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
                                    bundle.putBoolean(getString(R.string.key_back_to_settings), false);
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
    public void onEvent(OnSyncEvent event) {
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED) {
            updateGoogleFit();
            getCloudSyncManager().launchSyncWeekly(nevoUser, getNeedSyncSteps(nevoUser.getNevoUserID()), getNeedSyncSleep(nevoUser.getNevoUserID()));
        }
    }

    @Subscribe
    public void onEvent(LittleSyncEvent event) {
        if (event.isSuccess()) {
            final Steps steps = getDailySteps(nevoUser.getNevoUserID(), Common.removeTimeFromDate(new Date()));
            getCloudSyncManager().launchSyncDaily(nevoUser, steps);
        }
    }

    public MedManager getNetworkManage() {
        return validicMedManager;
    }

    public SyncController getSyncController() {
        return syncController;
    }

    public OtaController getOtaController() {
        return otaController;
    }

    public void startConnectToWatch(boolean forceScan) {
        syncController.startConnect(forceScan);
    }

    public boolean isWatchConnected() {
        return syncController.isConnected();
    }

    public void blinkWatch() {
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

    public void setGoal(Goal goal) {
        syncController.setGoal(new NumberOfStepsGoal(goal.getSteps()));
    }

    public void setAlarm(List<Alarm> list) {
        syncController.setAlarm(list, false);
    }

    public void forgetDevice() {
        syncController.forgetDevice();
    }

    public List<Steps> getAllSteps() {
        return stepsDatabaseHelper.convertToNormalList(stepsDatabaseHelper.getAll(nevoUser.getNevoUserID()));
    }

    public List<Alarm> getAllAlarm() {
        return alarmDatabaseHelper.convertToNormalList(alarmDatabaseHelper.getAll());
    }

    public void saveNevoUser(User user) {
        userDatabaseHelper.update(user);
    }

    public void saveDailySteps(Steps steps) {
        stepsDatabaseHelper.update(steps);
    }

    public Steps getDailySteps(String userId, Date date) {
        Optional<Steps> steps = stepsDatabaseHelper.get(userId, Common.removeTimeFromDate(date));
        if (steps.notEmpty()) {
            return steps.get();
        }
        return new Steps(0);
    }

    public void saveDailySleep(Sleep sleep) {
        sleepDatabaseHelper.update(sleep);
    }

    public List<Steps> getNeedSyncSteps(String userid) {
        return stepsDatabaseHelper.getNeedSyncSteps(userid);
    }

    public boolean isFoundInLocalSteps(int activity_id) {
        return stepsDatabaseHelper.isFoundInLocalSteps(activity_id);
    }

    public boolean isFoundInLocalSleep(int activity_id) {
        return sleepDatabaseHelper.isFoundInLocalSleep(activity_id);
    }

    public void saveStepsFromValidic(ValidicRoutineRecordModelBase routine) {
        Date createDate = Common.getLocalDateFromUTCTimestamp(routine.getTimestamp(), routine.getUtc_offset());

        Steps steps = new Steps(createDate.getTime());
        steps.setDate(Common.removeTimeFromDate(createDate).getTime());
        steps.setSteps((int) routine.getSteps());
        steps.setNevoUserID(getNevoUser().getNevoUserID());
        steps.setValidicRecordID(routine.get_id());
        steps.setiD(Integer.parseInt(routine.getActivity_id()));
        if (routine.getExtras() != null) {
            steps.setGoal(routine.getExtras().getGoal());
        } else {
            steps.setGoal(7000);
        }
        saveDailySteps(steps);
    }

    public void saveSleepFromValidic(ValidicSleepRecordModelBase validicSleepRecord) {
        Date createDate = Common.getLocalDateFromUTCTimestamp(validicSleepRecord.getTimestamp(), validicSleepRecord.getUtc_offset());

        Sleep sleep = new Sleep(createDate.getTime());
        sleep.setiD(Integer.parseInt(validicSleepRecord.getActivity_id()));
        sleep.setDate(Common.removeTimeFromDate(createDate).getTime());
        if (validicSleepRecord.getExtras() != null) {
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
                    hourlySleepList.set(i, hourlySleepList.get(i) + Integer.parseInt(hourlyLight.getString(i)));
                }

                JSONArray hourlyDeep = new JSONArray(sleep.getHourlyDeep());
                for (int i = 0; i < hourlyDeep.length(); i++) {
                    deepSleep += Integer.parseInt(hourlyDeep.getString(i));
                    hourlySleepList.set(i, hourlySleepList.get(i) + Integer.parseInt(hourlyDeep.getString(i)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            sleep.setHourlySleep(hourlySleepList.toString());
            sleep.setTotalSleepTime(wake + deepSleep + lightSleep);
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


    public List<Sleep> getNeedSyncSleep(String userid) {
        return sleepDatabaseHelper.getNeedSyncSleep(userid);
    }

    public Alarm addAlarm(Alarm alarm) {
        return alarmDatabaseHelper.add(alarm).get();
    }

    public boolean updateAlarm(Alarm alarm) {
        return alarmDatabaseHelper.update(alarm);
    }

    public Alarm getAlarmById(int id) {
        return alarmDatabaseHelper.get(id).isEmpty() ? null : alarmDatabaseHelper.get(id).get(0).get();
    }

    public boolean deleteAlarm(Alarm alarm) {
        return alarmDatabaseHelper.remove(alarm.getId());
    }

    public List<Goal> getAllGoal() {
        return goalDatabaseHelper.convertToNormalList(goalDatabaseHelper.getAll());
    }

    public Goal addGoal(Goal goal) {
        return goalDatabaseHelper.add(goal).get();
    }

    public boolean updateGoal(Goal goal) {
        return goalDatabaseHelper.update(goal);
    }

    public Goal getGoalById(int id) {
        return goalDatabaseHelper.get(id).isEmpty() ? null : goalDatabaseHelper.get(id).get(0).get();
    }

    public boolean deleteAlarm(Goal goal) {
        return goalDatabaseHelper.remove(goal.getId());
    }

    public boolean isBluetoothOn() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            return true;
        }
        return false;
    }

    public void initGoogleFit(AppCompatActivity appCompatActivity) {
        if (Preferences.isGoogleFitSet(this)) {
            googleFitTaskCounter = new GoogleFitTaskCounter(3);
            googleFitManager = new GoogleFitManager(this);
            if (appCompatActivity != null) {
                googleFitManager.setActivityForResults(appCompatActivity);
            }
            googleFitManager.connect();
        }
    }

    public void disconnectGoogleFit() {
        if (googleFitManager != null) {
            googleFitManager.disconnect();
        }
    }

    @Subscribe
    public void onEvent(GoogleApiClientConnectionFailedEvent event) {
        if (event.getConnectionResult().getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED ||
                event.getConnectionResult().getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
            try {
                if (googleFitManager.getActivity() != null) {
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
    public void onEvent(GoogleApiClientConnectionSuspendedEvent event) {
        if (event.getState() == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_network_lost);
        } else if (event.getState() == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_service_disconnected);
        } else {
            ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_unknown_network);
        }
    }

    @Subscribe
    public void onEvent(GoogleFitUpdateEvent event) {
        if (event.isSuccess()) {
            googleFitTaskCounter.incrementSuccessAndFinish();
            if (googleFitTaskCounter.allSucces()) {
                ToastHelper.showLongToast(ApplicationModel.this, "Updated Google Fit");
                googleFitTaskCounter.reset();
            }
        } else {
            googleFitTaskCounter.incrementFinish();
            if (googleFitTaskCounter.areTasksDone()) {
                ToastHelper.showLongToast(ApplicationModel.this, "Couldn't updated Google Fit");
                googleFitTaskCounter.reset();
            }
        }
    }

    public void updateGoogleFit() {
        if (Preferences.isGoogleFitSet(this)) {
            initGoogleFit(null);
            GoogleFitStepsDataHandler dataHandler = new GoogleFitStepsDataHandler(getAllSteps(), ApplicationModel.this);
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getStepsDataSet());
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getCaloriesDataSet());
            new GoogleHistoryUpdateTask(googleFitManager).execute(dataHandler.getDistanceDataSet());
        }
    }

    public CloudSyncManager getCloudSyncManager() {
        return cloudSyncManager;
    }

    public User getNevoUser() {
        return nevoUser;
    }


    public void nevoUserRegister(String email, String password) {
        NevoUserRegisterRequest nevoUserRegisterRequest = new NevoUserRegisterRequest(email, password);

        validicMedManager.execute(nevoUserRegisterRequest, new RequestListener<NevoUserModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                EventBus.getDefault().post(new SignUpEvent(SignUpEvent.status.FAILED));
            }

            @Override
            public void onRequestSuccess(NevoUserModel nevoUserModel) {

                Log.i("ApplicationModel", "nevo user register: " + nevoUserModel.getState());
                if (nevoUserModel.getState().equals("success")) {
                    EventBus.getDefault().post(new SignUpEvent(SignUpEvent.status.SUCCESS));
                    nevoUser.setNevoUserID(nevoUserModel.getUid());
                    nevoUser.setNevoUserToken(nevoUserModel.getToken());
                    nevoUser.setIsLogin(true);
                    //save to "user" local table
                    saveNevoUser(nevoUser);
                    getSyncController().getDailyTrackerInfo(true);
                    getCloudSyncManager().launchSyncAll(nevoUser, getNeedSyncSteps(nevoUser.getNevoUserID()), getNeedSyncSleep(nevoUser.getNevoUserID()));
                } else {
                    EventBus.getDefault().post(new SignUpEvent(SignUpEvent.status.FAILED));
                }

            }
        });
    }

    public void nevoUserLogin(String email, String password) {
        NevoUserLoginRequest nevoUserLoginRequest = new NevoUserLoginRequest(email, password);

        validicMedManager.execute(nevoUserLoginRequest, new RequestListener<NevoUserModel>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                EventBus.getDefault().post(new LoginEvent(LoginEvent.status.FAILED));
            }

            @Override
            public void onRequestSuccess(NevoUserModel nevoUserModel) {
                if (nevoUserModel.getState().equals("success")) {
                    //get the user's profile from local database
                    List<Optional<User>> user = userDatabaseHelper.get(nevoUserModel.getUid());
                    if (!user.isEmpty()) {
                        nevoUser = user.get(0).get();
                    }
                    nevoUser.setNevoUserID(nevoUserModel.getUid());
                    nevoUser.setNevoUserToken(nevoUserModel.getToken());
                    nevoUser.setIsLogin(true);
                    saveNevoUser(nevoUser);
                    getSyncController().getDailyTrackerInfo(true);
                    getCloudSyncManager().launchSyncAll(nevoUser, getNeedSyncSteps(nevoUser.getNevoUserID()), getNeedSyncSleep(nevoUser.getNevoUserID()));
                    EventBus.getDefault().post(new LoginEvent(LoginEvent.status.SUCCESS));
                } else {
                    EventBus.getDefault().post(new LoginEvent(LoginEvent.status.FAILED));
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void createValidicUser(String pin, ResponseListener<ValidicUser> responseListener) {
        getCloudSyncManager().createValidicUser(nevoUser, pin, responseListener);
    }

    @Subscribe
    public void onValidicAddRoutineRecordEvent(ValidicAddRoutineRecordEvent validicAddRoutineRecordEvent) {
        saveDailySteps(validicAddRoutineRecordEvent.getSteps());

    }

    @Subscribe
    public void onValidicAddSleepRecordEvent(ValidicAddSleepRecordEvent validicAddSleepRecordEvent) {
        saveDailySleep(validicAddSleepRecordEvent.getSleep());
    }

    @Subscribe
    public void onValidicCreateUserEvent(ValidicCreateUserEvent validicCreateUserEvent) {
        saveNevoUser(validicCreateUserEvent.getUser());
        getSyncController().getDailyTrackerInfo(true);
        getCloudSyncManager().launchSyncAll(nevoUser, getNeedSyncSteps(nevoUser.getNevoUserID()), getNeedSyncSleep(nevoUser.getNevoUserID()));
    }

    @Subscribe
    public void onValidicDeleteSleepRecordModelEvent(ValidicDeleteSleepRecordModelEvent validicDeleteSleepRecordModelEvent) {
        sleepDatabaseHelper.remove(validicDeleteSleepRecordModelEvent.getUserId() + "", validicDeleteSleepRecordModelEvent.getDate());
    }

    @Subscribe
    public void onValidicException(ValidicException validicException) {
        Log.w("Karl", "Exception occured!");
        validicException.getException().printStackTrace();
    }

    @Subscribe
    public void onValidicReadMoreRoutineRecordsModelEvent(ValidicReadMoreRoutineRecordsModelEvent validicReadMoreRoutineRecordsModelEvent) {
        for (ValidicRoutineRecordModelBase routine : validicReadMoreRoutineRecordsModelEvent.getValidicReadMoreRoutineRecordsModel().getRoutine()) {
            int activity_id = Integer.parseInt(routine.getActivity_id());
            // if activity_id not exist in local Steps table, save it
            if (!isFoundInLocalSteps(activity_id)) {
                saveStepsFromValidic(routine);
            }
        }
    }

    @Subscribe
    public void onValidicReadMoreSleepRecordsModelEvent(ValidicReadMoreSleepRecordsModelEvent validicReadMoreSleepRecordsModelEvent) {
        ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel = validicReadMoreSleepRecordsModelEvent.getValidicReadMoreSleepRecordsModel();
        for (ValidicSleepRecordModelBase sleep : validicReadMoreSleepRecordsModel.getSleep()) {
            int activity_id = Integer.parseInt(sleep.getActivity_id());
            //if activity_id not exist in local Sleep table, save it
            if (isFoundInLocalSleep(activity_id)) {
                saveSleepFromValidic(sleep);
            }
        }
    }

    @Subscribe
    public void onValidicUpdateRoutineRecordsModelEvent(ValidicUpdateRoutineRecordsModelEvent validicUpdateRoutineRecordsModelEvent) {
        saveDailySteps(validicUpdateRoutineRecordsModelEvent.getSteps());

    }

    @Subscribe
    public void onValidicDeleteRoutineRecordEvent(ValidicDeleteRoutineRecordEvent validicDeleteRoutineRecordEvent) {
        stepsDatabaseHelper.remove(validicDeleteRoutineRecordEvent.getUserId() + "", validicDeleteRoutineRecordEvent.getDate());
    }

}