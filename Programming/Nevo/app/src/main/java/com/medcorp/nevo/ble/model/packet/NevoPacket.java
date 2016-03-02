package com.medcorp.nevo.ble.model.packet;

import net.medcorp.library.ble.model.response.MEDRawData;
import net.medcorp.library.ble.util.HexUtils;

import java.util.List;

/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
public class NevoPacket {

    private List<MEDRawData> mPackets;

    public NevoPacket(List<MEDRawData> packets)
    {
        mPackets = packets;
    }

    public DailyTrackerInfoNevoPacket newDailyTrackerInfoNevoPacket()
    {
        return new DailyTrackerInfoNevoPacket(mPackets);
    }
    public DailyTrackerNevoPacket newDailyTrackerNevoPacket()
    {
        return new DailyTrackerNevoPacket(mPackets);
    }
    public DailyStepsNevoPacket newDailyStepsNevoPacket()
    {
        return new DailyStepsNevoPacket(mPackets);
    }
    public BatteryLevelNevoPacket newBatteryLevelNevoPacket()
    {
        return new BatteryLevelNevoPacket(mPackets);
    }

    public byte getHeader()
    {
        return mPackets.get(0).getRawData()[1];
    }
    public List<MEDRawData> getPackets()
    {
        return mPackets;
    }

    /**
     * Check the packets number, sometimes, I find that send one request cmd,00****,FF****
     * receive 4 packets: 00****,00****,FF****,FF****, the last packet always start with 0xFF
     * @return true or false
     */
    public boolean isVaildPackets() {
        if (mPackets.size() < 2) return false;
        for (int i = 0; i < (mPackets.size()-1); i++) {
            if (i != HexUtils.bytesToInt(new byte[]{mPackets.get(i).getRawData()[0]})) return false;
        }
        return true;
    }

}
