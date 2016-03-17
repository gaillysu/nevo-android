package com.medcorp.nevo.validic.model;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicReadAllRecordsModel {
    ValidicSummary summary;
    ValidicRecordModelBase[] routine;

    public ValidicSummary getSummary() {
        return summary;
    }

    public void setSummary(ValidicSummary summary) {
        this.summary = summary;
    }

    public ValidicRecordModelBase[] getRoutine() {
        return routine;
    }

    public void setRoutine(ValidicRecordModelBase[] routine) {
        this.routine = routine;
    }
}
