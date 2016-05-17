package com.medcorp.nevo.event.validic;

import com.medcorp.nevo.network.validic.model.ValidicReadMoreRoutineRecordsModel;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicReadMoreRoutineRecordsModelEvent {
    private ValidicReadMoreRoutineRecordsModel validicReadMoreRoutineRecordsModel;

    public ValidicReadMoreRoutineRecordsModelEvent(ValidicReadMoreRoutineRecordsModel validicReadMoreRoutineRecordsModel) {
        this.validicReadMoreRoutineRecordsModel = validicReadMoreRoutineRecordsModel;
    }

    public ValidicReadMoreRoutineRecordsModel getValidicReadMoreRoutineRecordsModel() {
        return validicReadMoreRoutineRecordsModel;
    }

    public void setValidicReadMoreRoutineRecordsModel(ValidicReadMoreRoutineRecordsModel validicReadMoreRoutineRecordsModel) {
        this.validicReadMoreRoutineRecordsModel = validicReadMoreRoutineRecordsModel;
    }
}
