package com.medcorp.nevo.ble.model.color;

import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class OrangeLed extends NevoLed{

    private final String TAG = "ORANGE";
    private final int COLOR = 0x080000;

    @Override
    public int getColor() {
        return COLOR;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public <T> T accept(NevoLedVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
