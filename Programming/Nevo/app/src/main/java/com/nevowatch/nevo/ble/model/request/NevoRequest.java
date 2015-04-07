package com.nevowatch.nevo.ble.model.request;

import java.util.UUID;

import com.nevowatch.nevo.ble.ble.GattAttributes;


/**
 * Created by gaillysu on 15/4/1.
 */

public abstract  class NevoRequest implements  SensorRequest{

	@Override
	public UUID getServiceUUID() {
		return UUID.fromString(GattAttributes.NEVO_SERVICE);
	}

	@Override
	public UUID getCharacteristicUUID() {
		return UUID.fromString(GattAttributes.NEVO_CALLBACK_CHARACTERISTIC);
	}

	@Override
	public UUID getInputCharacteristicUUID() {
		return UUID.fromString(GattAttributes.NEVO_INPUT_CHARACTERISTIC);
	}

	@Override
	public UUID getOTACharacteristicUUID() {
		return UUID.fromString(GattAttributes.NEVO_OTA_CHARACTERISTIC);
	}

	public enum NotificationType {
		SMS, Email, Call,Facebook,Calendar,Wechat
	}
	
}