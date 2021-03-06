package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

public class WriteSettingRequest extends BLERequestData {
	public  final static  byte HEADER = 0x21;

	public WriteSettingRequest(Context context) {
		super(new GattAttributesDataSourceImpl(context));
	}

	@Override
	public byte[] getRawData() {
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
        int walk_stride  = 73;
        int run_stride   = 122;
        int swiming_stride  = 105;
        int enable = 3; //bit0:1, bit1:1

		return new byte[][] {
				   {0,HEADER,
                   (byte)(walk_stride&0xFF),(byte)((walk_stride>>8)&0xFF),
                   (byte)(run_stride&0xFF),(byte)((run_stride>>8)&0xFF),
                   (byte)(swiming_stride&0xFF),(byte)((swiming_stride>>8)&0xFF),
                   (byte)(enable&0xFF),
                    0,0,0,
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
