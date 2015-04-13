package com.nevowatch.nevo.ble.model.request;

/**
 * Created by gaillysu on 15/4/10.
 */
import java.util.UUID;
import com.nevowatch.nevo.ble.ble.GattAttributes;


public class SendNotificationNevoRequest extends NevoRequest {

    public  final static  byte HEADER = 0x60;

    NotificationType  mType;

    int mNumber;
    byte mID =0;

    public SendNotificationNevoRequest(NotificationType type, int num) {
        mType = type;
        mNumber = num;

        if(mNumber == 0) mNumber = 1;
    }

    @Override
    public UUID getInputCharacteristicUUID() {
        return UUID.fromString(GattAttributes.NEVO_NOTIFICATION_CHARACTERISTIC);
    }
    @Override
    public byte[] getRawData() {
        return null;
    }

    @Override
    public byte[][] getRawDataEx() {
        if(mType == NotificationType.Call)
            mID = 3;
        if(mType == NotificationType.SMS)
            mID = 5;
        if(mType == NotificationType.Email)
            mID = 1;
        if(mType == NotificationType.Calendar)
            mID = 7;
        if(mType == NotificationType.Facebook)
            mID = 10;
        if(mType == NotificationType.Wechat)
            mID = 11;

        return new byte[][] {
                    {0,HEADER,
                            (byte) (mID),
                            (byte) (mNumber),
                            0,0,0,0,
                            0,0,0,0,
                            0,0,0,0,
                            0,0,0,0
                    },
                    {(byte) 0xFF,HEADER,0,0,
                            0,0,0,0,
                            0,0,0,0,
                            0,0,0,0,
                            0,0,0,0
                    }
            };
    }

    @Override
    public byte getHeader() {
        return HEADER;
    }

}
