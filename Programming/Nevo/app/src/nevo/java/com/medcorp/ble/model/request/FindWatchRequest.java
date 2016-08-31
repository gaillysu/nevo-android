package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;
import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 16/7/25.
 */
public class FindWatchRequest extends BLERequestData {
    public  final static  byte HEADER = 0x44;
    private boolean vibrator;

    public FindWatchRequest(Context context,boolean vibrator) {
        super(new GattAttributesDataSourceImpl(context));
        this.vibrator = vibrator;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {       0,HEADER, (byte) 0xFF, (byte) 0xFF,
                        vibrator?(byte)0xFF:0,0,0,0, //light on all color LED and start/stop vibrator
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
