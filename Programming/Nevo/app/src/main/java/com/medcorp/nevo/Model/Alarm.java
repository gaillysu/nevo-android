package com.medcorp.nevo.Model;

/**
 * Created by gaillysu on 15/4/21.
 */
public class Alarm {

    private int index;
    private int hour;
    private int minute;
    private boolean enable;

    public Alarm(int index,int hour, int minute,boolean enable)
    {
        this.index = index;
        this.hour = hour;
        this.minute = minute;
        this.enable = enable;
    }

    public int getIndex() {
        return index;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isEnable() {
        return enable;
    }
}
