package com.medcorp.nevo.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.RequestData;

public class ReadDailyTrackerNevoRequest extends RequestData {
	public  final static  byte HEADER = 0x25;
    // tracker no is 0~6
    private int mTrackerNo = 0;

    public ReadDailyTrackerNevoRequest(Context context, int trackerno)
    {
        super(new GattAttributesDataSourceImpl(context));
        mTrackerNo = trackerno;
    }
	@Override
	public byte[] getRawData() {
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
        return new byte[][] {
                {0,HEADER,(byte)(mTrackerNo&0xFF),0,
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
