package com.medcorp.nevo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;

import com.medcorp.nevo.model.Preset;

/**
 * Created by karl-john on 29/12/15.
 */
public class Preferences {

    private static SharedPreferences preferences;

    //TODO save to keys in key.xml
    private static final String PREF_KEY_CLOCK_STATE = "clockState";
    private static final String PREF_KEY_CLOCK_STATE2 = "clockState2";
    private static final String PREF_KEY_CLOCK_STATE3 = "clockState3";
    private static final String PREF_KEY_STEP_MODE = "stepMode";
    private static final int CUSTOM = -1;

    private static final String PREF_KEY_ALARM = "alarm";
    private static final String PREF_KEY_ALARM2 = "alarm2";
    private static final String PREF_KEY_ALARM3 = "alarm3";

    public static void savePreset(Context context, Preset preset){
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        //TODO put into keys.xml if it is still used
        editor.putInt("steps_preset_id", preset.getId());
        editor.commit();
    }

    public static int getPresetId(Context context){
        init(context);
        //TODO put into keys.xml if it is still used
        return preferences.getInt("steps_preset_id", -1);
    }

    private static void init(Context context){
        if (preferences == null){
            //TODO put into keys.xml if it is still used
            preferences= context.getSharedPreferences("Nevo_Shared_Preferences",Context.MODE_PRIVATE);
        }
    }

    public static boolean getLinklossNotification(Context context)
    {
        init(context);
        //TODO put into keys.xml if it is still used
        return preferences.getBoolean("link_loss_enable", false);
    }
    public static void saveLinklossNotification(Context context,boolean isEnable)
    {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        //TODO put into keys.xml if it is still used
        editor.putBoolean("link_loss_enable",isEnable);
        editor.commit();
    }

    public static void saveClockStateToPreference(int index,Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(index == 0) pref.edit().putBoolean(PREF_KEY_CLOCK_STATE, value).apply();
        if(index == 1) pref.edit().putBoolean(PREF_KEY_CLOCK_STATE2, value).apply();
        if(index == 2) pref.edit().putBoolean(PREF_KEY_CLOCK_STATE3, value).apply();
    }

    public static Boolean getClockStateFromPreference(int index,Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(index == 0) return pref.getBoolean(PREF_KEY_CLOCK_STATE, false);
        if(index == 1) return pref.getBoolean(PREF_KEY_CLOCK_STATE2, false);
        if(index == 2) return pref.getBoolean(PREF_KEY_CLOCK_STATE3, false);
        return false;
    }

    public static void saveGoalModeToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(PREF_KEY_STEP_MODE, value).apply();
    }

    public static int getGoalModeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(PREF_KEY_STEP_MODE, CUSTOM);
    }


    public static void saveAlarmToPreference(int index,Context context, String value) {
        SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        if(index == 0) pref.edit().putString(PREF_KEY_ALARM, value).apply();
        if(index == 1) pref.edit().putString(PREF_KEY_ALARM2, value).apply();
        if(index == 2) pref.edit().putString(PREF_KEY_ALARM3, value).apply();
    }

    public static String getAlarmFromPreference(int index,Context context) {
        SharedPreferences pref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        if(index == 0)  return pref.getString(PREF_KEY_ALARM, "00:00");
        if(index == 1)  return pref.getString(PREF_KEY_ALARM2, "00:00");
        if(index == 2)  return pref.getString(PREF_KEY_ALARM3, "00:00");
        else return "00:00";
    }
}
