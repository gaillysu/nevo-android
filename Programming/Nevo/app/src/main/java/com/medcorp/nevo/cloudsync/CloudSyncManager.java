package com.medcorp.nevo.cloudsync;

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
 */
public class CloudSyncManager {
    private final String TAG = "CloudSyncManager";
    private Context context;

    public CloudSyncManager(Context context)
    {
        this.context = context;
    }

    public void launchSync(){

    }
}
