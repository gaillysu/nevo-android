package com.medcorp.nevo.ble.model.color;

import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class BlueLed extends NevoLed{

    private final int COLOR = 0x010000;
    private final String TAG = "BLUE";

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
