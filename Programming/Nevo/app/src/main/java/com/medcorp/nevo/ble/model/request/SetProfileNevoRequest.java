package com.medcorp.nevo.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.nevo.model.User;
import com.medcorp.nevo.util.Preferences;

import net.medcorp.library.ble.model.request.BLERequestData;

public class SetProfileNevoRequest extends BLERequestData {
	public  final static  byte HEADER = 0x20;
	private User user;

	public SetProfileNevoRequest(Context context,User user) {
		super(new GattAttributesDataSourceImpl(context));
		this.user = user;
	}

	@Override
	public byte[] getRawData() {
	
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {

        int age = user.getAge();
        int height = user.getHeight();
        int weight = user.getWeight();
        int sex = user.getSex();

		return new byte[][] {
				   {0,HEADER,
                   (byte)(age&0xFF),(byte)(height&0xFF),(byte)(weight&0xFF),(byte)(sex&0xFF),
                    0,0,0,0,
                    0,0,0,0,
                    0,0,0,0,
                    0,0
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
