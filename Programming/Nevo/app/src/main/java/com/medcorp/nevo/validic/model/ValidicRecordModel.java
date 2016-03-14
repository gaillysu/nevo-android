package com.medcorp.nevo.validic.model;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicRecordModel {
    String code;
    String message;
    ValidicRecordModelBase routine;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ValidicRecordModelBase getRoutine() {
        return routine;
    }

    public void setRoutine(ValidicRecordModelBase routine) {
        this.routine = routine;
    }
}
