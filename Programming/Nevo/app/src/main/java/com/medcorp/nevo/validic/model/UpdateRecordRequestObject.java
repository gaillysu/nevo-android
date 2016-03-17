package com.medcorp.nevo.validic.model;

import com.google.gson.annotations.Expose;

/**
 * Created by gaillysu on 16/3/17.
 */
public class UpdateRecordRequestObject {

    @Expose
    String access_token;

    @Expose
    StepsModel routine;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public StepsModel getRoutine() {
        return routine;
    }

    public void setRoutine(StepsModel routine) {
        this.routine = routine;
    }
}
