package com.medcorp.nevo.ble.model.notification.visitor;

import android.content.Context;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;

/**
 * Created by gaillysu on 16/1/4.
 */
public class NotificationNameVisitor implements NotificationVisitor<String> {
    private Context context;

    public NotificationNameVisitor(Context context)
    {
        this.context = context;
    }

    @Override
    public String visit(CalendarNotification calendarNotification) {
        return context.getResources().getString(R.string.calendar_string);
    }

    @Override
    public String visit(EmailNotification emailNotification) {
        return context.getResources().getString(R.string.email_string);
    }

    @Override
    public String visit(FacebookNotification facebookNotification) {
        return context.getResources().getString(R.string.facebook_string);
    }

    @Override
    public String visit(SmsNotification smsNotification) {
        return context.getResources().getString(R.string.sms_string);
    }

    @Override
    public String visit(TelephoneNotification telephoneNotification) {
        return context.getResources().getString(R.string.call_string);
    }

    @Override
    public String visit(WeChatNotification weChatNotification) {
        return context.getResources().getString(R.string.wechat_string);
    }

    @Override
    public String visit(WhatsappNotification whatsappNotification) {
        return context.getResources().getString(R.string.whatsapp_string);
    }

    @Override
    public String visit(Notification applicationNotification) {
        return null;
    }
}