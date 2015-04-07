package com.nevowatch.nevo.ble.model.packet;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import org.apache.commons.codec.binary.Hex;

import java.util.UUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
/*package*/ class NevoRawDataImpl extends NevoRawData {



	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//This uuid is only usefull as long as the SDK is not fully integrated into the app, we should remove it after
	UUID mUuid;

	String mAddress;

	byte[] mRawData;


	public NevoRawDataImpl(BluetoothGattCharacteristic characteristic, String address) {
		
		mAddress = address;

		mUuid = characteristic.getUuid();
		
		mRawData = characteristic.getValue();
		

		Log.v("Nevo Received", new String(Hex.encodeHex(mRawData)));
	}

	/*
	 * (non-Javadoc)
	 * @see fr.imaze.sdk.model.receive.SensorData#getAddress()
	 */
	@Override
	public String getAddress() {
		return mAddress;
	}

	@Override
	public byte[] getRawData() {
		return mRawData;
	}

	
	
	
}

