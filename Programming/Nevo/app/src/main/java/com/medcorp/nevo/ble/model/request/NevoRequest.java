package com.medcorp.nevo.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.R;

import java.util.UUID;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public abstract  class NevoRequest implements  SensorRequest{

	protected Context context;

	public NevoRequest(Context context) {
		this.context = context;
	}

	@Override
	public UUID getServiceUUID() {
		return UUID.fromString(context.getString(R.string.NEVO_SERVICE));
	}

	@Override
	public UUID getCharacteristicUUID() {
		return UUID.fromString(context.getString(R.string.NEVO_CALLBACK_CHARACTERISTIC));
	}

	@Override
	public UUID getInputCharacteristicUUID() {
		return UUID.fromString(context.getString(R.string.NEVO_INPUT_CHARACTERISTIC));
	}

	@Override
	public UUID getOTACharacteristicUUID() {
		return UUID.fromString(context.getString(R.string.NEVO_OTA_CHARACTERISTIC));
	}

	@Override
	public UUID getNotificationCharacteristicUUID() {
		return UUID.fromString(context.getString(R.string.NEVO_NOTIFICATION_CHARACTERISTIC));
	}

}
