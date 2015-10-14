package com.medcorp.nevo.ble.model.request;

/**
 * Created by gaillysu on 15/4/10.
 */
import com.medcorp.nevo.ble.ble.GattAttributes;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationVisitor;

import java.util.UUID;


public class SendNotificationNevoRequest extends NevoRequest {

    public  final static  byte HEADER = 0x60;

    private Notification notification;

    int mNumber;

    public SendNotificationNevoRequest(Notification notification, int num) {
        this.notification = notification;
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
        int mID = notification.accept(new LedIdVisitor());
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

    private class LedIdVisitor implements NotificationVisitor<Integer> {

        @Override
        public Integer visit(CalendarNotification calendarNotification) {
            return 7;
        }

        @Override
        public Integer visit(EmailNotification emailNotification) {
            return 1;
        }

        @Override
        public Integer visit(FacebookNotification facebookNotification) {
            return 10;
        }

        @Override
        public Integer visit(SmsNotification smsNotification) {
            return 5;
        }

        @Override
        public Integer visit(TelephoneNotification telephoneNotification) {
            return 3;
        }

        @Override
        public Integer visit(WeChatNotification weChatNotification) {
            return 11;
        }

        @Override
        public Integer visit(WhatsappNotification whatsappNotification) {
            return 11;
        }

        @Override
        public Integer visit(Notification applicationNotification) {
            return null;
        }
    }
}
