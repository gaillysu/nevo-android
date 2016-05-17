package com.medcorp.nevo.cloud;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.model.User;
import com.medcorp.nevo.network.listener.ResponseListener;
import com.medcorp.nevo.network.validic.manager.ValidicManager;
import com.medcorp.nevo.network.validic.model.CreateUserRequestObject;
import com.medcorp.nevo.network.validic.model.CreateUserRequestObjectUser;
import com.medcorp.nevo.network.validic.model.NevoHourlySleepData;
import com.medcorp.nevo.network.validic.model.RoutineGoal;
import com.medcorp.nevo.network.validic.model.ValidicDeleteRoutineRecordModel;
import com.medcorp.nevo.network.validic.model.ValidicDeleteSleepRecordModel;
import com.medcorp.nevo.network.validic.model.ValidicReadMoreRoutineRecordsModel;
import com.medcorp.nevo.network.validic.model.ValidicReadMoreSleepRecordsModel;
import com.medcorp.nevo.network.validic.model.ValidicRoutineRecord;
import com.medcorp.nevo.network.validic.model.ValidicRoutineRecordModel;
import com.medcorp.nevo.network.validic.model.ValidicRoutineRecordModelBase;
import com.medcorp.nevo.network.validic.model.ValidicSleepRecord;
import com.medcorp.nevo.network.validic.model.ValidicSleepRecordModel;
import com.medcorp.nevo.network.validic.model.ValidicSleepRecordModelBase;
import com.medcorp.nevo.network.validic.model.ValidicUser;
import com.medcorp.nevo.network.validic.model.VerifyCredentialModel;
import com.medcorp.nevo.network.validic.request.VerifyCredentialsRetroRequest;
import com.medcorp.nevo.network.validic.request.routine.AddRoutineRecordRequest;
import com.medcorp.nevo.network.validic.request.routine.DeleteRoutineRecordRequest;
import com.medcorp.nevo.network.validic.request.routine.GetMoreRoutineRecordsRequest;
import com.medcorp.nevo.network.validic.request.routine.UpdateRoutineRecordRequest;
import com.medcorp.nevo.network.validic.request.sleep.AddSleepRecordRequest;
import com.medcorp.nevo.network.validic.request.sleep.DeleteSleepRecordRequest;
import com.medcorp.nevo.network.validic.request.sleep.GetMoreSleepRecordsRequest;
import com.medcorp.nevo.network.validic.request.user.CreateUserRetroRequest;
import com.medcorp.nevo.util.Common;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.medcorp.library.ble.util.Optional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by med on 16/3/23.
 * this class do cloud sync between validic server and local database
 * when do cloud sync operation?
 * case1: syncController big/little sync done
 * case2: user login
 * case3: ???
 * IMPORTANT, IF TOO MANY RECORDS NEED SYNC, PERHAPS SPEND TOO LONG TIME,
 * START AN BACKGROUND TASK TO DO IT IS A GOOD IDEA.
 * how to have a cloud sync?
 * step1: sync local steps & sleep records if their Valid_record_ID is "0","0" is the default value in the steps/sleep local table
 * step2: read validic records between start_date = Date.now() - 365 and end_date =  Date.now(),every time get limit 100 records.
 * step3: check step2 return list, get those records that not found in local database, save them to local database.
 * step4: goto step2, change the page number,read next page until step2 return summary.next is null
 */
public class CloudSyncManager {
    private final String TAG = "CloudSyncManager";
    private Context context;
    final long INTERVAL_DATE = 365 * 24 * 60 * 60 *1000l;//user can get all data in a year
    private ValidicManager validicManager;
    public CloudSyncManager(Context context)
    {
        this.context = context;
        this.validicManager = new ValidicManager(context);
    }

    /**
     * when user login, invoke it
     */
    public void launchSyncAll(User user, List<Steps> stepsList, List<Sleep> sleepList){
        //step1:read local table "Steps" with validicRecordID = "0"
//         List<Steps> stepsList = getModel().getNeedSyncSteps(getModel().getNevoUser().getNevoUserID());
         for(Steps steps: stepsList)
         {
             addValidicRoutineRecord(user,steps,new Date(steps.getDate()),null);
         }
        //step2:do loop mass read
        //every mass read is 100 days
        Date endDate = Common.removeTimeFromDate(new Date());
        Date startDate = new Date(endDate.getTime() - INTERVAL_DATE);

        downloadSteps(startDate,endDate,1);

        //step4:change table to "Sleep" and repeat step1,2,3
//        List<Sleep> sleepList = getModel().getNeedSyncSleep(getModel().getNevoUser().getNevoUserID());
        for(Sleep sleep: sleepList)
        {
            addValidicSleepRecord(user, sleep, new Date(sleep.getDate()), null);
        }

        downloadSleep(startDate,endDate,1);
    }

