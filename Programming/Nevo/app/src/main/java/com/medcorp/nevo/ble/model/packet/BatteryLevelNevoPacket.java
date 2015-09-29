package com.medcorp.nevo.ble.model.packet;

import java.util.ArrayList;

/**
 * Created by gaillysu on 15/4/1.
 */
public class BatteryLevelNevoPacket extends NevoPacket {
    public BatteryLevelNevoPacket(ArrayList<NevoRawData> packets) {
        super(packets);
    }
    /**
     return battery level
     batt_level
     0 - low battery level
     1 - half battery level
     2 - full battery level
     */
    public byte getBatteryLevel()
    {
        return getPackets().get(0).getRawData()[2];
    }


}
