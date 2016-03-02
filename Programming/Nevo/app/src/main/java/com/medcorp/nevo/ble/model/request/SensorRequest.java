package com.medcorp.nevo.ble.model.request;

import java.util.UUID;

/*
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
public interface SensorRequest {

    /**
     * @return the target service uuid
     */
    public UUID getServiceUUID();

    /**
     * @return the target Output characteristic
     */
    public UUID getCharacteristicUUID();

    /**
     * @return the target Input uuid, for setValue
     */
    public UUID getInputCharacteristicUUID();

    /**
     * @return the target Input uuid, for OTA write/callback
     */
    public UUID getOTACharacteristicUUID();

    /**
     * @return the target Input uuid, for sending Notification, SMS/Call/Email ,...etc
     */
    public UUID getNotificationCharacteristicUUID();
    /**
     * @return the raw data to be sent
     */
    public byte[] getRawData();

    /**
     * @return the raw data to be sent, more  packets
     */
    public byte[][] getRawDataEx();

    /**
     * @return the command 's value
     */
    public byte  getHeader();

}
