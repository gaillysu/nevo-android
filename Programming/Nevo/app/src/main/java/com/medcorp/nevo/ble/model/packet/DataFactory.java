/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.medcorp.nevo.ble.model.packet;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

import com.medcorp.nevo.ble.ble.GattAttributes;


/**
 * We don't want the Data Implementations to be visible Outside of this package, so we intantiate all the Implemetnations through this builder
 * @author Hugo
 *
 */
public class DataFactory {
	
	public static SensorData fromBluetoothGattCharacteristic(final BluetoothGattCharacteristic characteristic, final String address) {
        SensorData data=null;

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
         if (UUID.fromString(GattAttributes.NEVO_CALLBACK_CHARACTERISTIC).equals(characteristic.getUuid())){
            data = new NevoRawDataImpl(characteristic, address);
        } else if (UUID.fromString(GattAttributes.NEVO_OTA_CALLBACK_CHARACTERISTIC).equals(characteristic.getUuid())
                   || UUID.fromString(GattAttributes.NEVO_OTA_CHARACTERISTIC).equals(characteristic.getUuid())
                 ){
            data = new NevoFirmwareData(characteristic, address);
        }
        else{ // unknown type
        	data = new SensorData(){

				@Override
				public String getAddress() {
					return "00:00:00:00:00:00";
				}

				@Override
				public String getType() {
					return "undefined type";
				}
        		
        	};
        }
        
        //Log.v(ImazeBT.TAG,"get data from Characteristic,data type is " + data.getType() );
        //end added
        return data;
	}
	
}
