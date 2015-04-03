package com.nevowatch.nevo.Function;

/**
 * Created by imaze on 4/3/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveData {
    private static final String PREF_KEY_STEP_GOAL = "stepGoal";
    private static final String PREF_KEY_ALARM = "alarm";
    private static final String PREF_KEY_CLOCK_STATE = "clockState";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    public static void saveStepGoalToPreference(Context context, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(PREF_KEY_STEP_GOAL, value).apply();
    }

    public static String getStepGoalFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_KEY_STEP_GOAL, "7000");
    }

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
}
