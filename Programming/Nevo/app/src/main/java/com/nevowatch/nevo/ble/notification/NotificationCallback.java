package com.nevowatch.nevo.ble.notification;

/**
 * Created by gaillysu on 15/4/30.
 */
public interface NotificationCallback {
    /**
     *
     * @param titleID : String ID of title
     * @param msgID : String ID of message
     */
    void process(int titleID, int msgID);
}
