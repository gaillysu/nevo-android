package com.medcorp.nevo.ble.model.request;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public  class NevoOTAPacketRequest extends NevoOTARequest {

    private byte[] mPacket;
    public NevoOTAPacketRequest(byte[] packet)
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