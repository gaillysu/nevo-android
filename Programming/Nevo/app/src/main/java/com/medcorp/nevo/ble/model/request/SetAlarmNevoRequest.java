package com.medcorp.nevo.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.nevo.model.Alarm;

import net.medcorp.library.ble.model.request.BLERequestData;

import java.util.List;

public class SetAlarmNevoRequest extends BLERequestData {

	public  final static  byte HEADER = 0x41;
	public  final static int maxAlarmCount = 3;
	private int[] mHour;
	private int[] mMinute;
	private boolean[] mEnable;
	
	public SetAlarmNevoRequest(Context context, List<Alarm> list)
	{
		super(new GattAttributesDataSourceImpl(context));
		mHour = new int[maxAlarmCount];
		mMinute = new int[maxAlarmCount];
		mEnable = new boolean[maxAlarmCount];
        for (int i =0;i<list.size()&&i<maxAlarmCount;i++)
        {
            Alarm alarm = list.get(i);
            mHour[i]  = alarm.getHour();
            mMinute[i] = alarm.getMinute();
            mEnable[i] = alarm.isEnable();
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
