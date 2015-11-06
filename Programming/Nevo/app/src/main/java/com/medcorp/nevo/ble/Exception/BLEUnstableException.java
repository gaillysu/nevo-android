package com.medcorp.nevo.ble.exception;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.exception.visitor.NevoExceptionVisitor;

/**
 * BLE is unstable, the user should restart the bluetooth layer and/or his phone.
 * @author Hugo
 *
 */
public class BLEUnstableException extends NevoException {

	private static final long serialVersionUID = 7967365228475902486L;

	@Override
	public <T> T accept(NevoExceptionVisitor<T> visitor) {
		return visitor.visit(this);
	}

    @Override
    public int getWarningMessageId() {
        return R.string.ble_unstable;
    }
}
