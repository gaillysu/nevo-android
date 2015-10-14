package com.medcorp.nevo.ble.model.notification;

import com.medcorp.nevo.ble.model.notification.visitor.NotificationVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class WhatsappNotification extends Notification {

    private final String ON_OFF_TAG = "whatsapp";
    private final String TAG = "whatsappchoosencolor";

    public WhatsappNotification() {
        super(false);
    }

    public WhatsappNotification(boolean state) {
        super(state);
    }

    @Override
    public String getOnOffTag() {
        return ON_OFF_TAG;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public <T> T accept(NotificationVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
