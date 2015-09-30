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
import com.medcorp.nevo.ble.model.color.NevoLed;

/**
 * Created by Karl on 9/30/15.
 */
public class ColorSaver implements ApplicationLedVisitor<Void> {

    private SharedPreferences pref;
    private NevoLed led;

    public ColorSaver(Context context, NevoLed led) {
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.led = led;
    }

    public void setLed(NevoLed led) {
        this.led = led;
    }

    @Override
    public Void visit(CalendarColor appColor) {
        pref.edit().putInt(appColor.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(EmailColor appColor) {
        pref.edit().putInt(appColor.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(FacebookColor appColor) {
        pref.edit().putInt(appColor.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(SmsColor appColor) {
        pref.edit().putInt(appColor.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(TelephoneColor appColor) {
        pref.edit().putInt(appColor.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(WeChatColor appColor) {
        pref.edit().putInt(appColor.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(WhatsappColor appColor) {
        pref.edit().putInt(appColor.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(ApplicationLed appColor)
    {
        pref.edit().putInt(appColor.getTag(), led.getColor()).apply();
        return null;
    }
}
