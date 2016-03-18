package com.medcorp.nevo.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

public class GetStepsGoalNevoRequest extends BLERequestData {

	public  final static byte HEADER = 0x26;

        public GetStepsGoalNevoRequest(Context context) {
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
