package com.medcorp.nevo.ble.model.notification.visitor;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;

/**
 * Created by Karl on 10/7/15.
 */
public class NotificationDefaultColorVisitor implements NotificationVisitor<Integer> {
    @Override
    public Integer visit(CalendarNotification calendarNotification) {
        return R.drawable.red_indicator;
    }

    @Override
    public Integer visit(EmailNotification emailNotification) {
        return R.drawable.yellow_indicator;
    }

    @Override
    public Integer visit(FacebookNotification facebookNotification) {
        return R.drawable.blue_indicator;
    }

    @Override
    public Integer visit(SmsNotification smsNotification) {
        return R.drawable.green_indicator;
    }

    @Override
    public Integer visit(TelephoneNotification telephoneNotification) {
        return R.drawable.orange_indicator;
    }

    @Override
    public Integer visit(WeChatNotification weChatNotification) {
        return R.drawable.grass_green_indicator;
    }

    @Override
    public Integer visit(WhatsappNotification whatsappNotification) {
        return R.drawable.grass_green_indicator;
    }

    @Override
    public Integer visit(Notification applicationNotification) {
        return null;
    }
}
