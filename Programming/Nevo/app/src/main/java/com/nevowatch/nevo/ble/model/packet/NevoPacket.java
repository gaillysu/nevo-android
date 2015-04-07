package com.nevowatch.nevo.ble.model.packet;

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

    public class DailyHistory
    {
        public int TotalSteps;
        public int HourlySteps;
        public Date  date;
    }

}