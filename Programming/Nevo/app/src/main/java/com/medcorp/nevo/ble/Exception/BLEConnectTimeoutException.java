package com.medcorp.nevo.ble.exception;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.exception.visitor.NevoExceptionVisitor;

/**
 * Created by gaillysu on 15/4/24.
 */
public class BLEConnectTimeoutException extends NevoException {

    /**
     *
     */
    private static final long serialVersionUID = 978984361354590335L;

    @Override
    public <T> T accept(NevoExceptionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int getWarningMessageId() {
        return R.string.ble_connecttimeout;
    }

    public int getWarningMessageTitle(){
        return R.string.ble_connection_timeout_title;
    }
}
