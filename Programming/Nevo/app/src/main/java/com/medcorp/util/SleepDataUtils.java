package com.medcorp.util;

import android.util.Log;

import com.medcorp.model.SleepData;

import org.joda.time.DateTime;
import org.json.JSONArray;

public class SleepDataUtils{
    public static SleepData mergeYesterdayToday(SleepData yesterday,SleepData today){

        DateTime todayTime = new DateTime(today.getDate());
        DateTime yesterdayTime = new DateTime(yesterday.getDate());
        DateTime todayMinusOne = todayTime.minusDays(1);
        if (todayMinusOne.getMillis() != yesterdayTime.getMillis()){
            Log.w("Karl","Hey, something went wrong here!");
            return new SleepData(0,0,0,0);
        }
        int i1 = today.getAwake() + yesterday.getAwake();
        int i2 = today.getLightSleep() + yesterday.getLightSleep();
        int i3 = today.getDeepSleep() + yesterday.getDeepSleep();
        Log.w("Karl","Total = " + i1);
        Log.w("Karl","Total = " + i2);
        Log.w("Karl","Total = " + i3);
        Log.w("Karl","Total = " + (i1+i2+i3));
        SleepData sleepData = new SleepData(today.getAwake() + yesterday.getAwake(), today.getLightSleep() + yesterday.getLightSleep(), today.getDeepSleep() + yesterday.getDeepSleep(), today.getDate());
        sleepData.setSleepStart(yesterday.getSleepStart());
        sleepData.setSleepEnd(today.getSleepEnd());
        sleepData.setHourlyWake(mergeSleepData(today.getHourlyWakeInt(),yesterday.getHourlyWakeInt()));
        sleepData.setHourlyLight(mergeSleepData(today.getHourlyLightInt(),yesterday.getHourlyLightInt()));
        sleepData.setHourlyDeep(mergeSleepData(today.getHourlyDeepInt(),yesterday.getHourlyDeepInt()));
        return sleepData;
    }

    private static String mergeSleepData (int[] sleepArray1, int[] sleepArray2){
        JSONArray mergedSleepData = new JSONArray();
        for (int i = 0; i < sleepArray1.length; i++){
            mergedSleepData.put(sleepArray1[i]);
        }

        for (int i = 0; i < sleepArray2.length; i++){
            mergedSleepData.put(sleepArray2[i]);
        }
        return mergedSleepData.toString();
    }
}
