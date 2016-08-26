package com.medcorp.model;

/**
 * Created by Jason on 2016/8/26.
 */
public class DailySteps {

    final private long date;

    final private int stepsGoal;

    final private int[] hourlySteps;

    private int dailySteps;

    public DailySteps(long date, int[] hourlySteps, int stepsGoal) {
        this.date = date;
        this.hourlySteps = hourlySteps;
        this.stepsGoal = stepsGoal;
        for(int i=0;i<hourlySteps.length;i++)
        {
            this.dailySteps += hourlySteps[i];
        }
    }

    public long getDate() {
        return date;
    }

    public int[] getHourlySteps() {
        return hourlySteps;
    }

    public int getDailySteps() {
        return dailySteps;
    }

    public int getDailyStepsGoal() {
        return stepsGoal;
    }
}
