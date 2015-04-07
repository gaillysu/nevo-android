package com.nevowatch.nevo.ble.model.request;

import java.util.Calendar;
import java.util.Date;

public class SetRtcNevoRequest extends NevoRequest {

	public  final static  byte HEADER = 0x01;
	@Override
	public byte[] getRawData() {
		
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		
		int Year = c.get(Calendar.YEAR);
		int Month = c.get(Calendar.MONTH) + 1;
		int Day = c.get(Calendar.DAY_OF_MONTH);
		int Hour = c.get(Calendar.HOUR_OF_DAY);
		int Minute = c.get(Calendar.MINUTE);
		
		return new byte[][] {
				   {0,HEADER,
					(byte) (Year&0xFF),
					(byte) ((Year>>8)&0xFF),
					(byte) (Month&0xFF),
					(byte) (Day&0xFF),
					(byte) (Hour&0xFF),
					(byte) (Minute&0xFF),
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