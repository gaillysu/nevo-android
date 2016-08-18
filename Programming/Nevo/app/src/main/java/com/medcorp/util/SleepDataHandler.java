package com.medcorp.util;

import com.medcorp.model.Sleep;
import com.medcorp.model.SleepData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Karl on 11/27/15.
 */
public class SleepDataHandler {

    private List<Sleep> sleepList;

    public SleepDataHandler(List<Sleep> sleepList) {
        this.sleepList = sleepList;
    }

    public SleepDataHandler(List<Sleep> sleepList, boolean sortByDate) {
        this.sleepList = sleepList;
        if (sortByDate){
            Collections.sort(this.sleepList, new SleepSorter());
        }

    }

    private SleepData getSleepData(Sleep todaySleep) {
        int lightSleep = 0;
        int deepSleep = 0;
        int wake = 0;
        long sleepStart = 0;
        long sleepEnd = 0;

        List<Integer> wakeTimeList = new ArrayList<Integer>();
        List<Integer> lightTimeList = new ArrayList<Integer>();
        List<Integer> deepTimeList = new ArrayList<Integer>();

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
            JSONArray hourlySleep = new JSONArray(todaySleep.getHourlySleep());
            int start = 0;
            int end = 17;
            for (int i = 0; i < 18 && hourlySleep.length() > 18; i++) {
                int theMinutes = Integer.parseInt(hourlySleep.getString(i));
                if(theMinutes>0)
                {
                    sleepStart = todaySleep.getDate() + ((i+1)*60-theMinutes)*60*1000;
                    start = i;
                    break;
                }
            }
            for (int i = 17; i >=0; i--) {
                int theMinutes = Integer.parseInt(hourlySleep.getString(i));
                if(theMinutes>0)
                {
                    sleepEnd = todaySleep.getDate() + (i*60+theMinutes)*60*1000;
                    end = i;
                    break;
                }
            }
            for(int i=start;i<=end;i++)
            {
                wakeTimeList.add(Integer.parseInt(hourlyWake.getString(i)));
                lightTimeList.add(Integer.parseInt(hourlyLight.getString(i)));
                deepTimeList.add(Integer.parseInt(hourlyDeep.getString(i)));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        SleepData sleepData = new SleepData(deepSleep, lightSleep, wake, todaySleep.getCreatedDate(),sleepStart,sleepEnd);
        sleepData.setHourlyWake(wakeTimeList.toString());
        sleepData.setHourlyLight(lightTimeList.toString());
        sleepData.setHourlyDeep(deepTimeList.toString());
        return sleepData;
    }

    private boolean sleptToday(Sleep sleep) {
        try {
            JSONArray hourlySleep = new JSONArray(sleep.getHourlySleep());
            for (int i = 0; i < 18 && hourlySleep.length() > 18; i++) {
                if (Integer.parseInt(hourlySleep.getString(i)) > 0) {
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
            JSONArray hourlySleep = new JSONArray(sleep.getHourlySleep());
            totalFirstHour = Integer.parseInt(hourlySleep.getString(0));
            if (totalFirstHour == 60) {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    //Sleep.getDate() return the day's time stamp, YYYY/MM/DD 00:00:00
    private boolean isYesterday(Sleep todaySleep, Sleep yesterdaySleep) {
        //fix a bug 2016.1.1 sleep result got error
        if((todaySleep.getDate()-yesterdaySleep.getDate()) == 24*60*60*1000)
        {
            return true;
        }
        return false;
    }

    private SleepData getSleepDataAfterSix(Sleep yesterdaySleep) {
        int lightSleep = 0;
        int deepSleep = 0;
        int wake = 0;
        long sleepStart = 0;
        long sleepEnd = 0;
        List<Integer> wakeTimeList = new ArrayList<Integer>();
        List<Integer> lightTimeList = new ArrayList<Integer>();
        List<Integer> deepTimeList = new ArrayList<Integer>();
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
            JSONArray hourlySleep = new JSONArray(yesterdaySleep.getHourlySleep());
            int start = 18;
            int end = 23;
            for (int i = 18; i < hourlySleep.length(); i++) {
                int theMinutes = Integer.parseInt(hourlySleep.getString(i));
                if(theMinutes>0)
                {
                    sleepStart = yesterdaySleep.getDate() + ((i+1)*60-theMinutes)*60*1000;
                    start = i;
                    break;
                }
            }
            for (int i = 23; i >=18 && hourlySleep.length()>=23; i--) {
                int theMinutes = Integer.parseInt(hourlySleep.getString(i));
                if(theMinutes>0)
                {
                    sleepEnd = yesterdaySleep.getDate() + (i*60+theMinutes)*60*1000;
                    end = i;
                    break;
                }
            }
            for(int i=start;i<=end;i++)
            {
                wakeTimeList.add(Integer.parseInt(hourlyWake.getString(i)));
                lightTimeList.add(Integer.parseInt(hourlyLight.getString(i)));
                deepTimeList.add(Integer.parseInt(hourlyDeep.getString(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        int totalSleep = lightSleep + deepSleep + wake;
        if(totalSleep > 0) {
            SleepData sleepData = new SleepData(deepSleep, lightSleep, wake, yesterdaySleep.getDate(),sleepStart,sleepEnd);
            sleepData.setHourlyWake(wakeTimeList.toString());
            sleepData.setHourlyLight(lightTimeList.toString());
            sleepData.setHourlyDeep(deepTimeList.toString());
            return sleepData;
        }else{
            return new SleepData(0, 0, 0, yesterdaySleep.getDate(),0,0);
        }
    }

    private SleepData merge(SleepData today, SleepData yesterday) {
        if (yesterday.getTotalSleep() == 0){
            return today;
        }
        SleepData sleepData = new  SleepData((today.getDeepSleep() + yesterday.getDeepSleep()), (today.getLightSleep() + yesterday.getLightSleep()), (today.getAwake() + yesterday.getAwake()),today.getDate(),yesterday.getSleepStart(),today.getSleepEnd());
        List<Integer> wakeTimeList = new ArrayList<Integer>();
        List<Integer> lightTimeList = new ArrayList<Integer>();
        List<Integer> deepTimeList = new ArrayList<Integer>();

        try {
            JSONArray   hourlyWake = new JSONArray(yesterday.getHourlyWake());
            for (int i = 0; i < hourlyWake.length(); i++) {
                wakeTimeList.add(Integer.parseInt(hourlyWake.getString(i)));
            }
            hourlyWake = new JSONArray(today.getHourlyWake());
            for (int i = 0; i < hourlyWake.length(); i++) {
                wakeTimeList.add(Integer.parseInt(hourlyWake.getString(i)));
            }

            JSONArray   hourlyLight = new JSONArray(yesterday.getHourlyLight());
            for (int i = 0; i < hourlyLight.length(); i++) {
                lightTimeList.add(Integer.parseInt(hourlyLight.getString(i)));
            }
            hourlyLight = new JSONArray(today.getHourlyLight());
            for (int i = 0; i < hourlyLight.length(); i++) {
                lightTimeList.add(Integer.parseInt(hourlyLight.getString(i)));
            }

            JSONArray   hourlyDeep = new JSONArray(yesterday.getHourlyDeep());
            for (int i = 0; i < hourlyDeep.length(); i++) {
                deepTimeList.add(Integer.parseInt(hourlyDeep.getString(i)));
            }
            hourlyDeep = new JSONArray(today.getHourlyDeep());
            for (int i = 0; i < hourlyDeep.length(); i++) {
                deepTimeList.add(Integer.parseInt(hourlyDeep.getString(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sleepData.setHourlyWake(wakeTimeList.toString());
        sleepData.setHourlyLight(lightTimeList.toString());
        sleepData.setHourlyDeep(deepTimeList.toString());
        return sleepData;
    }

    public List<SleepData> getSleepData() {
        List<SleepData> sleepDataList = new ArrayList<>();
        for (int i = 0; i < sleepList.size(); i++) {
            Sleep todaySleep = sleepList.get(i);
            if (sleptToday(todaySleep)) {
                if (sleptAfterTwelve(todaySleep)) {
                    SleepData sleepData = getSleepData(todaySleep);
                    sleepDataList.add(sleepData);
                    if(i > 0){
                        Sleep yesterdaySleep = sleepList.get(i -1);
                        if (isYesterday(todaySleep, yesterdaySleep)){
                            SleepData strangeData = getSleepDataAfterSix(yesterdaySleep);
                            if (strangeData.getTotalSleep() > 0) {
                                sleepDataList.add(strangeData);
                            }
                        }
                    }else if (i == sleepList.size()-1){
                        if(sleptAfterTwelve(todaySleep)){
                            SleepData sleepData2 = getSleepDataAfterSix(todaySleep);
                            if (sleepData2.getTotalSleep() > 0) {
                                sleepDataList.add(sleepData2);
                            }
                        }
                    }
                } else {
                    if (i > 0) {
                        Sleep yesterdaySleep = sleepList.get(i - 1);
                        if (isYesterday(todaySleep, yesterdaySleep)) {
                            sleepDataList.add(merge(getSleepData(todaySleep), getSleepDataAfterSix(yesterdaySleep)));
                        }else{
                            sleepDataList.add(getSleepData(todaySleep));
                        }
                    }
                }
            } else if ((i + 1) < sleepList.size()) {
                Sleep tomorrow = sleepList.get(i + 1);
                if (!sleptToday(tomorrow)) {
                    sleepDataList.add(getSleepDataAfterSix(todaySleep));
                }
            } else if (i == sleepList.size() - 1) {
                sleepDataList.add(getSleepDataAfterSix(todaySleep));
            }
        }
        return sleepDataList;
    }
}