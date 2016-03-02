package com.medcorp.nevo.ble.model.color;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitor;

/**
 * Created by Karl on 9/30/15.
 */
public class OrangeLed extends NevoLed{

    private final String TAG = "ORANGE";
    private final int COLOR = 0x080000;

    @Override
    public int getHexColor() {
        return COLOR;
    }

    @Override
    public int getStringResource() {
        return R.string.notification_led_orange;
    }

    @Override
    public int getImageResource() {
        return R.drawable.orange_dot;
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
