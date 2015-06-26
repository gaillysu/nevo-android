package com.nevowatch.nevo.ble.model.request;


import java.util.ArrayList;

/**
 * Created by gaillysu on 15/6/8.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public  class NevoMCU_OTAPageRequest extends  NevoMCU_OTARequest{

    //one page has 5 packets
    private ArrayList<NevoMCU_OTAPacketRequest> mPage = new ArrayList<NevoMCU_OTAPacketRequest>();
    public NevoMCU_OTAPageRequest()
    {

    }
    public void addPacket(NevoMCU_OTAPacketRequest packet)
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
        for(NevoMCU_OTAPacketRequest packet:mPage)
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
