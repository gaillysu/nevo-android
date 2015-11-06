/*
 * COPYRIGHT (C) 2014 MED Enterprises LTD. All Rights Reserved.
 */
package com.medcorp.nevo.ble.exception;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.exception.visitor.NevoExceptionVisitor;

public class BluetoothDisabledException extends NevoException {

	private static final long serialVersionUID = 7450906170950978164L;

	@Override
	public <T> T accept(NevoExceptionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public int getWarningMessageId() {
		return R.string.ble_deactivated;
	}
}
