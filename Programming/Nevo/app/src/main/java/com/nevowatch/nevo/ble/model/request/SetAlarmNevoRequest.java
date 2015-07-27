package com.nevowatch.nevo.ble.model.request;

import com.nevowatch.nevo.Model.Alarm;

import java.util.ArrayList;

public class SetAlarmNevoRequest extends NevoRequest {

	public  final static  byte HEADER = 0x41;
	
	private int[] mHour;
	private int[] mMinute;
	private boolean[] mEnable;
	
	public SetAlarmNevoRequest(ArrayList<Alarm> list)
	{
		mHour = new int[list.size()];
		mMinute = new int[list.size()];
		mEnable = new boolean[list.size()];
        for (int i =0;i<list.size();i++)
        {
            Alarm alarm = list.get(i);
            mHour[i]  = alarm.getmHour();
            mMinute[i] = alarm.getmMinute();
            mEnable[i] = alarm.ismEnable();
        }
	}
	
	@Override
	public byte[] getRawData() {
		
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {
		
		return new byte[][] {
				   {0,HEADER,(byte) (mHour[0]&0xFF),(byte) (mMinute[0]&0xFF),
					(byte) (mEnable[0]?7:0),(byte) (mHour[1]&0xFF),(byte) (mMinute[1]&0xFF),
                           (byte) (mEnable[1]?7:0),
                           (byte) (mHour[2]&0xFF),(byte) (mMinute[2]&0xFF),
                           (byte) (mEnable[2]?7:0),
                    0,
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
