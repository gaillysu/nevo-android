package com.medcorp.lunar.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.nevo.model.Alarm;

import net.medcorp.library.ble.model.request.BLERequestData;

import java.util.List;

/**
 * Created by med on 16/8/1.
 */
public class SetAlarmLunarRequest extends BLERequestData {

    public  final static  byte HEADER = 0x0c;
    public  final static int maxAlarmCount = 14;
    private int mHour;
    private int mMinute;
    private boolean mEnable;
    private byte alarmNumber; //0 ~~ maxAlarmCount-1

    public SetAlarmLunarRequest(Context context, Alarm alarm,byte alarmNumber)
    {
        super(new GattAttributesDataSourceImpl(context));
        mHour = alarm.getHour();
        mMinute = alarm.getMinute();
        mEnable = alarm.isEnable();
        this.alarmNumber = alarmNumber;
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
                        (byte) (alarmNumber&0xFF),(byte) (mEnable?1:0),
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

