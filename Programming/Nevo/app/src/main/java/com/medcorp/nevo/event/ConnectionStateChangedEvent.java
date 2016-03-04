package com.medcorp.nevo.event;

/**
 * Created by karl-john on 3/3/16.
 */
public class ConnectionStateChangedEvent {

    private final boolean connected;

    public ConnectionStateChangedEvent(boolean isConnected) {
        this.connected = isConnected;
    }

    public boolean isConnected() {
        return connected;
    }
}
