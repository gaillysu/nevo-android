package com.nevowatch.nevo.ble.model.request;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public  class NevoOTAPacketFileSizeRequest extends NevoMCU_OTARequest {

    private int mFilelen;

    public NevoOTAPacketFileSizeRequest(int filelen)
    {
        mFilelen = filelen;
    }
    @Override
    public byte[] getRawData() {
        //little endian mode
        byte[] mPacket = new byte[]{0,0,0,0,0,0,0,0,
                (byte)(mFilelen & 0xFF),
                (byte)((mFilelen>>8)&0xFF),
                (byte)((mFilelen>>16)&0xFF),
                (byte)((mFilelen>>24)&0xFF)};
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
