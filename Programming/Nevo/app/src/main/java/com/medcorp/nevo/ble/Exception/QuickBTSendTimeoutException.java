package com.medcorp.nevo.ble.exception;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.exception.visitor.NevoExceptionVisitor;

/**
 * Created by gaillysu on 15/5/11.
 * when QuickBT send request timeout,throw this class
 * perhaps connect timeout,or discovery timeout or parameter error, all will lead to send timeout
 */
public class QuickBTSendTimeoutException extends NevoException{

    @Override
    public <T> T accept(NevoExceptionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int getWarningMessageId() {
        return R.string.ble_connect_timeout;
    }
}
