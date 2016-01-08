package com.medcorp.nevo.model;

import org.json.JSONException;
import org.json.JSONObject;

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
    private String hourlyWake;
    private String hourlyLight;
    private String hourlyDeep;

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

    public String getHourlyWake() {
        return hourlyWake;
    }

    public void setHourlyWake(String hourlyWake) {
        this.hourlyWake = hourlyWake;
    }

    public String getHourlyLight() {
        return hourlyLight;
    }

    public void setHourlyLight(String hourlyLight) {
        this.hourlyLight = hourlyLight;
    }

    public String getHourlyDeep() {
        return hourlyDeep;
    }

    public void setHourlyDeep(String hourlyDeep) {
        this.hourlyDeep = hourlyDeep;
    }

    public JSONObject toJSONObject()
    {
        JSONObject json = new JSONObject();
        try {
            json.put("sleepDuration",getTotalSleep());
            json.put("sleepWakeDuration",getAwake());
            json.put("sleepLightDuration",getLightSleep());
            json.put("sleepDeepDuration",getDeepSleep());
            json.put("startDateTime",getSleepStart());
            json.put("endDateTime",getSleepEnd());
            json.put("mergeHourlyWakeTime",getHourlyWake());
            json.put("mergeHourlyLightTime",getHourlyLight());
            json.put("mergeHourlyDeepTime",getHourlyDeep());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
