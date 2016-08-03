package com.medcorp.ble.model.request;

import android.content.Context;

import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.BLERequestData;

import java.util.UUID;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public abstract  class NevoOTARequest extends BLERequestData {

	public NevoOTARequest(Context context) {
		super(new GattAttributesDataSourceImpl(context));
	}

	@Override
	public UUID getServiceUUID() {
		return super.getOTAServiceUUID();
	}

	@Override
	public UUID getCharacteristicUUID() {
		return super.getOTACallbackCharacteristicUUID();
	}

	@Override
	public UUID getInputCharacteristicUUID() {
		return super.getOTAControlCharacteristicUUID();
	}

}
