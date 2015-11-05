package com.medcorp.nevo.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Karl on 10/31/15.
 */
public class DailySleep {

    private final List<Sleep> sleepList;
    private final int totalSleepTime;

    public DailySleep(DailyHistory history) {
        sleepList = new ArrayList<Sleep>();
        totalSleepTime = history.getTotalSleepTime();
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(history.getDate());
        startDate.set(Calendar.MINUTE, 0);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(history.getDate());
        endDate.set(Calendar.MINUTE, 0);
        for (int hour = 0; hour < 24; hour ++){
            startDate.set(Calendar.HOUR, hour);
            endDate.set(Calendar.HOUR, hour++);
            int hourlyTotalSleep = history.getHourlySleepTime().get(hour);
            int hourlyDeepSleep = history.getHourlDeepTime().get(hour);
            int hourlyLightSleep = history.getHourlyLightTime().get(hour);
            int hourlyWake = history.getHourlyWakeTime().get(hour);
            Sleep sleep = new Sleep(startDate, endDate, hourlyTotalSleep);
            sleep.addSleepBehaviour(SleepBehavior.SLEEP.LIGHT, hourlyLightSleep);
            sleep.addSleepBehaviour(SleepBehavior.SLEEP.NOSLEEP, hourlyWake);
            sleep.addSleepBehaviour(SleepBehavior.SLEEP.DEEP, hourlyDeepSleep);
            sleepList.add(sleep);
        }
    }

    public int getTotalSleepTime() {
        return totalSleepTime;
    }

    public List<Sleep> getSleepList() {
        return sleepList;
    }
}
