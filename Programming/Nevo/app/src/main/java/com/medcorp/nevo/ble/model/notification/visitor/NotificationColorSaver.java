package com.medcorp.nevo.ble.model.notification.visitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;
import com.medcorp.nevo.ble.model.color.NevoLed;

/**
 * Created by Karl on 9/30/15.
 */
public class NotificationColorSaver implements NotificationVisitor<Void> {

    private SharedPreferences pref;
    private NevoLed led;

    public NotificationColorSaver(Context context, NevoLed led) {
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
        this.led = led;
    }

    public void setLed(NevoLed led) {
        this.led = led;
    }

    @Override
    public Void visit(CalendarNotification calendarNotification) {
        pref.edit().putInt(calendarNotification.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(EmailNotification emailNotification) {
        pref.edit().putInt(emailNotification.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(FacebookNotification facebookNotification) {
        pref.edit().putInt(facebookNotification.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(SmsNotification smsNotification) {
        pref.edit().putInt(smsNotification.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(TelephoneNotification telephoneNotification) {
        pref.edit().putInt(telephoneNotification.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(WeChatNotification weChatNotification) {
        pref.edit().putInt(weChatNotification.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(WhatsappNotification whatsappNotification) {
        pref.edit().putInt(whatsappNotification.getTag(), led.getColor()).apply();
        return null;
    }

    @Override
    public Void visit(Notification applicationNotification)
    {
        pref.edit().putInt(applicationNotification.getTag(), led.getColor()).apply();
        return null;
    }
}
