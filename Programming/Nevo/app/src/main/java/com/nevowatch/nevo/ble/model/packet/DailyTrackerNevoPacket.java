package com.nevowatch.nevo.ble.model.packet;

import com.nevowatch.nevo.ble.util.HexUtils;

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
        int dailySteps = HexUtils.bytesToInt(new byte[]{getPackets().get(1).getRawData()[4],
                getPackets().get(1).getRawData()[5],
                getPackets().get(1).getRawData()[6],
                getPackets().get(1).getRawData()[7]
        });

        return dailySteps;

    }
    /**
     return History Hourly steps
     */
    public ArrayList<Integer> getHourlySteps()
    {

        int HEADERLENGTH = 6;
        int hourlySteps  = 0;
        ArrayList<Integer> HourlySteps = new ArrayList<Integer>();

        //get every hour Steps:
        for (int i = 0; i<24; i++)
        {
            hourlySteps = 0;

            if (getPackets().get(HEADERLENGTH+i*3).getRawData()[18] != (byte)0xFF
                && getPackets().get(HEADERLENGTH+i*3).getRawData()[19] != (byte)0xFF
                && getPackets().get(HEADERLENGTH+i*3+1).getRawData()[2] != (byte)0xFF
                && getPackets().get(HEADERLENGTH+i*3+1).getRawData()[3] != (byte)0xFF)
            {
                hourlySteps = HexUtils.bytesToInt(new byte[]{getPackets().get(HEADERLENGTH+i*3).getRawData()[18],
                        getPackets().get(HEADERLENGTH+i*3).getRawData()[19]});

                hourlySteps += HexUtils.bytesToInt(new byte[]{getPackets().get(HEADERLENGTH+i*3+1).getRawData()[2],
                        getPackets().get(HEADERLENGTH+i*3+1).getRawData()[3]});


            }

            HourlySteps.add(i,hourlySteps);
        }
        return HourlySteps;

    }
}
