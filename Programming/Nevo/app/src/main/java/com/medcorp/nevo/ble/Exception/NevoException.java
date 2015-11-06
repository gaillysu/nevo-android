package com.medcorp.nevo.ble.exception;

import com.medcorp.nevo.ble.exception.visitor.NevoExceptionVisitable;
import com.medcorp.nevo.ble.exception.visitor.NevoExceptionVisitor;

/**
 * Created by Karl on 11/5/15.
 */
public abstract class NevoException extends Exception implements NevoExceptionVisitable{

    @Override
    public <T> T accept(NevoExceptionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public abstract int getWarningMessageId();
}
