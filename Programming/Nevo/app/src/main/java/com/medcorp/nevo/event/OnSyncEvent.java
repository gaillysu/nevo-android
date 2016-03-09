package com.medcorp.nevo.event;

/**
 * Created by karl-john on 3/3/16.
 */
public class OnSyncEvent {

    public enum SYNC_EVENT{
        STARTED,
        STOPPED
    }

    public final SYNC_EVENT status;

    public OnSyncEvent(SYNC_EVENT status) {
        this.status = status;
    }

    public SYNC_EVENT getStatus() {
        return status;
    }
}
