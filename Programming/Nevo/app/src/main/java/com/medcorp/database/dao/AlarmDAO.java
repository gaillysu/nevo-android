package com.medcorp.database.dao;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Karl on 11/25/15.
 */
public class AlarmDAO {

    public static final String iDString = "ID";
    @DatabaseField(generatedId = true)
    private int ID;

    public static final String alarmString = "alarm";
    @DatabaseField
    private String Alarm;

    public static final String labelString = "label";
    @DatabaseField
    private String Label;

    public static final String weekDayString = "weekDay";
    @DatabaseField
    private byte weekDay;

    public static final String alarmTypeString = "alarmType";
    @DatabaseField
    private int alarmType;

    public static final String alarmRepeatString = "alarmRepeat";
    @DatabaseField
    private String alarmRepeat;

    public static String getAlarmTypeString() {
        return alarmTypeString;
    }

    public static String getAlarmRepeatString() {
        return alarmRepeatString;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmRepeat() {
        return alarmRepeat;
    }

    public void setAlarmRepeat(String alarmRepeat) {
        this.alarmRepeat = alarmRepeat;
    }

    public static String getiDString() {
        return iDString;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public static String getLabelString() {
        return labelString;
    }


    public static String getWeekDayString() {
        return weekDayString;
    }

    public byte getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(byte weekDay) {
        this.weekDay = weekDay;
    }

    public String getAlarm() {
        return Alarm;
    }

    public void setAlarm(String alarm) {
        Alarm = alarm;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }
}
