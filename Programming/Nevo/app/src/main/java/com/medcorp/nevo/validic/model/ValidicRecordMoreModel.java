package com.medcorp.nevo.validic.model;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicRecordMoreModel {
    ValidicRecordModelBase[] fitness;
    ValidicRecordErrorModel[] errors;

    public ValidicRecordModelBase[] getFitness() {
        return fitness;
    }

    public void setFitness(ValidicRecordModelBase[] fitness) {
        this.fitness = fitness;
    }

    public ValidicRecordErrorModel[] getErrors() {
        return errors;
    }

    public void setErrors(ValidicRecordErrorModel[] errors) {
        this.errors = errors;
    }
}
