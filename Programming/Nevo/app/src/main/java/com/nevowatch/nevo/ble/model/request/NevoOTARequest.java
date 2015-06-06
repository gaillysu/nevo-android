package com.nevowatch.nevo.ble.model.request;

import com.nevowatch.nevo.ble.ble.GattAttributes;

import java.util.UUID;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public abstract  class NevoOTARequest implements  SensorRequest{

	@Override
	public UUID getServiceUUID() {
		return UUID.fromString(GattAttributes.NEVO_OTA_SERVICE);
	}

	@Override
	public UUID getCharacteristicUUID() {
		return UUID.fromString(GattAttributes.NEVO_OTA_CALLBACK_CHARACTERISTIC);
	}

	@Override
	public UUID getInputCharacteristicUUID() {
		return UUID.fromString(GattAttributes.NEVO_OTA_CONTROL_CHARACTERISTIC);
	}

}
