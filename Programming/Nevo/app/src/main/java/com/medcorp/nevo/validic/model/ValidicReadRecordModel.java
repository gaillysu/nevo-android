package com.medcorp.nevo.validic.model;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicReadRecordModel {
    ValidicSummary summary;
    ValidicRecordModelBase[] fitness;

    public ValidicSummary getSummary() {
        return summary;
    }

    public void setSummary(ValidicSummary summary) {
        this.summary = summary;
    }

    public ValidicRecordModelBase[] getFitness() {
        return fitness;
    }

    public void setFitness(ValidicRecordModelBase[] fitness) {
        this.fitness = fitness;
    }
}
