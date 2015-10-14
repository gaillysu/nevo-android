package com.medcorp.nevo.ble.model.packet;

import com.medcorp.nevo.ble.util.HexUtils;

import java.util.List;

/**
 * Created by gaillysu on 15/4/1.
 */

public class DailyStepsNevoPacket extends NevoPacket {

    public DailyStepsNevoPacket(List<NevoRawData> packets) {
        super(packets);
    }

    /**
     return the Current Daily steps
     */
    public int getDailySteps()
    {
        int dailySteps = 0;

        dailySteps = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[2],
                getPackets().get(0).getRawData()[3],
                getPackets().get(0).getRawData()[4],
                getPackets().get(0).getRawData()[5]});
        return dailySteps;
    }
    /**
     return the Daily steps Goal
     */
    public int getDailyStepsGoal()
    {
        int dailyStepGoal = 0;

        dailyStepGoal  = HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[6],
                getPackets().get(0).getRawData()[7],
                getPackets().get(0).getRawData()[8],
                getPackets().get(0).getRawData()[9]});

        return dailyStepGoal;
    }


}
