package com.nevowatch.nevo.ble.model.request;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
// IMPORTANT: extends  NevoRequest
public  class NevoMCU_OTAChecksumRequest extends  NevoMCU_OTARequest{

    public  final static  byte HEADER = 0x71;

    private int mTotalpage;
    private int mChecksum;

    public NevoMCU_OTAChecksumRequest(int totalpage, int checksum)
    {
        mTotalpage = totalpage;
        mChecksum = checksum;
    }
    @Override
    public byte[] getRawData() {
           return null;
    }

    @Override
    public byte[][] getRawDataEx() {

        return new byte[][] {
                {0,HEADER,
                        (byte) (0xFF),
                        (byte) (0xFF),
                        (byte) (mTotalpage&0xFF),
                        (byte) ((mTotalpage>>8)&0xFF),
                        (byte) (mChecksum&0xFF),
                        0,
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0
                },

                {(byte) 0xFF,HEADER,0,0,
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0,
                        0,0,0,0
                }
        };
    }

    @Override
    public byte getHeader() {
        //no used value
        return HEADER;
    }

}
