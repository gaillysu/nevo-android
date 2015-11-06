package com.medcorp.nevo.ble.exception.visitor;

import com.medcorp.nevo.ble.exception.BLEConnectTimeoutException;
import com.medcorp.nevo.ble.exception.BLENotSupportedException;
import com.medcorp.nevo.ble.exception.BLEUnstableException;
import com.medcorp.nevo.ble.exception.BluetoothDisabledException;
import com.medcorp.nevo.ble.exception.NevoException;
import com.medcorp.nevo.ble.exception.QuickBTSendTimeoutException;
import com.medcorp.nevo.ble.exception.QuickBTUnBindNevoException;

/**
 * Created by Karl on 11/5/15.
 */
public interface NevoExceptionVisitor<T> {
    
    T visit(QuickBTUnBindNevoException e);

    T visit(BLEConnectTimeoutException e);

    T visit(BLENotSupportedException e);

    T visit(BLEUnstableException e);

    T visit(BluetoothDisabledException e);

    T visit(QuickBTSendTimeoutException e);

    T visit(NevoException e);
}
