/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.medcorp.nevo.ble.listener;

import com.medcorp.nevo.ble.exception.NevoException;

/**
 * This Listener will be called when an exception have been raised.
 * The user should be informed properly and react accordingly.
 * @author Hugo
 *
 */
	
public interface OnExceptionListener {
		/**
		 * This function is called everytime an important exception is raised.
		 * Up to you to inform the user and/or launch a re-scan
		 */
		public void onException(NevoException e);


}
