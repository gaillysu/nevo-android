/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.nevowatch.nevo.ble.kernel;

import android.content.Context;

import java.util.List;

import com.nevowatch.nevo.ble.ble.SupportedService;
import com.nevowatch.nevo.ble.model.request.SensorRequest;
import com.nevowatch.nevo.ble.util.Optional;



/**
 * The Interface ImazeBT is the core manager for the bluetooth interface.
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
 */
public interface ImazeBT {
	
	/**
	 * THe logcat tag used by the SDK
	 */
	public static String TAG = "iMaze BT SDK";
	
	/**
	 * We don't want outside classes to link to the kernel directly, hence this Builder inner class
	 * @author Hugo
	 *
	 */
	/*
	 * rename calss "Builder" to "Factory" ,such as AOSP naming rule
	 * add "sInstance" member ,no need alloc memory every time.
	 */
	public class Factory{
		private static ImazeBTImpl sInstance = null;
		public static ImazeBT newInstance(Context context) {
			if(null == sInstance )
			{
				sInstance = new ImazeBTImpl(context);
			} else {
				sInstance.setContext(context);
			}
			return sInstance;
		}
	}
	
	/**
	 * Adds a DataReiver callback. all the registered callbacks will be called every time the peripheral sends data.
	 *
	 * @param callback the callback
	 */
	void addCallback(OnDataReceivedListener callback);
	
	/**
	 * Removes a DataReiver callback. This callback won't be called anymore
	 *
	 * @param callback the callback
	 */
	void removeCallback(OnDataReceivedListener callback);
	
	/**
	 * Start scanning for nearby devices supporting the given services, it should connect automatically to the first device encountered.
	 * The scan will stop after 10 seconds
	 * @param servicelist, the list of services we are looking for
	 * @throws BLENotSupportedException
	 * @throws BluetoothDisabledException
	 */
	void startScan(List<SupportedService> servicelist) throws BLENotSupportedException, BluetoothDisabledException;
	
	/**
	 * Send request. Sends a write request to all the devices that supports the right service and characteristic.
	 * @param the request to be sent
	 */
	void sendRequest(SensorRequest request);
	
	/**
	 * Disconnect to the given peripheral
	 * If empty Optional, it will disconnect all peripherals
	 * WARNING ! You should disconnect(Empty Optional) before stopping the parent activity
	 * @param peripheral
	 */
	void disconnect(Optional<String> peripheralAdress);

	/**
	 * This callback will be called when a device is connected
	 * Note that this information isn't reliable and some call might not occur properly
	 * @param callback
	 */
	void connectCallback(OnConnectListener callback);
	
	/**
	 * This callback will be called when a device is disconnected
	 * Note that this information isn't reliable, in some cases it won't be called
	 * @param callback
	 */
	void disconnectCallback(OnDisconnectListener callback);
	
	/**
	 * This callback will be called when an exception is raised
	 * @param callback
	 */
	void exceptionCallback(OnExceptionListener callback);
	
	
	/**
	 * @return true if there's not a single device currently connected
	 */
	boolean isDisconnected();
	
	/**
	 * @param The service we are enquiering
	 * @return the MAC address of the device connected that supports this service. If the given service have no associated device currently, it returns an empty Optional
	 */
	Optional<String> getAddressByServiceType(SupportedService which);

    /**
    Tries to connect to a Nevo
    Myabe it will scan for nearby nevo, maybe it will simply connect to a known nevo
    */
	void connect(List<SupportedService> servicelist)throws BLENotSupportedException, BluetoothDisabledException;
	
    /**
    Checks if there is a preffered device.
    If the answer is yes, then we will systematically connect to this device.
    If it is no, then we will scan for a new device
    */
	boolean hasSavedAddress();
    /**
    Forgets the currently saved address.
    Next time connect is called, we will have to scan for nearby devices
    */
	void forgetSavedAddress();
    /**
    restore the saved address. BLE OTA use it
    Usage:forgetSavedAddress()/restoreSavedAddress(), if not call forgetSavedAddress()
    before call it, do nothing
    */
	void restoreSavedAddress();
	
	
}
