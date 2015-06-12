package com.nevowatch.nevo.ble.model.packet;

import com.nevowatch.nevo.Model.DailyHistory;
import com.nevowatch.nevo.ble.util.HexUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
