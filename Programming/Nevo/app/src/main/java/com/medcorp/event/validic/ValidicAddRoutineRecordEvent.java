package com.medcorp.event.validic;

import com.medcorp.model.Steps;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicAddRoutineRecordEvent {

    private Steps steps;

    public ValidicAddRoutineRecordEvent(Steps steps) {
        this.steps = steps;
    }

    public Steps getSteps() {
        return steps;
    }

    public void setSteps(Steps steps) {
        this.steps = steps;
    }
}