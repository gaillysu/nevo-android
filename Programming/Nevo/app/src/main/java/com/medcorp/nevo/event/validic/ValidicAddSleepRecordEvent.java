package com.medcorp.nevo.event.validic;

import com.medcorp.nevo.network.validic.model.ValidicSleepRecordModel;

/**
 * Created by karl-john on 17/5/16.
 */
public class ValidicAddSleepRecordEvent {
    private ValidicSleepRecordModel validicSleepRecordModel;

    public ValidicAddSleepRecordEvent(ValidicSleepRecordModel validicSleepRecordModel) {
        this.validicSleepRecordModel = validicSleepRecordModel;
    }

    public ValidicSleepRecordModel getValidicSleepRecordModel() {
        return validicSleepRecordModel;
    }

    public void setValidicSleepRecordModel(ValidicSleepRecordModel validicSleepRecordModel) {
        this.validicSleepRecordModel = validicSleepRecordModel;
    }
}
