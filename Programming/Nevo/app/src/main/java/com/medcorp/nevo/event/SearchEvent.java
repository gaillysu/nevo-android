package com.medcorp.nevo.event;

/**
 * Created by karl-john on 4/3/16.
 */
public class SearchEvent {

    public enum SEARCH_STATUS{
        SEARCHING,
        CONNECTED,
        DISCONNECTED,
        FAILED,
        FOUND
    }

    private final SEARCH_STATUS status;

    public SearchEvent(SEARCH_STATUS status) {
        this.status = status;
    }

    public SEARCH_STATUS getStatus() {
        return status;
    }
}
