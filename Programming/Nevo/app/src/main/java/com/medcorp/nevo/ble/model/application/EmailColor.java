package com.medcorp.nevo.ble.model.application;

import com.medcorp.nevo.ble.model.application.visitor.ApplicationLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class EmailColor extends ApplicationLed {

    private final String TAG = "emailchoosencolor";

    public EmailColor() {

    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public <T> T accept(ApplicationLedVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
