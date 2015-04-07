package com.nevowatch.nevo.ble.model.request;

import java.util.UUID;

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
