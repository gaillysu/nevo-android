package com.medcorp.ble.model.color;

import com.medcorp.ble.model.color.visitor.NevoLedVisitor;
import com.medcorp.R;

/**
 * Created by Karl on 9/30/15.
 */
public class RedLed extends NevoLed{

    private final String TAG = "RED";
    public final static int COLOR = 0x200000;

    @Override
    public int getHexColor() {
        return COLOR;
    }

    @Override
    public int getStringResource() {
        return R.string.notification_led_red;
    }

    @Override
    public int getImageResource() {
        return R.drawable.red_dot;
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
