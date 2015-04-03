/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.nevowatch.nevo.ble.kernel;


/**
 * This Listener will be called when a given peripheral is disconnected.
 * You can choose to start a new scan if you want to automatically reconnect.
 * @author Hugo
 *
 */
public interface OnDisconnectListener {
	
	/**
	 * This function is called everythime a device is disconnected.
	 * Up to you to launch a re-scan
	 * @param peripheral
	 */
	public void onDisconnect(String peripheralAdress);
}
