package com.medcorp.lunar.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 16/8/1.
 */
public class GetBatteryLevelLunarRequest extends BLERequestData {

    public  final static  byte HEADER = 0x0b;

    public GetBatteryLevelLunarRequest(Context context) {
        super(new GattAttributesDataSourceImpl(context));
    }

    @Override
    public byte[] getRawData() {

        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {       0,HEADER,0,0,
                        0,0,0,0,
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

