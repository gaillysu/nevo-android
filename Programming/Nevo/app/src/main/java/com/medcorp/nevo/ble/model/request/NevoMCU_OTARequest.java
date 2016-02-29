package com.medcorp.nevo.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.RequestData;

import java.util.UUID;


/**
 * Created by gaillysu on 15/6/8.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public abstract  class NevoMCU_OTARequest extends RequestData {

    public NevoMCU_OTARequest(Context context) {
        super(new GattAttributesDataSourceImpl(context));
    }

    @Override
    public UUID getServiceUUID() {
        return getServiceUUID();
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
