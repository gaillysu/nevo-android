package com.medcorp.nevo.model;

/**
 * Created by Karl on 11/27/15.
 */
public class SleepData {

    private int deepSleep;
    private int lightSleep;
    private int awake;
    private long date;
    private long sleepStart;
    private long sleepEnd;

    public SleepData(int deepSleep, int lightSleep, int awake, long date) {
        this.deepSleep = deepSleep;
        this.lightSleep = lightSleep;
        this.awake = awake;
        this.date = date;
    }
    public SleepData(int deepSleep, int lightSleep, int awake, long date,long sleepStart,long sleepEnd) {
        this.deepSleep = deepSleep;
        this.lightSleep = lightSleep;
        this.awake = awake;
        this.date = date;
        this.sleepStart = sleepStart;
        this.sleepEnd = sleepEnd;
    }

    public int getDeepSleep() {
        return deepSleep;
    }

    public void setDeepSleep(int deepSleep) {
        this.deepSleep = deepSleep;
    }

    public int getTotalSleep() {
        return deepSleep + lightSleep + awake;
    }


    public int getLightSleep() {
        return lightSleep;
    }

    public void setLightSleep(int lightSleep) {
        this.lightSleep = lightSleep;
    }

    public int getAwake() {
        return awake;
    }

    public void setAwake(int awake) {
        this.awake = awake;
    }

    public long getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public long getSleepStart() {
        return sleepStart;
    }

    public void setSleepStart(long sleepStart) {
        this.sleepStart = sleepStart;
    }

    public long getSleepEnd() {
        return sleepEnd;
    }

    public void setSleepEnd(long sleepEnd) {
        this.sleepEnd = sleepEnd;
    }
}
