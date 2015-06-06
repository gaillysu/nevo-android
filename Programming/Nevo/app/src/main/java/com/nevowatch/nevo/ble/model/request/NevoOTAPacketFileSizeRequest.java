package com.nevowatch.nevo.ble.model.request;

import com.nevowatch.nevo.ble.ble.GattAttributes;

import java.util.UUID;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public  class NevoOTAPacketFileSizeRequest extends  NevoOTARequest{

    private int mFilelen;

    public NevoOTAPacketFileSizeRequest(int filelen)
    {
        mFilelen = filelen;
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
        //little endian mode
        byte[] mPacket = new byte[]{0,0,0,0,0,0,0,0,
                (byte)(mFilelen & 0xFF),
                (byte)((mFilelen>>8)&0xFF),
                (byte)((mFilelen>>16)&0xFF),
                (byte)((mFilelen>>24)&0xFF)};
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
