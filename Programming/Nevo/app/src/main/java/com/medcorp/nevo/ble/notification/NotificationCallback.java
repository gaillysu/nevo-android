package com.medcorp.nevo.ble.notification;

import com.medcorp.nevo.ble.exception.NevoException;

/**
 * Created by gaillysu on 15/4/30.
 */
public interface NotificationCallback {
    /**
     *
     * @param e : Exception when got error, link@QuickBTUnBindNevoException,QuickBTSendTimeoutException
     */
    public void onErrorDetected(NevoException e);
}
