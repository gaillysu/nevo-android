package com.medcorp.nevo.ble.model.application;

import com.medcorp.nevo.ble.model.application.visitor.ApplicationLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class FacebookColor extends ApplicationLed {

    private final String TAG = "facechoosencolor";


    public FacebookColor() {

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
