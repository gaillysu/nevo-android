package com.medcorp.ble.model.packet;

import net.medcorp.library.ble.model.response.MEDRawData;
import net.medcorp.library.ble.util.HexUtils;

import java.util.List;

/**
 * Created by med on 16/8/1.
 */
public class DailyStepsPacket extends Packet {

    public DailyStepsPacket(List<MEDRawData> packets) {
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
