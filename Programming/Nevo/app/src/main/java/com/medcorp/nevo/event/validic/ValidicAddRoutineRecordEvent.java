package com.medcorp.nevo.event.validic;

import com.medcorp.nevo.network.validic.model.ValidicRoutineRecordModel;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicAddRoutineRecordEvent {

    private ValidicRoutineRecordModel validicRoutineRecordModel;


    public ValidicRoutineRecordModel getValidicRoutineRecordModel() {
        return validicRoutineRecordModel;
    }

    public void setValidicRoutineRecordModel(ValidicRoutineRecordModel validicRoutineRecordModel) {
        this.validicRoutineRecordModel = validicRoutineRecordModel;
    }

    public ValidicAddRoutineRecordEvent(ValidicRoutineRecordModel validicRoutineRecordModel) {

        this.validicRoutineRecordModel = validicRoutineRecordModel;
    }


}
