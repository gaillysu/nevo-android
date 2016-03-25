package com.medcorp.nevo.cloud;

import android.content.Context;

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

    public void launchSync(){
        //step1:read local table "Steps" with validicRecordID = "0"

        //step2:do loop mass read

        //step3:check read result is empty or some records aren't found in local table "Steps" and save them

        //step4:change table to "Sleep" and repeat step1,2,3

    }
}
