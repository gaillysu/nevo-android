package com.medcorp.nevo.ble.model.application;

import com.medcorp.nevo.ble.model.application.visitor.ApplicationLedVisitor;
import com.medcorp.nevo.ble.model.color.NevoLed;

/**
 * Created by Karl on 9/30/15.
 */
public class CalendarColor implements ApplicationLed {

    private NevoLed led;
    private final String TAG = "calchoosencolor";

    public CalendarColor(NevoLed led) {
        this.led = led;
    }

    public CalendarColor() {

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