    private void downloadSteps(final Date startDate, final Date endDate,final int page)
    {
        getMoreValidicRoutineRecord(startDate, endDate, page, new ResponseListener<ValidicReadMoreRoutineRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ValidicReadMoreRoutineRecordsModel validicReadMoreRoutineRecordsModel) {
                //step3:check read result is empty or some records aren't found in local table "Steps" and save them
                if (validicReadMoreRoutineRecordsModel.getSummary().getResults() > 0) {
                    for (ValidicRoutineRecordModelBase routine : validicReadMoreRoutineRecordsModel.getRoutine()) {
                        int activity_id = Integer.parseInt(routine.getActivity_id());
                        // if activity_id not exist in local Steps table, save it
                        if (!getModel().isFoundInLocalSteps(activity_id)) {
                            //save it
                            getModel().saveStepsFromValidic(routine);
                        }
                    }

                    if (validicReadMoreRoutineRecordsModel.getSummary().getNext() != null)
                    {
                        String nextPageUrl = validicReadMoreRoutineRecordsModel.getSummary().getNext();
                        int pageStart = nextPageUrl.indexOf("page=");
                        int pageEnd =  nextPageUrl.substring(pageStart).indexOf("&");
                        int nextPage = Integer.parseInt(nextPageUrl.substring(pageStart).substring(5, pageEnd));
                        downloadSteps(startDate, endDate, nextPage);
                    }

                }
            }
        });
    }

    private void downloadSleep(final Date startDate, final Date endDate,final int page)
    {
        getMoreValidicSleepRecord(startDate, endDate, page, new ResponseListener<ValidicReadMoreSleepRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel) {
                //step3:check read result is empty or some records aren't found in local table "Sleep" and save them
                if (validicReadMoreSleepRecordsModel.getSummary().getResults() > 0) {
                    for (ValidicSleepRecordModelBase sleep : validicReadMoreSleepRecordsModel.getSleep()) {
                        int activity_id = Integer.parseInt(sleep.getActivity_id());
                        // if activity_id not exist in local Sleep table, save it
                        if (!getModel().isFoundInLocalSleep(activity_id)) {
                            //save it
                            getModel().saveSleepFromValidic(sleep);
                        }
                    }

                    if (validicReadMoreSleepRecordsModel.getSummary().getNext() != null) {

                        String nextPageUrl = validicReadMoreSleepRecordsModel.getSummary().getNext();
                        int pageStart = nextPageUrl.indexOf("page=");
                        int pageEnd = nextPageUrl.substring(pageStart).indexOf("&");
                        int nextPage = Integer.parseInt(nextPageUrl.substring(pageStart).substring(5, pageEnd));
                        downloadSleep(startDate, endDate, nextPage);
                    }

                }
            }
        });
    }

    /**
     * when today's steps got change, invoke it
     */
    public void launchSyncDaily(User user, Steps steps)
    {
        addValidicRoutineRecord(user, steps,new Date(),null);
    }

    /**
     * when syncController big sync is done, invoke it
     */
    public void launchSyncWeekly(User user, List<Steps> stepsList, List<Sleep> sleepList)
    {
//        List<Steps> stepsList = getModel().getNeedSyncSteps(getModel().getNevoUser().getNevoUserID());
        for(Steps steps: stepsList)
        {
            addValidicRoutineRecord(user, steps,new Date(steps.getDate()),null);
        }

//        List<Sleep> sleepList = getModel().getNeedSyncSleep(getModel().getNevoUser().getNevoUserID());
        for(Sleep sleep: sleepList)
        {
            addValidicSleepRecord(user, sleep , new Date(sleep.getDate()), null);
        }
    }


    public void deleteValidicSleepRecord(final User user, final Sleep sleep, final Date date, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }
        String validicRecordID = sleep.getValidicRecordID();
        if(validicRecordID.equals("0")) {
            return;
        }
        DeleteSleepRecordRequest deleteRecordRequest = new DeleteSleepRecordRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(), user.getValidicUserID(), validicRecordID);

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
                    sleepDatabaseHelper.remove(user.getId(),date);
                }
                processListener(listener, validicDeleteSleepRecordModel);
            }
        });
    }


    public void getMoreValidicSleepRecord(User user, Date startDate,Date endDate,int page,final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }
        String startTimestamp = Common.getUTCTimestampFromLocalDate(startDate);
        String endTimeStamp = Common.getUTCTimestampFromLocalDate(endDate);

        GetMoreSleepRecordsRequest getMoreRecordsRequest = new GetMoreSleepRecordsRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(), user.getValidicUserID(),startTimestamp,endTimeStamp,page);

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


    //sleep operation functions:
    public void addValidicSleepRecord(final User user, final Sleep sleep, Date date,final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }

        ValidicSleepRecord record = new ValidicSleepRecord();
        record.setActivity_id("" + sleep.getiD());
        //validic sleep object , the value is  in seconds
        record.setAwake(60 * sleep.getTotalWakeTime());
        record.setLight(60 * sleep.getTotalLightTime());
        record.setDeep(60 * sleep.getTotalDeepTime());
        record.setTotal_sleep(60 * sleep.getTotalSleepTime());
        //TODO how to calculate the woken times? by hourly wake time is not zero?
        record.setTimes_woken(0);
        //REM value is set 0, nevo doesn't give this data
        record.setRem(0);
        NevoHourlySleepData nevoHourlySleepData = new NevoHourlySleepData();
        nevoHourlySleepData.setHourlyWake(sleep.getHourlyWake());
        nevoHourlySleepData.setHourlyLight(sleep.getHourlyLight());
        nevoHourlySleepData.setHourlyDeep(sleep.getHourlyDeep());
        record.setExtras(nevoHourlySleepData);

        String utc_offset = new SimpleDateFormat("z").format(date).substring(3);
        Date theDay = Common.removeTimeFromDate(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00+00:00");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp  = sdf.format(theDay);
        record.setTimestamp(timestamp);
        record.setUtc_offset(utc_offset);

        AddSleepRecordRequest addSleepRecordRequest = new AddSleepRecordRequest(record,validicManager.getOrganizationID(), validicManager.getOrganizationToken(),user.getValidicUserID());
        validicManager.execute(addSleepRecordRequest, new RequestListener<ValidicSleepRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                String causeString = spiceException.getCause()==null?"":spiceException.getCause().getLocalizedMessage()+"";
                if (causeString.contains("409")||causeString.contains("422"))
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
                    sleep.setValidicRecordID(validicSleepRecordModel.getSleep().get_id());
                    saveDailySleep(sleep);
                }
                processListener(listener, validicSleepRecordModel);
            }
        });
    }

    public void getValidicSleepRecord(User user, Date date,final ResponseListener listener)
    {
        getMoreValidicSleepRecord(user, date, date, 1, listener);
    }


    public void addValidicRoutineRecord(final User user, final Steps steps, final Date date, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }
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

        AddRoutineRecordRequest addRecordRequest = new AddRoutineRecordRequest(record,validicManager.getOrganizationID(),validicManager.getOrganizationToken(),user.getValidicUserID());
        validicManager.execute(addRecordRequest, new RequestListener<ValidicRoutineRecordModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                String causeString = spiceException.getCause()==null?"":spiceException.getCause().getLocalizedMessage()+"";
                if(causeString.contains("409") || causeString.contains("422"))
                {
                    //409:Activity is already taken
                    //422:Timestamp is already taken
                    updateValidicRoutineRecord(user, steps, date,listener);
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

    public void updateValidicRoutineRecord(final User user, final Steps steps, Date date, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }

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
                    UpdateRoutineRecordRequest updateRecordRequest = new UpdateRoutineRecordRequest(validicManager.getOrganizationID(), validicManager.getOrganizationToken(), user.getValidicUserID(), validicRecordID, steps.getSteps());
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

    public void getValidicRoutineRecord(User user, Date date,final ResponseListener listener)
    {
        getMoreValidicRoutineRecord(user, date, date,1,listener);
    }

    public void getMoreValidicRoutineRecord(User user, Date startDate, Date endDate, int page, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }

        String startTimestamp = Common.getUTCTimestampFromLocalDate(startDate);
        String endTimeStamps = Common.getUTCTimestampFromLocalDate(endDate);

        GetMoreRoutineRecordsRequest getMoreRecordsRequest = new GetMoreRoutineRecordsRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),user.getValidicUserID(),startTimestamp,endTimeStamps,page);

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

    public void deleteValidicRoutineRecord(final User user, Steps steps, final Date date, final ResponseListener listener)
    {
        if(user.isLogin()||!user.isConnectValidic()){
            return;
        }
        String validicRecordID = steps.getValidicRecordID();
        if(validicRecordID.equals("0")) {
            return;
        }

        DeleteRoutineRecordRequest deleteRecordRequest = new DeleteRoutineRecordRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken(),user.getValidicUserID(), validicRecordID);

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
                    // TODO event bus
//                    stepsDatabaseHelper.remove(nevoUserID,date);
                }
                processListener(listener, validicDeleteRecordModel);
            }
        });
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
    public void createValidicUser(final User nevoUser, String pinCode,final ResponseListener listener)
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
                //
                saveNevoUser(nevoUser);
                getSyncController().getDailyTrackerInfo(true);
                getCloudSyncManager().launchSyncAll();
                processListener(listener, validicUser);
            }
        });
    }
}
