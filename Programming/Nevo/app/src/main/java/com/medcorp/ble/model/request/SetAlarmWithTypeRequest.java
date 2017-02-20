package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.model.Alarm;

import net.medcorp.library.ble.model.request.BLERequestData;

public class SetAlarmWithTypeRequest extends BLERequestData {

	public  final static  byte HEADER = 0x41;
	public  final static int maxAlarmCount = 14;
	private int mHour;
	private int mMinute;
	private byte alarmWeekDay;
	private byte alarmNumber; //0 ~~ 13, 0~6:wake alarm, 7~13:sleep alarm

	public SetAlarmWithTypeRequest(Context context, Alarm alarm)
	{
		super(new GattAttributesDataSourceImpl(context));
		mHour = alarm.getHour();
		mMinute = alarm.getMinute();
		alarmWeekDay = (alarm.getWeekDay()&0x80)==0x80?(byte)(alarm.getWeekDay()&0x0f):(byte)0;
		this.alarmNumber = alarm.getAlarmNumber();
	}

	@Override
	public byte[] getRawData() {
		return null;
	}

	@Override
	public byte[][] getRawDataEx() {

		return new byte[][] {
				{0,HEADER,
						(byte) (mHour&0xFF),(byte) (mMinute&0xFF),
						(byte) (alarmNumber&0xFF),alarmWeekDay,
						0,0,0,0,
						0,0,
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