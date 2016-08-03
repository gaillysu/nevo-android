package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;


public class ReadDailyTrackerInfoNevoRequest extends BLERequestData {
	public  final static  byte HEADER = 0x24;

        public ReadDailyTrackerInfoNevoRequest(Context context) {
                super(new GattAttributesDataSourceImpl(context));
        }

    @Override
	public byte[] getRawData() {
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
        return new byte[][] {
                {0,HEADER,0,0,
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
