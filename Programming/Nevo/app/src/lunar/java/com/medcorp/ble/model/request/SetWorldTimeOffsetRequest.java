package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by med on 16/8/1.
 */
public class SetWorldTimeOffsetRequest extends BLERequestData {
    public  final static  byte HEADER = 0x03;
    private final byte  offset;// value: [-23,-22,...0,...,22,23]

    public SetWorldTimeOffsetRequest(Context context, byte offset) {
        super(new GattAttributesDataSourceImpl(context));
        this.offset = offset;
    }

    @Override
    public byte[] getRawData() {

        return null;
    }

    @Override
    public byte[][] getRawDataEx() {

        return new byte[][] {
                {0,HEADER,offset,0,
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
