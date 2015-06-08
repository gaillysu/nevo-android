package com.nevowatch.nevo.ble.model.request;


/**
 * Created by gaillysu on 15/6/8.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public  class NevoMCU_OTAPacketRequest extends  NevoMCU_OTARequest{

    private byte[] mPacket;
    public NevoMCU_OTAPacketRequest(byte[] packet)
    {
        mPacket = packet;
    }

    @Override
    public byte[] getRawData() {
        return mPacket;
    }

    @Override
    public byte[][] getRawDataEx() {
        //no used function
        return null;
    }

    @Override
    public byte getHeader() {
        //no used value
        return 0;
    }

}
