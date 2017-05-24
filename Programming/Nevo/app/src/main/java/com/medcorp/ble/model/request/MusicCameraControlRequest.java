package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;
import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 17/5/24.
 */

public class MusicCameraControlRequest extends BLERequestData {
    public  final static  byte HEADER = 0x42;
    private int controlCode;//pls see R25 Ble interface spec. file
    public MusicCameraControlRequest(Context context, int controlCode) {
        super(new GattAttributesDataSourceImpl(context));
        this.controlCode = (short) controlCode;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {       0,HEADER, (byte) (controlCode &0xFF),0,
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
        return 0;
    }
}
