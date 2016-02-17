package com.medcorp.nevo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.medcorp.nevo.ble.model.color.BlueLed;
import com.medcorp.nevo.ble.model.color.GreenLed;
import com.medcorp.nevo.ble.model.color.LightGreenLed;
import com.medcorp.nevo.ble.model.color.NevoLed;
import com.medcorp.nevo.ble.model.color.OrangeLed;
import com.medcorp.nevo.ble.model.color.RedLed;
import com.medcorp.nevo.ble.model.color.YellowLed;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.model.Preset;

/**
 * Created by karl-john on 29/12/15.
 */
public class Preferences {

    private static SharedPreferences preferences;

    private static void init(Context context){
        if (preferences == null){
            //TODO put into keys.xml if it is still used
            preferences= context.getSharedPreferences("Nevo_Shared_Preferences",Context.MODE_PRIVATE);
        }
    }

    public static void savePreset(Context context, Preset preset){
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        //TODO put into keys.xml if it is still used
        editor.putInt("steps_preset_id", preset.getId()).apply();
    }

    public static int getPresetId(Context context){
        init(context);
        //TODO put into keys.xml if it is still used
        return preferences.getInt("steps_preset_id", -1);
    }



    public static boolean getLinklossNotification(Context context)
    {
        init(context);
        //TODO put into keys.xml if it is still used
        return preferences.getBoolean("link_loss_enable", false);
    }
    public static void saveLinklossNotification(Context context,boolean isEnable) {
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        //TODO put into keys.xml if it is still used
        editor.putBoolean("link_loss_enable", isEnable).apply();

    }

    public static void saveNotificationColor(Context context, Notification notification, NevoLed led){
        init(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(notification.getTag(), led.getHexColor()).apply();
    }

    public static NevoLed getNotificationColor(Context context, Notification notification){
        return distinguish(preferences.getInt(notification.getTag(), notification.getDefaultColor().getHexColor()));
    }

    private static NevoLed distinguish(int ledColor){
        switch (ledColor){
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
}
