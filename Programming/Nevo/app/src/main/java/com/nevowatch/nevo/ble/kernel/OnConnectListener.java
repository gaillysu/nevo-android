/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.nevowatch.nevo.ble.kernel;


/**
 * This Listener will be called when a given peripheral is connected.
 * This call isn't reliable and can't be trusted fully
 * @author Hugo
 *
 */
public interface OnConnectListener {
	
	/**
	 * This function is called everythime a device is connected.
	 * @param peripheral
	 */
	public void onConnect(String peripheralAdress);
}
