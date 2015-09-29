package com.medcorp.nevo.ble.model.request;

/**
 * Created by gaillysu on 15/4/16.
 */
public class TestModeNevoRequest extends NevoRequest{
    public  final static  byte HEADER = (byte)0xF0;
    private int mLedpattern;

    public TestModeNevoRequest(int ledpattern, boolean motorOnOff)
    {
        if (motorOnOff)
        mLedpattern = ledpattern | SetNotificationNevoRequest.SetNortificationRequestValues.VIB_MOTOR;
        else
        mLedpattern = ledpattern & ~SetNotificationNevoRequest.SetNortificationRequestValues.VIB_MOTOR;
    }
    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {0,HEADER,
                        (byte)(mLedpattern&0xFF),
                        (byte)((mLedpattern>>8)&0xFF),
                        (byte)((mLedpattern>>16)&0xFF),
                        0,0,0,
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
        return HEADER;
    }
}
