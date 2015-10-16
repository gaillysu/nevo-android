/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.medcorp.nevo.ble.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Build;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.util.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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

    public static boolean supportedBLEService(Context context, String uuid) {
		if (uuid.equals(context.getString(R.string.NEVO_SERVICE))) {
            return true;
        }
		return false;
	}
    

    public static boolean supportedBLECharacteristic(Context context,String uuid){
        if (uuid.equals(context.getString(R.string.NEVO_CALLBACK_CHARACTERISTIC))
                || uuid.equals(context.getString(R.string.NEVO_OTA_CALLBACK_CHARACTERISTIC))
                || uuid.equals(context.getString(R.string.NEVO_OTA_CHARACTERISTIC))) {
            return true;
        }
		return false;
    }
    
    public static boolean shouldInitBLECharacteristic(String uuid){
		return false;
    }
    
    public static BluetoothGattCharacteristic initBLECharacteristic(String uuid, BluetoothGattCharacteristic characteristic){
		return characteristic;
    }

    public static boolean supportedBLEService(Context context, List<UUID> uuids) {
    	boolean supported = false;
    	for(UUID uuid : uuids){
    		if(supportedBLEService(context,uuid.toString())) {supported = true;break;}
    	}
		return supported;
	}
    
    public static Optional<SupportedService>  TransferUUID2SupportedService(Context context, String uuid)
    {
        if(uuid.equals(context.getString(R.string.NEVO_SERVICE))){
        return new Optional<SupportedService>(SupportedService.nevo);
    }
        if(uuid.equals(context.getString(R.string.NEVO_OTA_SERVICE))){
        return new Optional<SupportedService>(SupportedService.nevo_ota);
    }
       return new Optional<SupportedService>();
    }
    
    public static String  TransferSupportedService2UUID(Context context, SupportedService service)
    {
        if(service.equals(SupportedService.nevo))
            return context.getString(R.string.NEVO_SERVICE);
    	return null;
    }
    
    public static List<UUID> supportedBLEServiceByEnum(Context context, List<UUID> uuids,List<SupportedService> supportServicelist) {
    	List<UUID> chosenServices = new ArrayList<UUID>();
    	
    	for(UUID uuid : uuids){
    		Optional<SupportedService> service = TransferUUID2SupportedService(context, uuid.toString());
    		
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
