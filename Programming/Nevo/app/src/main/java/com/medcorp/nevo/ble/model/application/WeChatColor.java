package com.medcorp.nevo.ble.model.application;

import com.medcorp.nevo.ble.model.application.visitor.ApplicationLedVisitor;
import com.medcorp.nevo.ble.model.color.NevoLed;

/**
 * Created by Karl on 9/30/15.
 */
public class WeChatColor implements ApplicationLed {

    private NevoLed led;
    private final String TAG = "wechatchoosencolor";

    public WeChatColor(NevoLed led) {
        this.led = led;
    }

    public WeChatColor() {

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
