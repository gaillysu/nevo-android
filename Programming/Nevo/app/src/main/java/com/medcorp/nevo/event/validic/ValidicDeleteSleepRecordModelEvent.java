package com.medcorp.nevo.event.validic;

import com.medcorp.nevo.network.validic.model.ValidicDeleteSleepRecordModel;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicDeleteSleepRecordModelEvent {
    private ValidicDeleteSleepRecordModel validicDeleteSleepRecordModel;

    public ValidicDeleteSleepRecordModelEvent(ValidicDeleteSleepRecordModel validicDeleteSleepRecordModel) {
        this.validicDeleteSleepRecordModel = validicDeleteSleepRecordModel;
    }

    public ValidicDeleteSleepRecordModel getValidicDeleteSleepRecordModel() {
        return validicDeleteSleepRecordModel;
    }

    public void setValidicDeleteSleepRecordModel(ValidicDeleteSleepRecordModel validicDeleteSleepRecordModel) {
        this.validicDeleteSleepRecordModel = validicDeleteSleepRecordModel;
    }
}
