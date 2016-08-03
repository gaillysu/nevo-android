package com.medcorp.ble.datasource;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.medcorp.ble.model.notification.Notification;

/**
 * Created by Karl on 10/6/15.
 */
public class NotificationDataHelper {

    SharedPreferences pref;

    public NotificationDataHelper(Context context) {
        pref =PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveState(Notification applicationNotification){
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(applicationNotification.getOnOffTag(), applicationNotification.isOn());
        editor.commit();
    }

    public Notification getState(Notification applicationNotification){
        applicationNotification.setState(pref.getBoolean(applicationNotification.getOnOffTag(),false));
        return applicationNotification;
    }
}