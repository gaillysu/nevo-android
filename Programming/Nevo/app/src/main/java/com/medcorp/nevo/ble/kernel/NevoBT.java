/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.medcorp.nevo.ble.kernel;

import android.content.Context;

import com.medcorp.nevo.ble.ble.GattAttributes.SupportedService;
import com.medcorp.nevo.ble.Exception.BLENotSupportedException;
import com.medcorp.nevo.ble.Exception.BluetoothDisabledException;
import com.medcorp.nevo.ble.listener.OnConnectListener;
import com.medcorp.nevo.ble.listener.OnDataReceivedListener;
import com.medcorp.nevo.ble.listener.OnExceptionListener;
import com.medcorp.nevo.ble.listener.OnFirmwareVersionListener;
import com.medcorp.nevo.ble.model.request.SensorRequest;
import com.medcorp.nevo.ble.util.Optional;

import java.util.List;



/**
 * The Interface NevoBT is the core manager for the bluetooth interface. (deepest layer)
 * In order to connect to a bluetooth device, we should instantiate it, then add a OnDataReceivedListener.
 * The OnDataReceivedListener will handle all the callbacks coming from the peripherals.
 * 
 * OnDataReceivedListener will receive data in the form of a SensorData, it will be able to get the type of sensor by calling data.getType()
 * Then, once the right sensor is found, it could return the data by calling data.getHeartrate() (if the sensor is a heart rate monitor)
 * 
 * To find nearby devices, use the startScan function.
 * 
 * WARNING !
 * Don't forget to add the following in the manifest :
     
       <uses-feature
        android:name="android.hardware.bluetooth_le"
        android:required="true" />

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    
    And in the <application> tag
             <service
            android:name="fr.imaze.sdk.ble.ImazeBTService"
            android:enabled="true" />
            
 * Also, the minimum target should be android 4.3 !
 * 
 * And don't forget to disconnect(null) before stopping the parent activity
 *
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
public interface NevoBT {
	
	/**
	 * THe logcat tag used by the SDK
	 */
	public static String TAG = "Nevo BT SDK";
	
	/**
	 * We don't want outside classes to link to the kernel directly, hence this Builder inner class
	 * @author Hugo
	 *
	 */
	/*
	 * rename calss "Builder" to "Factory" ,such as AOSP naming rule
	 * add "sInstance" member ,no need alloc memory every time.
	 */
	public class Singleton {
		private static NevoBTImpl sInstance = null;
		public static NevoBT getInstance(Context context) {
			if(null == sInstance )
			{
				sInstance = new NevoBTImpl(context);
			} else {
				sInstance.setContext(context);
			}
			return sInstance;
		}
	}

	/**
     * Start scanning for nearby devices supporting the given services, it should connect automatically to the first device encountered.
     * The scan will stop after 10 seconds
     * @param servicelist, the list of services we are looking for
     * @throws BLENotSupportedException
     * @throws BluetoothDisabledException
     */
    void startScan(List<SupportedService> servicelist, Optional<String> preferredAddress);

    /**
     * WARNING ! You should disconnect(Empty Optional) before stopping the parent activity
     */
    void disconnect();

    /**
	 * Send request. Sends a write request to all the devices that supports the right service and characteristic.
	 */
	void sendRequest(SensorRequest request);


	/**
	 * Delegate will be notified of connection change, exceptions and data received events
	 */
	void setDelegate(Delegate d);

    /**
     *
     * @return the nevo 's firmware version, it means the BLE firware version
     */
    String getFirmwareVersion();

    /**
     *
     * @return the nevo's software version, it means the MCU firmware version
     */
    String getSoftwareVersion();

    /**
     * Pings the currently connected device (if any) to check it is actually connected
     */
    void ping();

        interface Delegate extends OnExceptionListener, OnDataReceivedListener, OnConnectListener,OnFirmwareVersionListener {

    }
}
