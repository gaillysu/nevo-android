package com.medcorp.ble.model.packet;

import net.medcorp.library.ble.model.response.MEDRawData;

import java.util.List;

/**
 * Created by gaillysu on 15/4/1.
 */
public class BatteryLevelPacket extends Packet {
    public BatteryLevelPacket(List<MEDRawData> packets) {
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
