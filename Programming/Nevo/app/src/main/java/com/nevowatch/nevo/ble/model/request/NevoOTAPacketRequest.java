package com.nevowatch.nevo.ble.model.request;

import com.nevowatch.nevo.ble.ble.GattAttributes;

import java.util.UUID;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public  class NevoOTAPacketRequest extends  NevoOTARequest{

    private byte[] mPacket;
    public NevoOTAPacketRequest(byte[] packet)
    {
        mPacket = packet;
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
        return mPacket;
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
