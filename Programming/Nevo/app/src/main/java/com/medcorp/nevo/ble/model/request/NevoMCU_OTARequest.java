package com.medcorp.nevo.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.R;

import java.util.UUID;


/**
 * Created by gaillysu on 15/6/8.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public abstract  class NevoMCU_OTARequest extends NevoRequest {

    public NevoMCU_OTARequest(Context context) {
        super(context);
    }

    @Override
    public UUID getServiceUUID() {
        return UUID.fromString(context.getString(R.string.NEVO_SERVICE));
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
