package com.medcorp.nevo.cloud;

import android.content.Context;

import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.dao.StepsDAO;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.network.listener.ResponseListener;
import com.medcorp.nevo.util.Common;
import com.medcorp.nevo.validic.model.routine.ValidicReadMoreRoutineRecordsModel;
import com.medcorp.nevo.validic.model.routine.ValidicRoutineRecordModelBase;
import com.octo.android.robospice.persistence.exception.SpiceException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * step2: read validic records between start_date = Date.now() - 100 and end_date =  Date.now()
 * step3: check step2 return list, get those records that not found in local database, save them to local database.
 * step4: goto step2, change start_date = Date.now() - 200 and end_date = Date.now() - 100,do next read until step2 return empty records
 */
public class CloudSyncManager {
    private final String TAG = "CloudSyncManager";
    private Context context;

    public CloudSyncManager(Context context)
    {
        this.context = context;
    }
    private ApplicationModel getModel() {return (ApplicationModel)context;}

    public void launchSync(){
        //step1:read local table "Steps" with validicRecordID = "0"
         List<Steps> stepsList = getModel().getNeedSyncSteps(getModel().getNevoUser().getNevoUserID());
         for(Steps steps: stepsList)
         {
             getModel().addValidicRoutineRecord(getModel().getNevoUser().getNevoUserID(),new Date(steps.getDate()),null);
         }
        //step2:do loop mass read
        //every mass read is 100 days
        Date endDate = Common.removeTimeFromDate(new Date());
        Date startDate = new Date(Common.removeTimeFromDate(endDate).getTime() - 99*24*60*60*1000);

        getModel().getMoreValidicRoutineRecord(startDate, endDate, new ResponseListener<ValidicReadMoreRoutineRecordsModel>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(ValidicReadMoreRoutineRecordsModel validicReadMoreRoutineRecordsModel) {
                if(validicReadMoreRoutineRecordsModel.getSummary().getResults()>0)
                {
                    for(ValidicRoutineRecordModelBase routine:validicReadMoreRoutineRecordsModel.getRoutine())
                    {
                        int activity_id = Integer.parseInt(routine.getActivity_id());
                        // if activity_id not exist in local Steps table, save it
                        if(!getModel().isFoundInLocalSteps(activity_id))
                        {
                            //save it
                            getModel().saveStepsFromValidic(routine);
                        }
                    }

                }
            }
        });
        //step3:check read result is empty or some records aren't found in local table "Steps" and save them

        //step4:change table to "Sleep" and repeat step1,2,3

    }
}
