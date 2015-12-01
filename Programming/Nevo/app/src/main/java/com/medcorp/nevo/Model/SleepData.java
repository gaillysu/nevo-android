package com.medcorp.nevo.model;

/**
 * Created by Karl on 11/27/15.
 */
public class SleepData {

    private int deepSleep;
    private int totalSleep;
    private int lightSleep;
    private int awake;
    private long date;

    public SleepData(int totalSleep, int deepSleep, int lightSleep, int awake, long date) {
        this.deepSleep = deepSleep;
        this.totalSleep = totalSleep;
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
        return totalSleep;
    }

    public void setTotalSleep(int totalSleep) {
        this.totalSleep = totalSleep;
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
