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
       /* TODO : parse packets */
        return dailySteps;
    }
    /**
     return the Daily steps Goal
     */
    public int getDailyStepsGoal()
    {
        int dailyStepGoal = 0;
        /* TODO : parse packets */
        return dailyStepGoal;
    }


}
