package com.medcorp.nevo.event.validic;

import com.medcorp.nevo.model.Steps;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicUpdateRoutineRecordsModelEvent {
    private Steps steps;


    public ValidicUpdateRoutineRecordsModelEvent(Steps steps) {
        this.steps = steps;
    }

    public Steps getSteps() {
        return steps;
    }

    public void setSteps(Steps steps) {
        this.steps = steps;
    }
}
