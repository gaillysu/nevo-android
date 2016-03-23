package com.medcorp.nevo.validic.model.sleep;


import com.medcorp.nevo.validic.model.ValidicSummary;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicReadSleepRecordModel {
    ValidicSummary summary;
    ValidicSleepRecordModelBase sleep;

    public ValidicSummary getSummary() {
        return summary;
    }

    public void setSummary(ValidicSummary summary) {
        this.summary = summary;
    }

    public ValidicSleepRecordModelBase getSleep() {
        return sleep;
    }

    public void setSleep(ValidicSleepRecordModelBase sleep) {
        this.sleep = sleep;
    }
}
