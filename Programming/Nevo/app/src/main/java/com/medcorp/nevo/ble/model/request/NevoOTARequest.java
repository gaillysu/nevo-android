package com.medcorp.nevo.ble.model.request;

import com.medcorp.nevo.ble.ble.GattAttributes;

import java.util.UUID;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public abstract  class NevoOTARequest extends NevoRequest {

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
