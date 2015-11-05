package com.medcorp.nevo.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Karl on 10/29/15.
 */
public class DailySteps {

    private List<Steps> stepsList;
    private int totalSteps;

    public DailySteps(DailyHistory history) {
        this.totalSteps = history.getTotalSteps();
        this.stepsList = new ArrayList<Steps>();
        int time = 0;
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(history.getDate());
        startDate.set(Calendar.MINUTE, 0);
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(history.getDate());
        endDate.set(Calendar.MINUTE, 0);
        for(int steps: history.getHourlySteps()){
            startDate.set(Calendar.HOUR, time);
            endDate.set(Calendar.HOUR, time+1);
            stepsList.add(new Steps(startDate, endDate, steps));
            time+=1;
        }
    }

    public List<Steps> getStepsList() {
        return stepsList;
    }

    public int getTotalSteps() {
        return totalSteps;
    }
}
