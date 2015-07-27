package com.nevowatch.nevo.Model;

/**
 * Created by gaillysu on 15/4/21.
 */
public class Alarm {

    private int mIndex;
    private int mHour;
    private int mMinute;
    private boolean mEnable;

    public Alarm(int index,int hour, int minute,boolean enable)
    {
        mIndex = index;
        mHour = hour;
        mMinute = minute;
        mEnable = enable;
    }

    public int getmIndex() {
        return mIndex;
    }

    public int getmHour() {
        return mHour;
    }

    public int getmMinute() {
        return mMinute;
    }

    public boolean ismEnable() {
        return mEnable;
    }
}
