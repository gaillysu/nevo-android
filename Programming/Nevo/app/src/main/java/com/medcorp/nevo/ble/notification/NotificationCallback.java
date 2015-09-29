package com.medcorp.nevo.ble.notification;

/**
 * Created by gaillysu on 15/4/30.
 */
public interface NotificationCallback {
    /**
     *
     * @param e : Exception when got error, link@QuickBTUnBindNevoException,QuickBTSendTimeoutException
     */
    void onErrorDetected(Exception e);
}
