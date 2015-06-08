package com.nevowatch.nevo.ble.model.request;

import com.nevowatch.nevo.ble.ble.GattAttributes;

import java.util.UUID;


/**
 * Created by gaillysu on 15/6/8.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public abstract  class NevoMCU_OTARequest extends NevoRequest {

    @Override
    public UUID getServiceUUID() {
        return UUID.fromString(GattAttributes.NEVO_SERVICE);
    }

    @Override
    public UUID getCharacteristicUUID() {
        return getOTACharacteristicUUID();
    }

    @Override
    public UUID getInputCharacteristicUUID() {
        return getOTACharacteristicUUID();
    }
}
