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

//    BLUE_LED   = 0x010000;
//    GREEN_LED  = 0x100000;
//    YELLOW_LED = 0x040000;
//    RED_LED    = 0x200000;
//    ORANGE_LED = 0x080000;
//    LIGHTGREEN_LED = 0x020000;


}
