package com.medcorp.nevo.ble.model.application;


import com.medcorp.nevo.ble.model.application.visitor.ApplicationLedVisitable;

/**
 * Created by Karl on 9/30/15.
 */
public abstract  class  ApplicationLed implements ApplicationLedVisitable{
    public abstract String getTag();

}
