package com.medcorp.nevo.ble.model.application;

import com.medcorp.nevo.ble.model.application.visitor.ApplicationLedVisitor;
import com.medcorp.nevo.ble.model.color.NevoLed;

/**
 * Created by Karl on 9/30/15.
 */
public class WhatsappColor implements ApplicationLed {

    private NevoLed led;
    private final String TAG = "whatsappchoosencolor";

    public WhatsappColor(NevoLed led) {
        this.led = led;
    }

    public WhatsappColor() {

    }

    @Override
    public NevoLed getLed() {
        return led;
    }

    @Override
    public String getTag() {
        return TAG;
    }
    @Override
    public void accept(ApplicationLedVisitor visitor) {
        visitor.visit(this);
    }
}
