package com.medcorp.nevo.ble.model.notification.visitor;

import android.content.Context;
import android.graphics.drawable.Drawable;

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
public class NotificationIconVisitor implements NotificationVisitor<Drawable> {
    private Context context;

    public NotificationIconVisitor(Context context)
    {
        this.context = context;
    }

    @Override
    public Drawable visit(CalendarNotification calendarNotification) {
        return context.getResources().getDrawable(R.drawable.calendar_notification);
    }

    @Override
    public Drawable visit(EmailNotification emailNotification) {
        return context.getResources().getDrawable(R.drawable.email_notification);
    }

    @Override
    public Drawable visit(FacebookNotification facebookNotification) {
        return context.getResources().getDrawable(R.drawable.facebook_notification);
    }

    @Override
    public Drawable visit(SmsNotification smsNotification) {
        return context.getResources().getDrawable(R.drawable.message_notification);
    }

    @Override
    public Drawable visit(TelephoneNotification telephoneNotification) {
        return context.getResources().getDrawable(R.drawable.call_notification);
    }

    @Override
    public Drawable visit(WeChatNotification weChatNotification) {
        return context.getResources().getDrawable(R.drawable.wechart_notification);
    }

    @Override
    public Drawable visit(WhatsappNotification whatsappNotification) {
        return context.getResources().getDrawable(R.drawable.whatsapp_notification);
    }

    @Override
    public Drawable visit(Notification applicationNotification) {
        return null;
    }
}
