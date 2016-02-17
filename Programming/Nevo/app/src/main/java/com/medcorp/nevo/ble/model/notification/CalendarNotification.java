package com.medcorp.nevo.ble.model.notification;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.model.color.NevoLed;
import com.medcorp.nevo.ble.model.color.RedLed;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class CalendarNotification extends Notification {

    private final String TAG = "calchoosencolor";
    private final String ON_OFF_TAG = "calendar";

    public CalendarNotification() {
        super(false);
    }

    public CalendarNotification(boolean state) {
        super(state);
    }


    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getOnOffTag() {
        return ON_OFF_TAG;
    }

    @Override
    public int getStringResource() {
        return R.string.calendar_string;
    }

    @Override
    public int getImageResource() {
        return R.drawable.calendar_notification;
    }

    @Override
    public NevoLed getDefaultColor() {
        return new RedLed();
    }

    @Override
    public <T> T accept(NotificationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
