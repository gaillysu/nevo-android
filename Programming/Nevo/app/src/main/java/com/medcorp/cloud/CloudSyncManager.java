package com.medcorp.cloud;

import android.content.Context;
import android.util.Log;

import com.medcorp.cloud.validic.ValidicOperation;
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

    private Context context;
    public CloudSyncManager(Context context)
    {
        this.context = context;
    }

    /**
     * when user login, invoke it
     */
    public void launchSyncAll(User user, List<Steps> stepsList, List<Sleep> sleepList){
         for(Steps steps: stepsList)
         {
             ValidicOperation.getInstance(context).addValidicRoutineRecord(user,steps,new Date(steps.getDate()),null);
         }
        Date endDate = Common.removeTimeFromDate(new Date());
        Date startDate = new Date(endDate.getTime() - INTERVAL_DATE);

        downloadSteps(user,startDate,endDate,1);
        for(Sleep sleep: sleepList)
        {
            ValidicOperation.getInstance(context).addValidicSleepRecord(user, sleep, new Date(sleep.getDate()), null);
        }

        downloadSleep(user, startDate,endDate,1);
    }

    private void downloadSteps(final User user, final Date startDate, final Date endDate,final int page)
    {
        ValidicOperation.getInstance(context).getMoreValidicRoutineRecord(user, startDate, endDate, page, new ResponseListener<ValidicReadMoreRoutineRecordsModel>() {
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
        ValidicOperation.getInstance(context).getMoreValidicSleepRecord(user, startDate, endDate, page, new ResponseListener<ValidicReadMoreSleepRecordsModel>() {
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
        ValidicOperation.getInstance(context).addValidicRoutineRecord(user, steps,new Date(),null);
    }

    /**
     * when syncController big sync is done, invoke it
     */
    public void launchSyncWeekly(User user, List<Steps> stepsList, List<Sleep> sleepList)
    {
        for(Steps steps: stepsList)
        {
            ValidicOperation.getInstance(context).addValidicRoutineRecord(user, steps,new Date(steps.getDate()),null);
        }

        for(Sleep sleep: sleepList)
        {
            ValidicOperation.getInstance(context).addValidicSleepRecord(user, sleep , new Date(sleep.getDate()), null);
        }
    }


}
