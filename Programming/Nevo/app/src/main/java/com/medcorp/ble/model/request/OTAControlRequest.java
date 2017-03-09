package com.medcorp.ble.model.request;

import android.content.Context;

import java.util.UUID;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public  class OTAControlRequest extends OTARequest {

    //save different control values, such  as cancel/reset/start...
    private byte[] mControlValues;
    public OTAControlRequest(Context context, byte[] ControlValues)
    {
        super(context);
        mControlValues = ControlValues;
    }

    @Override
    public UUID getInputCharacteristicUUID() {
        //for controll request, the input char. is the call back char.
        return super.getCharacteristicUUID();
    }

    @Override
    public byte[] getRawData() {
        return mControlValues;
    }

    @Override
    public byte[][] getRawDataEx() {
        //no used function
        return null;
    }

    @Override
    public byte getHeader() {
        //no used value
        return 0;
    }

}
