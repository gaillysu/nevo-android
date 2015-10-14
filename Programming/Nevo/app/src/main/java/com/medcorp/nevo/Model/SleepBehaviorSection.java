package com.medcorp.nevo.model;

/**
 * Created by Karl on 10/2/15.
 */
public class SleepBehaviorSection {

    private int minutes;
    private SleepBehavior.SLEEP behavior;

    public SleepBehaviorSection(SleepBehavior.SLEEP behavior, int minutes) {
        this.behavior = behavior;
        this.minutes = minutes;
    }

    public SleepBehavior.SLEEP getBehavior() {
        return behavior;
    }

    public int getMinutes() {
        return minutes;
    }
}
