package com.medcorp.nevo.ble.model.color;

import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class RedLed extends NevoLed{

    private final String TAG = "RED";
    private final int COLOR = 0x200000;

    @Override
    public int getColor() {
        return COLOR;
    }

    @Override
    public String getTag() {
        return TAG;
    }


    @Override
    public void accept(NevoLedVisitor visitor) {
        visitor.visit(this);
    }
}
