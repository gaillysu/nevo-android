package com.medcorp.nevo.ble.model.application.visitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.medcorp.nevo.ble.model.application.ApplicationLed;
import com.medcorp.nevo.ble.model.application.CalendarColor;
import com.medcorp.nevo.ble.model.application.EmailColor;
import com.medcorp.nevo.ble.model.application.FacebookColor;
import com.medcorp.nevo.ble.model.application.SmsColor;
import com.medcorp.nevo.ble.model.application.TelephoneColor;
import com.medcorp.nevo.ble.model.application.WeChatColor;
import com.medcorp.nevo.ble.model.application.WhatsappColor;
import com.medcorp.nevo.ble.model.color.BlueLed;
import com.medcorp.nevo.ble.model.color.GreenLed;
import com.medcorp.nevo.ble.model.color.LightGreenLed;
import com.medcorp.nevo.ble.model.color.NevoLed;
import com.medcorp.nevo.ble.model.color.OrangeLed;
import com.medcorp.nevo.ble.model.color.RedLed;
import com.medcorp.nevo.ble.model.color.YellowLed;

/**
 * Created by Karl on 9/30/15.
 */
public class ColorGetter implements ApplicationLedVisitor<NevoLed> {

    private SharedPreferences pref;

    public ColorGetter(Context context) {
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public NevoLed visit(CalendarColor appColor) {
        return distinguish(pref.getInt(appColor.getTag(), new RedLed().getColor()));
    }

    @Override
    public NevoLed visit(EmailColor appColor) {
        return distinguish(pref.getInt(appColor.getTag(), new YellowLed().getColor()));
    }

    @Override
    public NevoLed visit(FacebookColor appColor) {
        return distinguish(pref.getInt(appColor.getTag(), new BlueLed().getColor()));
    }

    @Override
    public NevoLed visit(SmsColor appColor) {
        return distinguish(pref.getInt(appColor.getTag(), new GreenLed().getColor()));
    }

    @Override
    public NevoLed visit(TelephoneColor appColor) {
        return distinguish(pref.getInt(appColor.getTag(), new OrangeLed().getColor()));
    }

    @Override
    public NevoLed visit(WeChatColor appColor) {
        return distinguish(pref.getInt(appColor.getTag(), new LightGreenLed().getColor()));
    }

    @Override
    public NevoLed visit(WhatsappColor appColor) {
        return distinguish(pref.getInt(appColor.getTag(), new LightGreenLed().getColor()));
    }

    @Override
    public NevoLed visit(ApplicationLed appColor)
    {
        return null;
    }

    /* still need to find some better solution instead of this ugly switch.
    LED is based on
    BLUE_LED        = 0x010000
    GREEN_LED       = 0x100000
    YELLOW_LED      = 0x040000
    RED_LED         = 0x200000
    ORANGE_LED      = 0x080000
    LIGHTGREEN_LED  = 0x020000
    */
    private NevoLed distinguish(int ledColor){
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
