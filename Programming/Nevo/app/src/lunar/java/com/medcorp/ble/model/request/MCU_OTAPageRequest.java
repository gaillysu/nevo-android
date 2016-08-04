package com.medcorp.ble.model.request;


import android.content.Context;

import java.util.ArrayList;

/**
 * Created by gaillysu on 15/6/8.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public  class MCU_OTAPageRequest extends MCU_OTARequest {

    //one page has 5 packets
    private ArrayList<MCU_OTAPacketRequest> mPage = new ArrayList<MCU_OTAPacketRequest>();
    public MCU_OTAPageRequest(Context context)
    {
        super(context);
    }

    public void addPacket(MCU_OTAPacketRequest packet)
    {
        mPage.add(packet);
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        byte [][] page = new byte[mPage.size()][mPage.get(0).getRawData().length];
        int i=0;
        for(MCU_OTAPacketRequest packet:mPage)
        {
            System.arraycopy(packet.getRawData(),0,page[i++],0,packet.getRawData().length);
        }
        return page;
    }

    @Override
    public byte getHeader() {
        //no used value
        return 0;
    }

}
