package com.medcorp.nevo.event.bluetooth;

import com.medcorp.nevo.model.Battery;

/**
 * Created by karl-john on 3/3/16.
 */
public class BatteryEvent {
    private final Battery battery;

    public BatteryEvent(Battery battery) {
        this.battery = battery;
    }

    public Battery getBattery() {
        return battery;
    }
}
