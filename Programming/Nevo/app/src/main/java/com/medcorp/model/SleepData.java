package com.medcorp.model;

import android.content.Context;

import com.medcorp.R;

import org.json.JSONArray;
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

    public JSONObject toJSONObject(Context context)
    {
        JSONObject json = new JSONObject();
        try {
            json.put(context.getString(R.string.key_sleep_duration),getTotalSleep());
            json.put(context.getString(R.string.key_sleep_deep_duration),getDeepSleep());
            json.put(context.getString(R.string.key_sleep_light_duration),getLightSleep());
            json.put(context.getString(R.string.key_sleep_start_time),getSleepStart());
            json.put(context.getString(R.string.key_sleep_end_time),getSleepEnd());
            json.put(context.getString(R.string.key_sleep_wake_duration),getAwake());
            json.put(context.getString(R.string.key_sleep_hourly_wake),getHourlyWake());
            json.put(context.getString(R.string.key_sleep_hourly_light),getHourlyLight());
            json.put(context.getString(R.string.key_sleep_hourly_deep),getHourlyDeep());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private int[] convertJSONArrayIntToArray(String string){
        try {
            JSONArray jsonArray = new JSONArray(string);
            int[] hourlyLight = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++){
                hourlyLight[i] = jsonArray.optInt(i,0);
            }
            return hourlyLight;
        } catch (JSONException e) {
            e.printStackTrace();
            return new int[0];
        }
    }

    public int[] getHourlyDeepInt(){
        return convertJSONArrayIntToArray(getHourlyDeep());
    }

    public int[] getHourlyLightInt(){
        return convertJSONArrayIntToArray(getHourlyLight());
    }


    public int[] getHourlyWakeInt(){
        return convertJSONArrayIntToArray(getHourlyWake());
    }
}
