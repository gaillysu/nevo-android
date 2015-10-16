package com.medcorp.nevo.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.R;

import java.util.UUID;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public abstract  class NevoOTARequest extends NevoRequest {

	public NevoOTARequest(Context context) {
		super(context);
	}

	@Override
	public UUID getServiceUUID() {
		return UUID.fromString(context.getString(R.string.NEVO_OTA_SERVICE));
	}

	@Override
	public UUID getCharacteristicUUID() {
		return UUID.fromString(context.getString(R.string.NEVO_OTA_CALLBACK_CHARACTERISTIC));
	}

	@Override
	public UUID getInputCharacteristicUUID() {
		return UUID.fromString(context.getString(R.string.NEVO_OTA_CONTROL_CHARACTERISTIC));
	}

}
