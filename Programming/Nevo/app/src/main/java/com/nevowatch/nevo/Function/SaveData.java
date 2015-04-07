package com.nevowatch.nevo.Function;

/**
 * Created by imaze on 4/3/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveData {
    private static final String PREF_KEY_STEP_GOAL = "stepGoal";
    private static final String PREF_KEY_STEP_MODE = "stepMode";
    private static final String PREF_KEY_ALARM = "alarm";
    private static final String PREF_KEY_CLOCK_STATE = "clockState";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String PREF_USER_HOUR_DEGREE = "hour_pointer_degree";
    private static final String PREF_USER_MINUTE_DEGREE = "minute_pointer_degree";
    private static final String PREF_USER_BLE_CONNECT = "bluetooth_connect";
    private static final String PREF_USER_FIRST_LAUNCH = "first_launch";

    /**
     * Goal Fragment
     * */
    public static void saveStepGoalToPreference(Context context, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(PREF_KEY_STEP_GOAL, value).apply();
    }

    public static String getStepGoalFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_KEY_STEP_GOAL, "7000");
    }

    public static void saveGoalModeToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(PREF_KEY_STEP_MODE, value).apply();
    }

    public static int getGoalModeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(PREF_KEY_STEP_MODE, -1);
    }

    /**
    * Alarm Fragment
    * */
    public static void saveAlarmToPreference(Context context, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(PREF_KEY_ALARM, value).apply();
    }

    public static String getAlarmFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_KEY_ALARM, "00:00");
    }

    public static void saveClockStateToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(PREF_KEY_CLOCK_STATE, value).apply();
    }

    public static Boolean getClockStateFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_KEY_CLOCK_STATE, false);
    }

    /**
     * Welcome Fragment saves hour and minute pointer degree
     * */
    public static void saveHourDegreeToPreference(Context context, float value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putFloat(PREF_USER_HOUR_DEGREE, value).apply();
    }

    public static Float getHourDegreeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getFloat(PREF_USER_HOUR_DEGREE, 0);
    }

    public static void saveMinDegreeToPreference(Context context, float value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putFloat(PREF_USER_MINUTE_DEGREE, value).apply();
    }

    public static Float getMinDegreeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getFloat(PREF_USER_MINUTE_DEGREE, 0);
    }

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    public static void saveUserLearnToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(PREF_USER_LEARNED_DRAWER, value).apply();
    }

    public static Boolean getUserLearnFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }

    /**
     * Bluetooth Connect State
     * */
    public static void saveBleConnectToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(PREF_USER_BLE_CONNECT, value).apply();
    }

    public static Boolean getBleConnectFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_USER_BLE_CONNECT, false);
    }
    /**
     * Set Tag for Frist Launch application
     * */
    public static void saveFirstLaunchtToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(PREF_USER_FIRST_LAUNCH, value).apply();
    }

    public static Boolean getFirstLaunchFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_USER_FIRST_LAUNCH, false);
    }
  }
