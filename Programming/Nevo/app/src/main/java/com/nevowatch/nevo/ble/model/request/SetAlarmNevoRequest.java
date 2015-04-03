package com.nevowatch.nevo.ble.model.request;

public class SetAlarmNevoRequest extends NevoRequest {

	public  final static  byte HEADER = 0x41;
	
	private int mHour;
	private int mMinute;
	private boolean mEnable;
	
	public SetAlarmNevoRequest(int hour, int minute,boolean enable)
	{
		mHour = hour;
		mMinute = minute;
		mEnable = enable;
	}
	
	@Override
	public byte[] getRawData() {
		
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
		
		return new byte[][] {
				   {0,HEADER,(byte) (mHour&0xFF),(byte) (mMinute&0xFF),
					(byte) (mEnable?7:0),0,0,0,			
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
