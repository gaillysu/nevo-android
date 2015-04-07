package com.nevowatch.nevo.ble.model.packet;

import java.util.ArrayList;

/**
 * Created by gaillysu on 15/4/1.
 */
public class DailyTrackerInfoNevoPacket extends NevoPacket {
    public DailyTrackerInfoNevoPacket(ArrayList<NevoRawData> packets) {
        super(packets);
    }

    /**
     * return Tracker history summary infomation, MAX total 7 days(include Today)
     * the actually days is saved by [DailyHistory].count
     */
    public ArrayList<DailyHistory> getDailyTrackerInfo(){

        ArrayList<DailyHistory> history = new ArrayList<DailyHistory>();
        /*TODO parse packets, here is only a blank sample*/
        history.add(new DailyHistory());
        return history;

    }
}
