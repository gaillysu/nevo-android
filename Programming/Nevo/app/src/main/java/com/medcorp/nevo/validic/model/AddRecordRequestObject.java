package com.medcorp.nevo.validic.model;

import com.google.gson.annotations.Expose;

/**
 * Created by gaillysu on 16/3/17.
 */
public class AddRecordRequestObject {
    @Expose
    ValidicRecord routine;

    @Expose
    String access_token;

    public ValidicRecord getRoutine() {
        return routine;
    }

    public void setRoutine(ValidicRecord routine) {
        this.routine = routine;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
