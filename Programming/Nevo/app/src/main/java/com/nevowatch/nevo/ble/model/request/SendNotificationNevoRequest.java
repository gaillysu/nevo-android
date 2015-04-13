package com.nevowatch.nevo.ble.model.request;

/**
 * Created by gaillysu on 15/4/10.
 */
import java.util.UUID;
import com.nevowatch.nevo.ble.ble.GattAttributes;


public class SendNotificationNevoRequest extends NevoRequest {

    NotificationType  mType;

    int mNumber;

    public SendNotificationNevoRequest(NotificationType type, int num) {
        mType = type;
        mNumber = num;

        if(mNumber == 0) mNumber = 1;
    }

    @Override
    public UUID getCharacteristicUUID() {
        return UUID.fromString(GattAttributes.NEVO_NOTIFICATION_CHARACTERISTIC);
    }

    @Override
    public byte[] getRawData() {
        if(mType == NotificationType.Call)
            return new byte[]{ (byte) 0x03, (byte) 0x01};
        if(mType == NotificationType.SMS)
            return new byte[]{ (byte) 0x09, (byte) mNumber};
        if(mType == NotificationType.Email)
            return new byte[]{ (byte) 0x09, (byte) mNumber};
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        return null;
    }

    @Override
    public byte getHeader() {
        return 0;
    }

}
