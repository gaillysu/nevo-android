package com.medcorp.nevo.model;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Karl on 10/29/15.
 */
public class Sleep {

    private Calendar starDate;
    private Calendar endDate;
    private int totalSleep;
    private List<SleepBehaviorSection> sleepBehaviorSectionList;

    public Sleep(Calendar starDate, Calendar endDate, int totalSleep) {
        this.starDate = starDate;
        this.endDate = endDate;
        this.totalSleep = totalSleep;
    }

    public void addSleepBehaviour(SleepBehavior.SLEEP behavior, int duration){
        sleepBehaviorSectionList.add(new SleepBehaviorSection(behavior, duration));
    }

}
