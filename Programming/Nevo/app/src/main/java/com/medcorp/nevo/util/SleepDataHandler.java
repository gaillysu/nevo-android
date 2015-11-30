package com.medcorp.nevo.util;

import android.util.Log;

import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.SleepData;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Karl on 11/27/15.
 */
public class SleepDataHandler {

    private List<Sleep> sleepList;
    private boolean logging;

    public SleepDataHandler(List<Sleep> sleepList) {
        this.sleepList = sleepList;
    }

    private SleepData getSleepData(Sleep todaySleep) {
        int lightSleep = 0;
        int deepSleep = 0;
        int wake = 0;
        try {
            JSONArray hourlyLight = new JSONArray(todaySleep.getHourlyLight());
            for (int i = 0; i < 18 && hourlyLight.length() > 18; i++) {
                lightSleep += Integer.parseInt(hourlyLight.getString(i));
            }

            JSONArray hourlyDeep = new JSONArray(todaySleep.getHourlyDeep());
            for (int i = 0; i < 18 && hourlyDeep.length() > 18; i++) {
                deepSleep += Integer.parseInt(hourlyDeep.getString(i));
            }

            JSONArray hourlyWake = new JSONArray(todaySleep.getHourlyWake());
            for (int i = 0; i < 18 && hourlyWake.length() > 18; i++) {
                wake += Integer.parseInt(hourlyWake.getString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new SleepData((lightSleep + deepSleep + wake), deepSleep, lightSleep, wake, todaySleep.getCreatedDate());
    }

    private boolean sleptToday(Sleep sleep) {
        try {
            JSONArray hourlyLight = new JSONArray(sleep.getHourlyLight());
            for (int i = 0; i < 18 && hourlyLight.length() > 18; i++) {
                if (Integer.parseInt(hourlyLight.getString(i)) > 0) {
                    return true;
                }
            }
            JSONArray hourlyDeep = new JSONArray(sleep.getHourlyDeep());
            for (int i = 0; i < 18 && hourlyDeep.length() > 18; i++) {
                if (Integer.parseInt(hourlyDeep.getString(i)) > 0) {
                    return true;
                }
            }
            JSONArray hourlyWake = new JSONArray(sleep.getHourlyWake());
            for (int i = 0; i < 18 && hourlyWake.length() > 18; i++) {
                if (Integer.parseInt(hourlyWake.getString(i)) > 0) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean sleptAfterTwelve(Sleep sleep) {
        try {
            int totalFirstHour = 0;
            JSONArray hourlyLight = new JSONArray(sleep.getHourlyLight());
            JSONArray hourlyDeep = new JSONArray(sleep.getHourlyDeep());
            JSONArray hourlyWake = new JSONArray(sleep.getHourlyWake());
            totalFirstHour += Integer.parseInt(hourlyLight.getString(0));
            totalFirstHour += Integer.parseInt(hourlyDeep.getString(0));
            totalFirstHour += Integer.parseInt(hourlyWake.getString(0));
            if (totalFirstHour == 60) {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean isYesterday(Sleep todaySleep, Sleep yesterdaySleep) {
        Calendar todayCalendar = Calendar.getInstance(); // today
        todayCalendar.setTime(new Date(todaySleep.getDate()));
        Calendar yesterdayCalendar = Calendar.getInstance();
        yesterdayCalendar.setTime(new Date(yesterdaySleep.getDate())); // your date
        if (todayCalendar.get(Calendar.YEAR) == yesterdayCalendar.get(Calendar.YEAR)
                && todayCalendar.get(Calendar.DAY_OF_YEAR) - 1 == yesterdayCalendar.get(Calendar.DAY_OF_YEAR)) {
            return true;
        }
        return false;
    }

    private SleepData getSleepDataAfterSix(Sleep yesterdaySleep) {
        int lightSleep = 0;
        int deepSleep = 0;
        int wake = 0;
        try {
            JSONArray hourlyLight = new JSONArray(yesterdaySleep.getHourlyLight());
            for (int i = 18; i < hourlyLight.length(); i++) {
                lightSleep += Integer.parseInt(hourlyLight.getString(i));
            }

            JSONArray hourlyDeep = new JSONArray(yesterdaySleep.getHourlyDeep());
            for (int i = 18; i < hourlyDeep.length(); i++) {
                deepSleep += Integer.parseInt(hourlyDeep.getString(i));
            }

            JSONArray hourlyWake = new JSONArray(yesterdaySleep.getHourlyWake());
            for (int i = 18; i < hourlyWake.length(); i++) {
                wake += Integer.parseInt(hourlyWake.getString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new SleepData((lightSleep + deepSleep + wake), deepSleep, lightSleep, wake,yesterdaySleep.getDate());
    }

    private SleepData merge(SleepData today, SleepData yesterday) {
        return new SleepData((today.getTotalSleep()+ yesterday.getTotalSleep()), (today.getDeepSleep() + yesterday.getDeepSleep()), (today.getLightSleep() + yesterday.getLightSleep()), (today.getAwake() + yesterday.getAwake()),today.getDate());
    }

    public List<SleepData> getSleepData() {
        List<SleepData> sleepDataList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("d'/'M");
        if (logging) {
            for (Sleep sleep : sleepList) {
                Log.w("Karl",sleep.toString());
            }
        }
        for (int i = 0; i < sleepList.size(); i++) {
            Sleep todaySleep = sleepList.get(i);
            if (logging){
                Log.w("Karl", "=========================================");
                Log.w("Karl", "Today = " + sdf.format(new Date(todaySleep.getDate())));
            }
            if (sleptToday(todaySleep)) {
                if (logging){
                    Log.w("Karl", "User slept today");
                }
                if (sleptAfterTwelve(todaySleep)) {
                    if (logging){
                        Log.w("Karl", "User slept after twelve");
                    }
                    SleepData sleepData = getSleepData(todaySleep);
                    sleepDataList.add(sleepData);
                    if(i > 0){
                        Sleep yesterdaySleep = sleepList.get(i -1);
                        if (isYesterday(todaySleep, yesterdaySleep)){
                            Log.w("Karl"," HMmm we lso got that strange data of yesterday. Just add it");
                            SleepData strangeData = getSleepDataAfterSix(yesterdaySleep);
                            if (strangeData.getTotalSleep() > 0) {
                                sleepDataList.add(strangeData);
                            }
                        }
                    }
                } else {
                    if (logging){
                        Log.w("Karl", "User Slept before twelve");
                    }
                    if (i > 0) {
                        Sleep yesterdaySleep = sleepList.get(i - 1);
                        if (isYesterday(todaySleep, yesterdaySleep)) {
                            if (logging){
                                Log.w("Karl", "There is yesterdays data! We added it");
                            }
                            SleepData sleepData = merge(getSleepData(todaySleep), getSleepDataAfterSix(yesterdaySleep));
                            sleepDataList.add(sleepData);
                        }else{
                            if (logging){
                                Log.w("Karl", "Strange sleep data! But its added");
                            }
                            SleepData strangeData = getSleepData(todaySleep);
                            sleepDataList.add(strangeData);
                        }
                    }
                }
            } else if ((i + 1) < sleepList.size()) {
                Sleep tomorrow = sleepList.get(i + 1);
                if (!sleptToday(tomorrow)) {
                    if (logging){
                        Log.w("Karl", "Strange sleep data for tomorrow! But its added");
                    }

                    sleepDataList.add(getSleepDataAfterSix(todaySleep));
                }
            } else if (i == sleepList.size() - 1) {
                if (logging){
                    Log.w("Karl", "Strangest sleep data! But its added");
                }
                sleepDataList.add(getSleepDataAfterSix(todaySleep));
            }
        }
        return sleepDataList;
    }

    public void setLoggingOn(boolean logging){
        this.logging = logging;
    }
}
