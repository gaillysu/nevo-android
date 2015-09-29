package com.medcorp.nevo.ble.model.request;

import java.util.UUID;

/*
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
public interface SensorRequest {

	/**
	 * @return the target service uuid
	 */
	UUID getServiceUUID();

	/**
	 * @return the target Output characteristic
	 */
	UUID getCharacteristicUUID();

    /**
     * @return the target Input uuid, for setValue
     */
    UUID getInputCharacteristicUUID();

    /**
     * @return the target Input uuid, for OTA write/callback
     */
    UUID getOTACharacteristicUUID();

    /**
     * @return the target Input uuid, for sending Notification, SMS/Call/Email ,...etc
     */
    UUID getNotificationCharacteristicUUID();
    /**
	 * @return the raw data to be sent
	 */
	byte[] getRawData();
	
    /**
	 * @return the raw data to be sent, more  packets
	 */
	byte[][] getRawDataEx();
	
	 /**
		 * @return the command 's value
	*/
	byte  getHeader();

}
