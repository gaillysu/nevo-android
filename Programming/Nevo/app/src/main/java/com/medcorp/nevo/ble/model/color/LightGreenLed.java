package com.medcorp.nevo.ble.model.color;

import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class LightGreenLed extends NevoLed{

    private final String TAG = "LIGHT_GREEN";
    private final int COLOR = 0x020000;

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
