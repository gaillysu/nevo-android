package com.medcorp.nevo.model;

import java.util.Calendar;

/**
 * Created by Karl on 10/29/15.
 */
public class Steps {
    private final Calendar startDate;
    private final Calendar endDate;
    private final int steps;

    public Steps(Calendar startDate, Calendar endDate, int steps) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public Calendar getStartDate() {
        return startDate;
    }
}
