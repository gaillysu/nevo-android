package com.medcorp.nevo.ble.model.request;

import android.content.Context;

import com.medcorp.nevo.ble.datasource.GattAttributesDataSourceImpl;

import net.medcorp.library.ble.model.request.RequestData;

import java.util.UUID;


/**
 * Created by gaillysu on 15/4/1.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

public abstract  class NevoOTARequest extends RequestData {

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
