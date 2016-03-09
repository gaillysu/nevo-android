package com.medcorp.nevo.event;

/**
 * Created by karl-john on 3/3/16.
 */
public class RequestResponseEvent {

    private boolean success;

    public RequestResponseEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}