package com.nevowatch.nevo.ble.model.packet;

import java.util.ArrayList;

/**
 * Created by gaillysu on 15/4/1.
 */
public class DailyTrackerNevoPacket extends NevoPacket {
    public DailyTrackerNevoPacket(ArrayList<NevoRawData> packets) {
        super(packets);
    }

    /**
     return History Daily steps
     */
    public int getDailySteps()
    {
        /* TODO : parse packets */
        return 0;
    }
    /**
     return History Hourly steps
     */
    public ArrayList<Integer> getHourlySteps()
    {
        /* TODO : parse packets */
        return new ArrayList<Integer>();
    }
}
