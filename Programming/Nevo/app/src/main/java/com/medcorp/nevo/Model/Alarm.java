package com.medcorp.nevo.model;

/**
 * Created by gaillysu on 15/4/21.
 */
public class Alarm {

    private int id = -1;
    private int hour;
    private int minute;
    private boolean enable;
    private String label;

    public Alarm(int hour, int minute,boolean enable,String label)
    {
        this.hour = hour;
        this.minute = minute;
        this.enable = enable;
        this.label = label;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
