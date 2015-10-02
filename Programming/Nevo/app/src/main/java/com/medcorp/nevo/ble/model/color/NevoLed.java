package com.medcorp.nevo.ble.model.color;

import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitable;

/**
 * Created by Karl on 9/30/15.
 */
public abstract class NevoLed implements NevoLedVisitable{
    public abstract int getColor();
    public abstract String getTag();

    @Override
    public boolean equals(Object o) {
        if (o instanceof NevoLed){
            return getColor() == ((NevoLed) o).getColor();
        }
        return super.equals(o);
    }
}
