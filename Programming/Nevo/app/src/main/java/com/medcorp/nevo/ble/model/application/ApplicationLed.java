package com.medcorp.nevo.ble.model.application;


import com.medcorp.nevo.ble.model.application.visitor.ApplicationLedVisitor;
import com.medcorp.nevo.ble.model.color.NevoLed;

/**
 * Created by Karl on 9/30/15.
 */
public interface ApplicationLed {
    public NevoLed getLed();
    public String getTag();
    public void accept(ApplicationLedVisitor visitor );

}
