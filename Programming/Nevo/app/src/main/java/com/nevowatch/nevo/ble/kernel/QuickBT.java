package com.nevowatch.nevo.ble.kernel;

import com.nevowatch.nevo.ble.model.request.SensorRequest;

import android.content.Context;


public interface QuickBT {

	static String TAG = "iMaze Quick BT SDK";
	
	public class Factory{

		public static QuickBT newInstance(String address, Context context) {
			
			return new QuickBTImpl(address, context);
			
		}
		
	}
	
	/**
	 * Sends the given request to this device.
	 * Warning though, two consecutive requests can have impredictable behaviors
	 * @param request
	 */
	public void send(SensorRequest request);

}