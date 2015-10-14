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
public class NotificationColorGetter implements NotificationVisitor<NevoLed> {

    private SharedPreferences pref;

    public NotificationColorGetter(Context context) {
        this.pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public NevoLed visit(CalendarNotification calendarNotification) {
        return distinguish(pref.getInt(calendarNotification.getTag(), new RedLed().getColor()));
    }

    @Override
    public NevoLed visit(EmailNotification emailNotification) {
        return distinguish(pref.getInt(emailNotification.getTag(), new YellowLed().getColor()));
    }

    @Override
    public NevoLed visit(FacebookNotification facebookNotification) {
        return distinguish(pref.getInt(facebookNotification.getTag(), new BlueLed().getColor()));
    }

    @Override
    public NevoLed visit(SmsNotification smsNotification) {
        return distinguish(pref.getInt(smsNotification.getTag(), new GreenLed().getColor()));
    }

    @Override
    public NevoLed visit(TelephoneNotification telephoneNotification) {
        return distinguish(pref.getInt(telephoneNotification.getTag(), new OrangeLed().getColor()));
    }

    @Override
    public NevoLed visit(WeChatNotification weChatNotification) {
        return distinguish(pref.getInt(weChatNotification.getTag(), new LightGreenLed().getColor()));
    }

    @Override
    public NevoLed visit(WhatsappNotification whatsappNotification) {
        return distinguish(pref.getInt(whatsappNotification.getTag(), new LightGreenLed().getColor()));
    }

    @Override
    public NevoLed visit(Notification applicationNotification)
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
