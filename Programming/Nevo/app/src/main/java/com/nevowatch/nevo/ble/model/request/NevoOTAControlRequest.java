package com.nevowatch.nevo.ble.model.request;

import com.nevowatch.nevo.ble.ble.GattAttributes;

import java.util.UUID;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public  class NevoOTAControlRequest extends  NevoOTARequest{

    //save different control values, such  as cancel/reset/start...
    private byte[] mControlValues;
    public NevoOTAControlRequest(byte[] ControlValues)
    {
        mControlValues = ControlValues;
    }

    @Override
    public UUID getInputCharacteristicUUID() {
        //for controll request, the input char. is the call back char.
        return UUID.fromString(GattAttributes.NEVO_OTA_CALLBACK_CHARACTERISTIC);
    }

    @Override
    public UUID getOTACharacteristicUUID() {
        return UUID.fromString(GattAttributes.NEVO_OTA_CHARACTERISTIC);
    }

    @Override
    public UUID getNotificationCharacteristicUUID() {
        return UUID.fromString(GattAttributes.NEVO_NOTIFICATION_CHARACTERISTIC);
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
