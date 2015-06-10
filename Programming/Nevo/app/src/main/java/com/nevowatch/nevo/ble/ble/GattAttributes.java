/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.nevowatch.nevo.ble.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.nevowatch.nevo.ble.util.Optional;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
/**
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
public class GattAttributes {

    public enum SupportedService {
        nevo,
        nevo_ota,
        allService
    }

    private static HashMap<String, String> attributes = new HashMap<String, String>();
    
    public static String DEVICEINFO_UDID 	= "0000180a-0000-1000-8000-00805f9b34fb";
    public static String DEVICEINFO_FIRMWARE_VERSION 	= "00002a26-0000-1000-8000-00805f9b34fb";
    public static String DEVICEINFO_SOFTWARE_VERSION 	= "00002a28-0000-1000-8000-00805f9b34fb";

    // Client Characteristic
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    //Nevo communicaiton service
    public static String NEVO_SERVICE = "f0ba3020-6cac-4c99-9089-4b0a1df45002";

    //This service will return us all the data we ask for through notification
    public static String NEVO_CALLBACK_CHARACTERISTIC = "f0ba3021-6cac-4c99-9089-4b0a1df45002";
    public static String NEVO_INPUT_CHARACTERISTIC = "f0ba3022-6cac-4c99-9089-4b0a1df45002";
    public static String NEVO_OTA_CHARACTERISTIC = "f0ba3023-6cac-4c99-9089-4b0a1df45002";
    public static String NEVO_NOTIFICATION_CHARACTERISTIC = "f0ba3024-6cac-4c99-9089-4b0a1df45002";

    public static String NEVO_OTA_SERVICE = "00001530-1212-efde-1523-785feabcd123";
    public static String NEVO_OTA_CONTROL_CHARACTERISTIC = "00001532-1212-efde-1523-785feabcd123";
    public static String NEVO_OTA_CALLBACK_CHARACTERISTIC = "00001531-1212-efde-1523-785feabcd123";


    static 
    {
        // Sample Services.
    	attributes.put(DEVICEINFO_UDID, "Device Information");
        attributes.put(NEVO_SERVICE, "Nevo Service");
    }

    public static String lookup(String uuid, String defaultName) 
    {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
    
    
    public static boolean supportedBLEService(String uuid) {
		if (uuid.equals(GattAttributes.NEVO_SERVICE))
			return true;
		return false;
	}
    

    public static boolean supportedBLECharacteristic(String uuid){
		if (uuid.equals(GattAttributes.NEVO_CALLBACK_CHARACTERISTIC)
           || uuid.equals(GattAttributes.NEVO_OTA_CALLBACK_CHARACTERISTIC)
           || uuid.equals(GattAttributes.NEVO_OTA_CHARACTERISTIC))
			return true;
		return false;
    }
    
    public static boolean shouldInitBLECharacteristic(String uuid){
		return false;
    }
    
    public static BluetoothGattCharacteristic initBLECharacteristic(String uuid, BluetoothGattCharacteristic characteristic){
		return characteristic;
    }
    
    public static boolean supportedBLEService(List<UUID> uuids) {
    	boolean supported = false;
    	for(UUID uuid : uuids){
    		if(supportedBLEService(uuid.toString())) {supported = true;break;}
    	}
		return supported;
	}
    
    public static Optional<SupportedService>  TransferUUID2SupportedService(String uuid)
    {
        if(uuid.equals(GattAttributes.NEVO_SERVICE))
            return new Optional<SupportedService>(SupportedService.nevo);
        if(uuid.equals(GattAttributes.NEVO_OTA_SERVICE))
            return new Optional<SupportedService>(SupportedService.nevo_ota);
       return new Optional<SupportedService>();
    }
    
    public static String  TransferSupportedService2UUID(SupportedService service)
    {
        if(service.equals(SupportedService.nevo))
            return GattAttributes.NEVO_SERVICE;
    	return null;
    }
    
    public static List<UUID> supportedBLEServiceByEnum(List<UUID> uuids,List<SupportedService> supportServicelist) {
    	List<UUID> chosenServices = new ArrayList<UUID>();
    	
    	for(UUID uuid : uuids){
    		Optional<SupportedService> service = TransferUUID2SupportedService(uuid.toString());
    		
    		//If this service is unknown, no reason to pursue
    		if(service.isEmpty()) continue;
    		
    		//If all services are supported, then we add each and every services we find.
    		//If the service we are investigating is in the supported services list, we add it too
    		if(supportServicelist.contains(SupportedService.allService)
    				|| supportServicelist.contains(service.get())) {

    			chosenServices.add(uuid);
    			continue;
    		}
    		
    	}
		return chosenServices;
	}
}
