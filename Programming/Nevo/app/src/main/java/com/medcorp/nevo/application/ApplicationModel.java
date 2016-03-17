package com.medcorp.nevo.application;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
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
import com.medcorp.nevo.database.entry.AlarmDatabaseHelper;
import com.medcorp.nevo.database.entry.GoalDatabaseHelper;
import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.database.entry.StepsDatabaseHelper;
import com.medcorp.nevo.event.FirmwareReceivedEvent;
import com.medcorp.nevo.event.OnSyncEvent;
import com.medcorp.nevo.googlefit.GoogleFitManager;
import com.medcorp.nevo.googlefit.GoogleFitStepsDataHandler;
import com.medcorp.nevo.googlefit.GoogleFitTaskCounter;
import com.medcorp.nevo.googlefit.GoogleHistoryUpdateTask;
import com.medcorp.nevo.listener.GoogleFitHistoryListener;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.Goal;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.network.listener.ResponseListener;
import com.medcorp.nevo.util.Common;
import com.medcorp.nevo.util.Preferences;
import com.medcorp.nevo.validic.ValidicManager;
import com.medcorp.nevo.validic.model.NevoUser;
import com.medcorp.nevo.validic.model.ValidicDeleteRecordModel;
import com.medcorp.nevo.validic.model.ValidicReadAllRecordsModel;
import com.medcorp.nevo.validic.model.ValidicReadRecordModel;
import com.medcorp.nevo.validic.model.ValidicRecord;
import com.medcorp.nevo.validic.model.ValidicRecordModel;
import com.medcorp.nevo.validic.model.ValidicUser;
import com.medcorp.nevo.validic.model.VerifyCredentialModel;
import com.medcorp.nevo.validic.request.AddRecordRequest;
import com.medcorp.nevo.validic.request.CreateUserRequest;
import com.medcorp.nevo.validic.request.DeleteRecordRequest;
import com.medcorp.nevo.validic.request.GetAllRecordsRequest;
import com.medcorp.nevo.validic.request.GetRecordRequest;
import com.medcorp.nevo.validic.request.NevoUserLogin;
import com.medcorp.nevo.validic.request.NevoUserRegister;
import com.medcorp.nevo.validic.request.UpdateRecordRequest;
import com.medcorp.nevo.validic.request.VerifyCredentialRequest;
import com.medcorp.nevo.validic.retrofit.CreateUserRequestObject;
import com.medcorp.nevo.validic.retrofit.CreateUserRequestObjectUser;
import com.medcorp.nevo.validic.retrofit.CreateUserRetroRequest;
import com.medcorp.nevo.validic.retrofit.VerifyCredentialsRetroRequest;
import com.medcorp.nevo.view.ToastHelper;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.medcorp.library.ble.controller.OtaController;
import net.medcorp.library.ble.util.Constants;
import net.medcorp.library.ble.util.Optional;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
    private boolean firmwareUpdateAlertDailog = false;
    //if it is -1, means mcu version hasn't be read
    private int mcuFirmwareVersion = -1;
    private int bleFirmwareVersion = -1;
    private GoogleFitManager googleFitManager;
    private GoogleFitTaskCounter googleFitTaskCounter;
    private ValidicManager validicManager;
    private NevoUser  nevoUser;
    private final String DEFAULT_NEVO_USER_TOKEN = "a6b3e3fa8b8d9f59bb27fe4c11ed68df";
    private final String DEFAULT_VALIDIC_USER_ID = "56e8c19b1147b14e3c000658";
    private final String DEFAULT_VALIDIC_RECORD_ID = "56ea5f7d36c913062d000233";
    private final String DEFAULT_VALIDIC_USER_TOKEN = "RyuWaZ5yVXixE3TTCEPg";

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
        updateGoogleFit();
        validicManager = new ValidicManager(this);
        nevoUser = new NevoUser();
        //TODO here assume a default user gaillysu@med-corp.net has logged in, and has got connected to validic marketplace.
        nevoUser.setToken(DEFAULT_NEVO_USER_TOKEN);
        nevoUser.setValidicID(DEFAULT_VALIDIC_USER_ID);
        nevoUser.setLastValidicRecordID(DEFAULT_VALIDIC_RECORD_ID);
        nevoUser.setValidicUserAccessToken(DEFAULT_VALIDIC_USER_TOKEN);
        verifyValidicCredential();
    }

    @Subscribe
    public void onEvent(FirmwareReceivedEvent event){
        //in tutorial steps, don't popup this alert dialog
        if(!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG,true))
        {
            if(event.getType() == Constants.DfuFirmwareTypes.SOFTDEVICE)
            {
                mcuFirmwareVersion = Integer.parseInt(event.getVersion());
            }
            if(event.getType() == Constants.DfuFirmwareTypes.APPLICATION)
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
                                    bundle.putStringArrayList("firmwares", needOTAFirmwareList);
                                    bundle.putBoolean("backtosetting",false);
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

    public void initGoogleFit(AppCompatActivity appCompatActivity) {
        if (Preferences.isGoogleFitSet(this)) {
            googleFitTaskCounter  = new GoogleFitTaskCounter(3);
            googleFitManager = new GoogleFitManager(this,connectionCallbacks,onConnectionFailedListener);
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

    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED ||
                    result.getErrorCode() == FitnessStatusCodes.NEEDS_OAUTH_PERMISSIONS) {
                try {
                    if (googleFitManager.getActivity()!= null) {
                        result.startResolutionForResult(googleFitManager.getActivity(), GOOGLE_FIT_OATH_RESULT);
                    }
                } catch (IntentSender.SendIntentException e) {
                    ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_could_not_login);
                }
            } else {
                ToastHelper.showShortToast(ApplicationModel.this,R.string.google_fit_connecting);
            }
        }
    };

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
        }

        @Override
        public void onConnectionSuspended(int result) {
            if (result == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                ToastHelper.showShortToast(ApplicationModel.this, R.string.google_fit_network_lost);
            } else if (result == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                ToastHelper.showShortToast(ApplicationModel.this,R.string.google_fit_service_disconnected);
            }else{
                ToastHelper.showShortToast(ApplicationModel.this,R.string.google_fit_unknown_network);
            }
        }
    };

    private GoogleFitHistoryListener googleFitHistoryListener = new GoogleFitHistoryListener() {
        @Override
        public void onUpdateSuccess() {
            googleFitTaskCounter.incrementSuccessAndFinish();
            if (googleFitTaskCounter.allSucces()) {
                ToastHelper.showLongToast(ApplicationModel.this, "Updated Google Fit");
                googleFitTaskCounter.reset();
            }
        }

        @Override
        public void onUpdateFailed() {
            googleFitTaskCounter.incrementFinish();
            if(googleFitTaskCounter.areTasksDone()) {
                ToastHelper.showLongToast(ApplicationModel.this, "Couldn't updated Google Fit");
                googleFitTaskCounter.reset();
            }
        }
    };

    public void updateGoogleFit(){
        if(Preferences.isGoogleFitSet(this)) {
            initGoogleFit(null);
            GoogleFitStepsDataHandler dataHandler = new GoogleFitStepsDataHandler(getAllSteps(), ApplicationModel.this);
            new GoogleHistoryUpdateTask(googleFitManager, googleFitHistoryListener).execute(dataHandler.getStepsDataSet());
            new GoogleHistoryUpdateTask(googleFitManager, googleFitHistoryListener).execute(dataHandler.getCaloriesDataSet());
            new GoogleHistoryUpdateTask(googleFitManager, googleFitHistoryListener).execute(dataHandler.getDistanceDataSet());
        }
    }

    public ValidicManager getValidicManager() {
        return validicManager;
    }
    public NevoUser getNevoUser(){
        return nevoUser;
    }

    private void processListener(final ResponseListener listener,final Object result,final boolean exception)
    {
        if(listener!=null && result!=null) {
            final Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(exception) {
                        listener.onRequestFailure((SpiceException)result);
                    }
                    else {
                        listener.onRequestSuccess(result);
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
        CreateUserRequestObject object = new CreateUserRequestObject();
        object.setPin(pinCode);
        object.setAccess_token(validicManager.getOrganizationToken());
        CreateUserRequestObjectUser user  = new CreateUserRequestObjectUser();
        user.setUid(nevoUser.getToken());
        object.setUser(user);
        Gson gson = new Gson();

        Log.w("Karl",gson.toJson(object).toString());
        CreateUserRetroRequest request = new CreateUserRetroRequest(validicManager.getOrganizationID(), object);

        validicManager.execute(request, new RequestListener<ValidicUser>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("ApplicationModel", "spiceException = " + spiceException.getCause());
                processListener(listener, spiceException,true);
            }

            @Override
            public void onRequestSuccess(ValidicUser validicUser) {
                Log.i("ApplicationModel", "ValidicUser = " + new Gson().toJson(validicUser));
                //TODO here get status 200, update "validicID" field where the token == nevoUserToken in the "user" table
                nevoUser.setValidicID(validicUser.getUser().get_id());
                nevoUser.setValidicUserAccessToken(validicUser.getUser().getAccess_token());
                processListener(listener, validicUser,false);
            }
        });
    }
    public void nevoUserLogin(String email,String password,final ResponseListener listener)
    {
        //TODO here add a record in the user table with uid/token
    }

    public void addValidicRecord(int nevoUserID,Date date,final ResponseListener listener)
    {
        //TODO current app, no use the nevo User ID when save steps record, 0 menas it is a public user.(unlogged in user)
        Steps steps =  getDailySteps(nevoUserID, Common.removeTimeFromDate(date));
        ValidicRecord record = new ValidicRecord();
        record.setSteps(steps.getSteps());
        String utc_offset = new SimpleDateFormat("z").format(date).substring(3);//remove "GMT" starting
        String timestamp  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date) + "+00:00";
        record.setTimestamp(timestamp);
        record.setUtc_offset(utc_offset);
        record.setDistance(steps.getDistance());
        record.setFloors(0);
        record.setElevation(0);
        record.setCalories_burned(steps.getCalories());
        record.setActivity_id(steps.getiD());

        AddRecordRequest addRecordRequest = new AddRecordRequest(record,validicManager.getOrganizationID(),validicManager.getOrganizationToken(),nevoUser.getValidicID());
        validicManager.execute(addRecordRequest, new RequestListener<ValidicRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                processListener(listener, spiceException,true);
            }

            @Override
            public void onRequestSuccess(ValidicRecordModel validicRecordModel) {
                Log.i("ApplicationModel", "validicRecordModel = " + validicRecordModel);
                getNevoUser().setLastValidicRecordID(validicRecordModel.getRoutine().get_id());
                //TODO update steps table where steps.Id = record.activity_id
                processListener(listener, validicRecordModel,false);
            }
        });
    }

    public void updateValidicRecord(int nevoUserID,Date date,final ResponseListener listener)
    {
        Steps steps =  getDailySteps(nevoUserID, Common.removeTimeFromDate(date));
        UpdateRecordRequest updateRecordRequest = new UpdateRecordRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),getNevoUser().getValidicID(), getNevoUser().getLastValidicRecordID(),steps.getSteps());
        validicManager.execute(updateRecordRequest, new RequestListener<ValidicRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                processListener(listener, spiceException,true);
            }

            @Override
            public void onRequestSuccess(ValidicRecordModel validicRecordModel) {
                processListener(listener, validicRecordModel,false);
            }
        });
    }

    public void getValidicRecord()
    {
        GetRecordRequest  getRecordRequest = new GetRecordRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),getNevoUser().getValidicID(), getNevoUser().getLastValidicRecordID());

        validicManager.execute(getRecordRequest, new RequestListener<ValidicReadRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("ApplicationModel", "spiceException = " + spiceException.getCause());
            }

            @Override
            public void onRequestSuccess(ValidicReadRecordModel validicReadRecordModel) {
                Log.i("ApplicationModel", "validicReadRecordModel = " + validicReadRecordModel);
            }
        });
    }

    public void getAllValidicRecord()
    {
        GetAllRecordsRequest getAllRecordsRequest = new GetAllRecordsRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),getNevoUser().getValidicID());

        validicManager.execute(getAllRecordsRequest, new RequestListener<ValidicReadAllRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("ApplicationModel", "spiceException = " + spiceException.getCause());
            }

            @Override
            public void onRequestSuccess(ValidicReadAllRecordsModel validicReadAllRecordsModel) {
                Log.i("ApplicationModel", "validicReadAllRecordsModel = " + validicReadAllRecordsModel);
            }
        });
    }

    public void deleteValidicRecord()
    {
        DeleteRecordRequest deleteRecordRequest = new DeleteRecordRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),getNevoUser().getValidicID(), getNevoUser().getLastValidicRecordID());

        validicManager.execute(deleteRecordRequest, new RequestListener<ValidicDeleteRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.e("ApplicationModel", "spiceException = " + spiceException.getCause());
            }

            @Override
            public void onRequestSuccess(ValidicDeleteRecordModel validicDeleteRecordModel) {
                Log.i("ApplicationModel", "validicDeleteRecordModel = " + validicDeleteRecordModel);
            }
        });

    }

}