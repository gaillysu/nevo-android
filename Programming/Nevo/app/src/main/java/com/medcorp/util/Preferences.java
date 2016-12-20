package com.medcorp.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.medcorp.ApplicationFlag;
import com.medcorp.R;
import com.medcorp.application.ApplicationModel;
import com.medcorp.ble.model.color.BlueLed;
import com.medcorp.ble.model.color.GreenLed;
import com.medcorp.ble.model.color.LightGreenLed;
import com.medcorp.ble.model.color.NevoLed;
import com.medcorp.ble.model.color.OrangeLed;
import com.medcorp.ble.model.color.RedLed;
import com.medcorp.ble.model.color.YellowLed;
import com.medcorp.ble.model.notification.Notification;
import com.medcorp.model.Goal;

/**
 * Created by karl-john on 29/12/15.
 */
public class Preferences {

    private static SharedPreferences preferences;

    private static void init(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(context.getString(R.string.key_prefs_nevo_shared_pref), Context.MODE_PRIVATE);
        }
    }

    public static void saveUserHeardPicture(Context context, String userEmail, String picturePath) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(userEmail, picturePath).apply();
    }

    public static String getUserHeardPicturePath(Context context, String userEmail) {
        init(context);
        return preferences.getString(userEmail, null);
    }

    public static void saveUserHomeCityName(Context context, int cityId) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.key_prefs_save_other_name), cityId).apply();
    }

    public static int getSaveHomeCityName(Context context) {
        init(context);
        return preferences.getInt(context.getString(R.string.key_prefs_save_other_name), -1);
    }

    public static void saveSelectDate(Context context, String selectDate) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.key_prefs_select_date), selectDate).apply();
    }

    public static String getSelectDate(Context context) {
        init(context);
        return preferences.getString(context.getString(R.string.key_prefs_select_date),
                null);
    }

    public static void saveIsFirstLogin(Context context, boolean isNotFirst) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getString(R.string.key_prefs_is_first_login), isNotFirst).apply();
    }

    public static void saveUnitSelect(Context context, boolean isMetrics) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getString(R.string.key_prefs_is_metrics), isMetrics).apply();
    }

    public static boolean getUnitSelect(Context context) {
        init(context);
        return preferences.getBoolean(context.getString(R.string.key_prefs_is_metrics),false);
    }

    public static void startInitAlarm(Context context, boolean isInit) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getString(R.string.key_init_alarm), isInit);
    }

    public static boolean getisInitAlarm(Context context) {
        init(context);
        return preferences.getBoolean(context.getString(R.string.key_init_alarm), true);
    }

    public static boolean getIsFirstLogin(Context context) {
        init(context);
        return preferences.getBoolean(context.getString(R.string.key_prefs_is_first_login), true);
    }

    public static void savePreset(Context context, Goal goal) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.key_prefs_step_preset_id), goal.getId()).apply();
    }

    public static int getPresetId(Context context) {
        init(context);
        return preferences.getInt(context.getString(R.string.key_prefs_step_preset_id), -1);
    }

    public static boolean getLinklossNotification(Context context) {
        init(context);
        return preferences.getBoolean(context.getString(R.string.key_prefs_link_loss), false);
    }

    public static void saveLinklossNotification(Context context, boolean isEnable) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getString(R.string.key_prefs_link_loss), isEnable).apply();

    }

    public static void setGoogleFit(Context context, boolean on) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getString(R.string.key_prefs_google_fit), on).apply();
    }

    public static boolean isGoogleFitSet(Context context) {
        init(context);
        return preferences.getBoolean(context.getString(R.string.key_prefs_google_fit), false);
    }

    public static void saveNotificationColor(Context context, Notification notification, int ledColor) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(notification.getTag(), ledColor).apply();
    }

    public static NevoLed getNotificationColor(Context context, Notification notification, ApplicationModel model) {
        init(context);
        if (ApplicationFlag.FLAG == ApplicationFlag.Flag.NEVO) {
            return distinguish(preferences.getInt(notification.getTag(), notification.getDefaultColor().getHexColor()));
        } else {
            return distinguish(preferences.getInt(notification.getTag(), notification.getDefaultColor().getHexColor()), model);
        }
    }

    public static NevoLed getNotificationColor(Context context, Notification notification) {
        init(context);
        return distinguish(preferences.getInt(notification.getTag(), notification.getDefaultColor().getHexColor()));
    }

    public static void setProfileUnit(Context context, int unit) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("profile_unit", unit).apply();
    }

    public static int getProfileUnit(Context context) {
        init(context);
        return preferences.getInt("profile_unit", 0);
    }

    public static void setWatchId(Context context, int id) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.key_prefs_watch_id), id).apply();
    }

    public static int getWatchId(Context context) {
        init(context);
        return preferences.getInt(context.getString(R.string.key_prefs_watch_id), 1);
    }

    public static void setWatchModel(Context context, int model) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(context.getString(R.string.key_prefs_watch_model), model).apply();
    }

    public static int getWatchModel(Context context) {
        init(context);
        return preferences.getInt(context.getString(R.string.key_prefs_watch_model), 1);
    }

    private static NevoLed distinguish(int ledColor) {
        switch (ledColor) {
            case 0x100000:
                return new GreenLed();
            case 0x020000:
                return new LightGreenLed();
            case 0x080000:
                return new OrangeLed();
            case 0x010000:
                return new BlueLed();
            case 0x040000:
                return new YellowLed();
            case 0x200000:
                return new RedLed();
        }
        return null;
    }

    private static NevoLed distinguish(int ledColor, ApplicationModel model) {
        return model.getUserSelectLedLamp(ledColor);
    }
}
