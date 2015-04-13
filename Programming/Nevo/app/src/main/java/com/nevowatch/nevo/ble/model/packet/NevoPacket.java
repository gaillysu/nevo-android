package com.nevowatch.nevo.ble.model.packet;

import com.nevowatch.nevo.ble.util.HexUtils;

import java.util.ArrayList;
import java.util.Date;
/**
 * Created by gaillysu on 15/4/1.
 */
public class NevoPacket {

    private ArrayList<NevoRawData> mPackets;

    public NevoPacket(ArrayList<NevoRawData> packets)
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
    public byte getHeader()
    {
        return mPackets.get(0).getRawData()[1];
    }
    public ArrayList<NevoRawData> getPackets()
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
