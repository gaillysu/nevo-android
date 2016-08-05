package com.medcorp.ble.model.packet;

import com.medcorp.model.ApplicationInfomation;

import net.medcorp.library.ble.model.response.MEDRawData;
import net.medcorp.library.ble.util.HexUtils;

import java.util.List;

/**
 * Created by med on 16/8/5.
 */
public class ReadApplicationPacket extends Packet {
    public ReadApplicationPacket(List<MEDRawData> packets) {
        super(packets);
    }
    public ApplicationInfomation getApplicationInfomation()
    {
        byte dataLength = getPackets().get(0).getRawData()[2];
        if(dataLength == (byte)0xFF)
        {
            return null;
        }
        short ledPattern = (short) HexUtils.bytesToInt(new byte[]{getPackets().get(0).getRawData()[3],getPackets().get(0).getRawData()[4]});
        byte[] byteData = new byte[dataLength];
        if(dataLength<=15)
        {
            System.arraycopy(getPackets().get(0).getRawData(),5,byteData,0,dataLength);
        }
        else{
            int totalDataRaws = 1 + ((dataLength -15)%18 == 0?(dataLength -15)/18 : (dataLength -15)/18+1);
            int copyLength = 0;
            for(int i =0;i<totalDataRaws;i++)
            {
                if(i==0)
                {
                    System.arraycopy(getPackets().get(i).getRawData(),5,byteData,0,15);
                    copyLength = 15;
                }
                else if(i==totalDataRaws-1)
                {
                    System.arraycopy(getPackets().get(i).getRawData(),2,byteData,copyLength,dataLength-copyLength);
                }
                else {
                    System.arraycopy(getPackets().get(i).getRawData(),2,byteData,copyLength,18);
                    copyLength = copyLength + 18;
                }
            }
        }

        return new ApplicationInfomation((byte)0,ledPattern,new String(byteData));
    }
}
