package com.medcorp.nevo.ble.exception.visitor;

/**
 * Created by Karl on 11/5/15.
 */
public interface NevoExceptionVisitable{
    public <T> T accept (NevoExceptionVisitor<T> visitor);
}
