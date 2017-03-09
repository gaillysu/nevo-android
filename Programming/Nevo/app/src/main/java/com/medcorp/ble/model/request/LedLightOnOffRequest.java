package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by gaillysu on 15/4/16.
 */
public class LedLightOnOffRequest extends BLERequestData {
    public  final static  byte HEADER = (byte)0xF0;
    private int mLedpattern;

    public LedLightOnOffRequest(Context context, int ledpattern, boolean motorOnOff)
    {
        super(new GattAttributesDataSourceImpl(context));
        if (motorOnOff)
        mLedpattern = ledpattern | SetNotificationRequest.SetNortificationRequestValues.VIB_MOTOR;
        else
        mLedpattern = ledpattern & ~SetNotificationRequest.SetNortificationRequestValues.VIB_MOTOR;
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
