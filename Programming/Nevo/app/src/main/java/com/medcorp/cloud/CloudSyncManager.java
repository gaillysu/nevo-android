package com.medcorp.cloud;

import android.content.Context;
import android.util.Log;

import com.medcorp.event.validic.ValidicAddRoutineRecordEvent;
import com.medcorp.event.validic.ValidicAddSleepRecordEvent;
import com.medcorp.event.validic.ValidicDeleteRoutineRecordEvent;
import com.medcorp.event.validic.ValidicDeleteSleepRecordModelEvent;
import com.medcorp.event.validic.ValidicReadMoreRoutineRecordsModelEvent;
import com.medcorp.event.validic.ValidicReadMoreSleepRecordsModelEvent;
import com.medcorp.event.validic.ValidicUpdateRoutineRecordsModelEvent;
import com.medcorp.model.Sleep;
import com.medcorp.model.Steps;
import com.medcorp.model.User;
import com.medcorp.network.listener.ResponseListener;
import com.medcorp.network.validic.manager.ValidicManager;
import com.medcorp.network.validic.model.CreateUserRequestObject;
import com.medcorp.network.validic.model.CreateUserRequestObjectUser;
import com.medcorp.network.validic.model.NevoHourlySleepData;
import com.medcorp.network.validic.model.RoutineGoal;
import com.medcorp.network.validic.model.ValidicDeleteRoutineRecordModel;
import com.medcorp.network.validic.model.ValidicDeleteSleepRecordModel;
import com.medcorp.network.validic.model.ValidicReadMoreRoutineRecordsModel;
import com.medcorp.network.validic.model.ValidicReadMoreSleepRecordsModel;
import com.medcorp.network.validic.model.ValidicRoutineRecord;
import com.medcorp.network.validic.model.ValidicRoutineRecordModel;
import com.medcorp.network.validic.model.ValidicSleepRecord;
import com.medcorp.network.validic.model.ValidicSleepRecordModel;
import com.medcorp.network.validic.model.ValidicUser;
import com.medcorp.network.validic.model.VerifyCredentialModel;
import com.medcorp.network.validic.request.VerifyCredentialsRetroRequest;
import com.medcorp.network.validic.request.routine.AddRoutineRecordRequest;
import com.medcorp.network.validic.request.routine.DeleteRoutineRecordRequest;
import com.medcorp.network.validic.request.routine.GetMoreRoutineRecordsRequest;
import com.medcorp.network.validic.request.routine.UpdateRoutineRecordRequest;
import com.medcorp.network.validic.request.sleep.AddSleepRecordRequest;
import com.medcorp.network.validic.request.sleep.DeleteSleepRecordRequest;
import com.medcorp.network.validic.request.user.CreateUserRetroRequest;
import com.medcorp.util.Common;
import com.medcorp.event.validic.ValidicCreateUserEvent;
import com.medcorp.event.validic.ValidicException;
import com.medcorp.network.validic.request.sleep.GetMoreSleepRecordsRequest;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.greenrobot.eventbus.EventBus;

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
    final long INTERVAL_DATE = 365 * 24 * 60 * 60 *1000l;//user can get all data in a year
    private ValidicManager validicManager;

    public CloudSyncManager(Context context)
    {
        this.validicManager = new ValidicManager(context);
    }

    /**
     * when user login, invoke it
     */
    public void launchSyncAll(User user, List<Steps> stepsList, List<Sleep> sleepList){
         for(Steps steps: stepsList)
         {
             addValidicRoutineRecord(user,steps,new Date(steps.getDate()),null);
         }
        Date endDate = Common.removeTimeFromDate(new Date());
        Date startDate = new Date(endDate.getTime() - INTERVAL_DATE);

        downloadSteps(user,startDate,endDate,1);
        for(Sleep sleep: sleepList)
        {
            addValidicSleepRecord(user, sleep, new Date(sleep.getDate()), null);
        }

        downloadSleep(user, startDate,endDate,1);
    }

    private void downloadSteps(final User user, final Date startDate, final Date endDate,final int page)
    {
        getMoreValidicRoutineRecord(user, startDate, endDate, page, new ResponseListener<ValidicReadMoreRoutineRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicReadMoreRoutineRecordsModel validicReadMoreRoutineRecordsModel) {
                if (validicReadMoreRoutineRecordsModel.getSummary().getResults() > 0) {
                    EventBus.getDefault().post(new ValidicReadMoreRoutineRecordsModelEvent(validicReadMoreRoutineRecordsModel));
                    if (validicReadMoreRoutineRecordsModel.getSummary().getNext() != null) {
                        String nextPageUrl = validicReadMoreRoutineRecordsModel.getSummary().getNext();
                        int pageStart = nextPageUrl.indexOf("page=");
                        int pageEnd =  nextPageUrl.substring(pageStart).indexOf("&");
                        int nextPage = Integer.parseInt(nextPageUrl.substring(pageStart).substring(5, pageEnd));
                        downloadSteps(user,startDate, endDate, nextPage);
                    }
                }
            }
        });
    }


    private void downloadSleep(final User user, final Date startDate, final Date endDate,final int page)
    {
        getMoreValidicSleepRecord(user, startDate, endDate, page, new ResponseListener<ValidicReadMoreSleepRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel) {
                if (validicReadMoreSleepRecordsModel.getSummary().getResults() > 0) {
                    EventBus.getDefault().post(new ValidicReadMoreSleepRecordsModelEvent(validicReadMoreSleepRecordsModel));
                    if (validicReadMoreSleepRecordsModel.getSummary().getNext() != null) {
                        String nextPageUrl = validicReadMoreSleepRecordsModel.getSummary().getNext();
                        int pageStart = nextPageUrl.indexOf("page=");
                        int pageEnd = nextPageUrl.substring(pageStart).indexOf("&");
                        int nextPage = Integer.parseInt(nextPageUrl.substring(pageStart).substring(5, pageEnd));
                        downloadSleep(user, startDate, endDate, nextPage);
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
        for(Steps steps: stepsList)
        {
            addValidicRoutineRecord(user, steps,new Date(steps.getDate()),null);
        }

        for(Sleep sleep: sleepList)
        {
            addValidicSleepRecord(user, sleep , new Date(sleep.getDate()), null);
        }
    }

    private void deleteValidicSleepRecord(final User user, final Sleep sleep, final Date date, final ResponseListener listener)
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
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicDeleteSleepRecordModel validicDeleteSleepRecordModel) {
                if(validicDeleteSleepRecordModel.getCode().equals("200") || validicDeleteSleepRecordModel.getCode().equals("201"))
                {
                    EventBus.getDefault().post(new ValidicDeleteSleepRecordModelEvent(user.getId(),date));
                }

            }
        });
    }

    private void getMoreValidicSleepRecord(User user, Date startDate,Date endDate,int page,final ResponseListener listener)
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
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicReadMoreSleepRecordsModel validicReadMoreSleepRecordsModel) {
                // TODO catch this if needed.
                EventBus.getDefault().post(new ValidicReadMoreSleepRecordsModelEvent(validicReadMoreSleepRecordsModel));
//                processListener(listener, validicReadMoreSleepRecordsModel);
            }
        });
    }

    private void addValidicSleepRecord(final User user, final Sleep sleep, Date date,final ResponseListener listener)
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
                if (causeString.contains("409")||causeString.contains("422")) {
                    //DO NOTHING, sleep record can't be update
                }
                EventBus.getDefault().post(new ValidicException(spiceException));
            }
            @Override
            public void onRequestSuccess(ValidicSleepRecordModel validicSleepRecordModel) {
                if(validicSleepRecordModel.getCode().equals("200")||validicSleepRecordModel.getCode().equals("201"))
                {
                    sleep.setValidicRecordID(validicSleepRecordModel.getSleep().get_id());
                    EventBus.getDefault().post(new ValidicAddSleepRecordEvent(sleep));
                }
            }
        });
    }

    private void getValidicSleepRecord(User user, Date date,final ResponseListener listener)
    {
        getMoreValidicSleepRecord(user, date, date, 1, listener);
    }


    private void addValidicRoutineRecord(final User user, final Steps steps, final Date date, final ResponseListener listener)
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
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicRoutineRecordModel validicRecordModel) {
                if (validicRecordModel.getCode().equals("200") || validicRecordModel.getCode().equals("201"))
                {
                    //save validic record ID to local database, for using cloud sync
                    steps.setValidicRecordID(validicRecordModel.getRoutine().get_id());
                    EventBus.getDefault().post(new ValidicAddRoutineRecordEvent(steps));
                }
            }
        });
    }

    private void updateValidicRoutineRecord(final User user, final Steps steps, Date date, final ResponseListener listener)
    {
        if(!user.isLogin()||!user.isConnectValidic()){
            return;
        }

        getValidicRoutineRecord(user, date, new ResponseListener<ValidicReadMoreRoutineRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicReadMoreRoutineRecordsModel validicReadMoreRoutineRecordsModel) {
                if (validicReadMoreRoutineRecordsModel.getSummary().getResults() > 0) {
                    String validicRecordID = validicReadMoreRoutineRecordsModel.getRoutine()[0].get_id();
                    //if local data == cloud data, save record_id and return
                    if(steps.getSteps()==validicReadMoreRoutineRecordsModel.getRoutine()[0].getSteps())
                    {
                        steps.setValidicRecordID(validicReadMoreRoutineRecordsModel.getRoutine()[0].get_id());
                        EventBus.getDefault().post(new ValidicUpdateRoutineRecordsModelEvent(steps));
                        return;
                    }
                    UpdateRoutineRecordRequest updateRecordRequest = new UpdateRoutineRecordRequest(validicManager.getOrganizationID(), validicManager.getOrganizationToken(), user.getValidicUserID(), validicRecordID, steps.getSteps());
                    validicManager.execute(updateRecordRequest, new RequestListener<ValidicRoutineRecordModel>() {
                        @Override
                        public void onRequestFailure(SpiceException spiceException) {
                            EventBus.getDefault().post(new ValidicException(spiceException));
                        }

                        @Override
                        public void onRequestSuccess(ValidicRoutineRecordModel validicRecordModel) {
                            //save validic record ID to local database, for using cloud sync

                            steps.setValidicRecordID(validicRecordModel.getRoutine().get_id());
                            EventBus.getDefault().post(new ValidicUpdateRoutineRecordsModelEvent(steps));
                        }
                    });
                }
            }
        });
    }

    private void getValidicRoutineRecord(User user, Date date,final ResponseListener listener)
    {
        getMoreValidicRoutineRecord(user, date, date,1,listener);
    }

    private void getMoreValidicRoutineRecord(User user, Date startDate, Date endDate, int page, final ResponseListener listener)
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
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicReadMoreRoutineRecordsModel validicReadAllRecordsModel) {
                EventBus.getDefault().post(new ValidicReadMoreRoutineRecordsModelEvent(validicReadAllRecordsModel));
            }
        });
    }

    private void deleteValidicRoutineRecord(final User user, Steps steps, final Date date, final ResponseListener listener)
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
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicDeleteRoutineRecordModel validicDeleteRecordModel) {
                if(validicDeleteRecordModel.getCode().equals("200") || validicDeleteRecordModel.getCode().equals("201"))
                {
                    EventBus.getDefault().post(new ValidicDeleteRoutineRecordEvent(user.getId(), date));
                }
            }
        });
    }


    private void verifyValidicCredential()
    {
        VerifyCredentialsRetroRequest request = new VerifyCredentialsRetroRequest(validicManager.getOrganizationID(),validicManager.getOrganizationToken());
        validicManager.execute(request, new RequestListener<VerifyCredentialModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
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
                EventBus.getDefault().post(new ValidicException(spiceException));
            }

            @Override
            public void onRequestSuccess(ValidicUser validicUser) {
                nevoUser.setValidicUserID(validicUser.getUser().get_id());
                nevoUser.setValidicUserToken(validicUser.getUser().getAccess_token());
                nevoUser.setIsConnectValidic(true);
                EventBus.getDefault().post(new ValidicCreateUserEvent(nevoUser));
            }
        });
    }
}
