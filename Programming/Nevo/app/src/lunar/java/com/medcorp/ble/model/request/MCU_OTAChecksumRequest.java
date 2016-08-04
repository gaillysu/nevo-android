package com.medcorp.ble.model.request;


import android.content.Context;

/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
// IMPORTANT: extends  NevoRequest
public  class MCU_OTAChecksumRequest extends MCU_OTARequest {

    public  final static  byte HEADER = 0x71;

    private int mTotalpage;
    private int mChecksum;

    public MCU_OTAChecksumRequest(Context context, int totalpage, int checksum)
    {
        super(context);
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
