package com.medcorp.nevo.application;

import android.app.Application;
import android.util.Log;

/**
 * Created by Karl on 10/15/15.
 */
public class ApplicationModel extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("Karl","On create app model");
    }
}
