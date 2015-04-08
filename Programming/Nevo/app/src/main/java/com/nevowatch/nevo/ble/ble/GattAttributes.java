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
    private static HashMap<String, String> attributes = new HashMap<String, String>();
    
    public static String DEVICEINFO_UDID 	= "0000180a-0000-1000-8000-00805f9b34fb";
    public static String COMBO_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static String COMBOSERVICE2_UDID = "00001816-0000-1000-8000-00805f9b34fb";
    
    // Power Part
    
    public static String CYCLEPOWER_UUID	="00001818-0000-1000-8000-00805f9b34fb";
    public static String CYCLEPOWER_MEASUREMENT 	= "00002a63-0000-1000-8000-00805f9b34fb";
    
    public static String POWERSERVICE_UDID 	="f0b94d50-6cac-4c99-9089-4b0a1df45002";
    public static String POWERCALIBRATION_MEASUREMENT 	="f0b94d51-6cac-4c99-9089-4b0a1df45002";
    
    
    // Heart Rate Part
    
    public static String HEART_RATE_SERVICE 			= "0000180d-0000-1000-8000-00805f9b34fb";
    													//2a37
    public static String HEART_RATE_CHARACTERISTIC 	= "00002a37-0000-1000-8000-00805f9b34fb";    
    public static String BODYSENSOR_CHARACTERISTIC 	= "00002a38-0000-1000-8000-00805f9b34fb";
  
    // Combo Senser Part
    
    public static String BSC_RATE_UDID				= "00002a5b-0000-1000-8000-00805f9b34fb";
    
    // Client Characteristic
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    
    //Running Speed and Cadence https://developer.bluetooth.org/gatt/services/Pages/ServiceViewer.aspx?u=org.bluetooth.service.running_speed_and_cadence.xml
    public static String RUNNING_SPEED_AND_CADENCE_SERVICE  			= "00001814-0000-1000-8000-00805f9b34fb";
    public static String RUNNING_SPEED_AND_CADENCE_CHARACTERISTIC 			= "00002a53-0000-1000-8000-00805f9b34fb";
    
    //Running control service:
    public static String RUNNING_CONTROL_SERVICE  			= "f0ba3001-6cac-4c99-9089-4b0a1df45002";
    
    //TODO 101 102
    public static String RUNNING_SPEED_AND_CADENCE_CONTROL_CHARACTERISTIC 			= "f0ba5101-6cac-4c99-9089-4b0a1df45002";
    public static String RUNNING_SPEED_AND_CADENCE_STATUS_CHARACTERISTIC 			= "f0ba5102-6cac-4c99-9089-4b0a1df45002";
    
    //Watch Z communicaiton service
    public static String WATCHZ_SERVICE = "f0ba3011-6cac-4c99-9089-4b0a1df45002";
    
    //This service will return us all the data we ask for through notification
    public static String WATCHZ_CALLBACK_CHARACTERISTIC = "f0ba5111-6cac-4c99-9089-4b0a1df45002";
    public static String WATCHZ_INPUT_CHARACTERISTIC = "f0ba5112-6cac-4c99-9089-4b0a1df45002";
    public static String WATCHZ_NOTIFICATION_CHARACTERISTIC = "f0ba5113-6cac-4c99-9089-4b0a1df45002";


    //Nevo communicaiton service
    public static String NEVO_SERVICE = "f0ba3020-6cac-4c99-9089-4b0a1df45002";

    //This service will return us all the data we ask for through notification
    public static String NEVO_CALLBACK_CHARACTERISTIC = "f0ba3021-6cac-4c99-9089-4b0a1df45002";
    public static String NEVO_INPUT_CHARACTERISTIC = "f0ba3022-6cac-4c99-9089-4b0a1df45002";
    public static String NEVO_OTA_CHARACTERISTIC = "f0ba3023-6cac-4c99-9089-4b0a1df45002";

    static 
    {
        // Sample Services.
    	
    	attributes.put(DEVICEINFO_UDID, "Device Information");
    	attributes.put(COMBO_SERVICE, "Combo1 Information");
    	attributes.put(COMBOSERVICE2_UDID, "Combo2 Information");
    	
    	attributes.put(CYCLEPOWER_UUID, "Cycle Power Service");
    	attributes.put(CYCLEPOWER_MEASUREMENT, "Cycle Power Measurement");
    	
    	attributes.put(POWERSERVICE_UDID, "Power Service");
    	attributes.put(POWERCALIBRATION_MEASUREMENT, "Power Calibration");
    	
    	attributes.put(HEART_RATE_SERVICE, "Heart Rate Service");
        attributes.put(HEART_RATE_CHARACTERISTIC, "Heart Rate Measurement");        
        attributes.put(BODYSENSOR_CHARACTERISTIC, "Body Sensor Characteristic");
        
        attributes.put(WATCHZ_SERVICE, "Watch Z Service");
    }

    public static String lookup(String uuid, String defaultName) 
    {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
    
    
    // here add COMBOSERVICE2_UDID for BSC BLE service, gaillysu
    //TODO check with iOS version if the same
    public static boolean supportedBLEService(String uuid) {
		if (uuid.equals(GattAttributes.HEART_RATE_SERVICE)
				|| uuid.equals(GattAttributes.COMBO_SERVICE)
				|| uuid.equals(GattAttributes.COMBOSERVICE2_UDID)
				|| uuid.equals(GattAttributes.BSC_RATE_UDID)
				|| uuid.equals(GattAttributes.CYCLEPOWER_UUID)
				|| uuid.equals(GattAttributes.RUNNING_SPEED_AND_CADENCE_SERVICE)
				|| uuid.equals(GattAttributes.WATCHZ_SERVICE)
                || uuid.equals(GattAttributes.NEVO_SERVICE))
			return true;
		return false;
	}
    
    //TODO check with iOS version if the same
    public static boolean supportedBLECharacteristic(String uuid){
		if (uuid.equals(GattAttributes.HEART_RATE_CHARACTERISTIC)
				|| uuid.equals(GattAttributes.CYCLEPOWER_MEASUREMENT)
				|| uuid.equals(GattAttributes.POWERCALIBRATION_MEASUREMENT)
				|| uuid.equals(GattAttributes.BSC_RATE_UDID)
				|| uuid.equals(GattAttributes.RUNNING_SPEED_AND_CADENCE_CHARACTERISTIC)
				|| uuid.equals(GattAttributes.RUNNING_SPEED_AND_CADENCE_STATUS_CHARACTERISTIC)
				|| uuid.equals(GattAttributes.WATCHZ_CALLBACK_CHARACTERISTIC)
                || uuid.equals(GattAttributes.NEVO_CALLBACK_CHARACTERISTIC)
				)
			return true;
		return false;
    }
    
    public static boolean shouldInitBLECharacteristic(String uuid){
		if (uuid.equals(GattAttributes.RUNNING_SPEED_AND_CADENCE_CONTROL_CHARACTERISTIC))
			return true;
		return false;
    }
    
    public static BluetoothGattCharacteristic initBLECharacteristic(String uuid, BluetoothGattCharacteristic characteristic){
		if (uuid.equals(GattAttributes.RUNNING_SPEED_AND_CADENCE_CONTROL_CHARACTERISTIC)){
			characteristic.setValue(new byte[]{1});
		}
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
       if(uuid.equals(GattAttributes.HEART_RATE_SERVICE))
    	   return new Optional<SupportedService>(SupportedService.heartRate);
       if(uuid.equals(GattAttributes.CYCLEPOWER_UUID))
    	   return new Optional<SupportedService>(SupportedService.power);
       if(uuid.equals(GattAttributes.COMBOSERVICE2_UDID))
    	   return new Optional<SupportedService>(SupportedService.bikeCombo);
       if(uuid.equals(GattAttributes.WATCHZ_SERVICE))
    	   return new Optional<SupportedService>(SupportedService.watch);
        if(uuid.equals(GattAttributes.NEVO_SERVICE))
            return new Optional<SupportedService>(SupportedService.nevo);
       return new Optional<SupportedService>();
    }
    
    public static String  TransferSupportedService2UUID(SupportedService service)
    {
    	if(service.equals(SupportedService.heartRate))
    		return GattAttributes.HEART_RATE_SERVICE;
    	if(service.equals(SupportedService.power))
    		return GattAttributes.CYCLEPOWER_UUID;
    	if(service.equals(SupportedService.bikeCombo))
    		return GattAttributes.COMBOSERVICE2_UDID;	
    	if(service.equals(SupportedService.watch))
    		return GattAttributes.WATCHZ_SERVICE;
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
    			
    			//Only one exception to the general rule.
    			//With the WatchZ, We shouldn't connect to a "allService" request.
    			if(supportServicelist.contains(SupportedService.allService) && service.get() == SupportedService.watch) continue;
    			
    			chosenServices.add(uuid);
    			continue;
    		}
    		
    	}
		return chosenServices;
	}
}
