package com.medcorp.nevo.event;

/**
 * Created by karl-john on 3/3/16.
 */
public class OnRequestResponse {

    private boolean success;

    public OnRequestResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
