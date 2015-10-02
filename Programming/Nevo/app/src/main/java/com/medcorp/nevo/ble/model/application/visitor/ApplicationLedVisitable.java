package com.medcorp.nevo.ble.model.application.visitor;

/**
 * Created by Karl on 10/2/15.
 */
public interface ApplicationLedVisitable {

    public <T> T accept(ApplicationLedVisitor<T> visitor);
}
