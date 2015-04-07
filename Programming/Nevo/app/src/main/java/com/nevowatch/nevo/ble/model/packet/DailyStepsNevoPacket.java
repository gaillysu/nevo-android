package com.nevowatch.nevo.ble.model.packet;

import java.util.ArrayList;

/**
 * Created by gaillysu on 15/4/1.
 */

public class DailyStepsNevoPacket extends NevoPacket {

    public DailyStepsNevoPacket(ArrayList<NevoRawData> packets) {
        super(packets);
    }

    /**
     return the Current Daily steps
     */
    public int getDailySteps()
    {
        int dailySteps = 0;

        dailySteps = (int)getPackets().get(0).getRawData()[2]
                      + (int)getPackets().get(0).getRawData()[3]<<8
                      + (int)getPackets().get(0).getRawData()[4]<<16
                      + (int)getPackets().get(0).getRawData()[5]<<24;

        return dailySteps;
    }
    /**
     return the Daily steps Goal
     */
    public int getDailyStepsGoal()
    {
        int dailyStepGoal = 0;

        dailyStepGoal = (int)getPackets().get(0).getRawData()[6]
                + (int)getPackets().get(0).getRawData()[7]<<8
                + (int)getPackets().get(0).getRawData()[8]<<16
                + (int)getPackets().get(0).getRawData()[9]<<24;

        return dailyStepGoal;
    }


}
