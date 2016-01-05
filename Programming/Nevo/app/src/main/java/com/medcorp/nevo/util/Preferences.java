package com.medcorp.nevo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.Preference;

import com.medcorp.nevo.model.Preset;

/**
 * Created by karl-john on 29/12/15.
 */
public class Preferences {

    private static SharedPreferences preferences;

    public static void savePreset(Context context, Preset preset){
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("steps_preset_id", preset.getId());
        editor.commit();
    }

    public static int getPresetId(Context context){
        init(context);
        return preferences.getInt("steps_preset_id", -1);
    }

    private static void init(Context context){
        if (preferences == null){
            preferences= context.getSharedPreferences("Nevo_Shared_Preferences",Context.MODE_PRIVATE);
        }
    }

    public static boolean getLinklossNotification(Context context)
    {
        init(context);
        return preferences.getBoolean("link_loss_enable",false);
    }
    public static void saveLinklossNotification(Context context,boolean isEnable)
    {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("link_loss_enable",isEnable);
        editor.commit();
    }
}
