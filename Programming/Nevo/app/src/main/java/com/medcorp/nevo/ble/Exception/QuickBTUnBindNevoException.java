package com.medcorp.nevo.ble.exception;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.exception.visitor.NevoExceptionVisitor;

/**
 * Created by gaillysu on 15/5/11.
 * if send QuickBt command without bind a nevo, will throw this class
 */
public class QuickBTUnBindNevoException extends NevoException {

    @Override
    public <T> T accept(NevoExceptionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int getWarningMessageId() {
        return R.string.ble_notification_message;
    }


}
