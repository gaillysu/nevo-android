package com.medcorp.nevo.ble.model.color;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class GreenLed extends NevoLed{

    private final String TAG = "GREEN";
    private final int COLOR = 0x100000;

    @Override
    public int getHexColor() {
        return COLOR;
    }

    @Override
    public int getStringResource() {
        return R.string.notification_led_green;
    }

    @Override
    public int getImageResource() {
        return R.drawable.green_dot;
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
