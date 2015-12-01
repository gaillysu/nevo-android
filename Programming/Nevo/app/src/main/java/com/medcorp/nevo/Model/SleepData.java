package com.medcorp.nevo.model;

/**
 * Created by Karl on 11/27/15.
 */
public class SleepData {

    private int deepSleep;
    private int lightSleep;
    private int awake;
    private long date;

    public SleepData(int deepSleep, int lightSleep, int awake, long date) {
        this.deepSleep = deepSleep;
        this.lightSleep = lightSleep;
        this.awake = awake;
        this.date = date;
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
}
