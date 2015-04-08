package com.nevowatch.nevo.ble.model.packet;

import com.nevowatch.nevo.Model.DailyHistory;

import java.util.ArrayList;
import java.util.Date;

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
    public DailyHistory getDailyTrackerInfo(){

        /*TODO by Gailly parse packets, here is only a blank sample*/

        return new DailyHistory(new Date(), new ArrayList<Integer>(), 0);

    }
}
