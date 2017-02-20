package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.datasource.GattAttributesDataSource;
import net.medcorp.library.ble.model.request.BLERequestData;

/**
 * Created by med on 16/8/9.
 */
public class NotificationRequest extends BLERequestData {
    public  final static  byte HEADER = 0x60;
    final private byte categoryID;
    final private byte numberAlert;

    public NotificationRequest(Context context, byte categoryID, byte numberAlert) {
        super(new GattAttributesDataSourceImpl(context));
        this.categoryID = categoryID;
        this.numberAlert = numberAlert;
    }

    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return new byte[][] {
                {       0,HEADER,categoryID,numberAlert,
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
