package com.nevowatch.nevo.ble.notification;

/**
 * Created by gaillysu on 15/4/10.
 */
import java.util.Date;

import com.nevowatch.nevo.ble.kernel.QuickBT;
import com.nevowatch.nevo.ble.model.request.NevoRequest.NotificationType;
import com.nevowatch.nevo.ble.model.request.SendNotificationNevoRequest;
import com.nevowatch.nevo.ble.util.Constants;
import com.nevowatch.nevo.ble.util.Optional;

import android.annotation.TargetApi;
import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

@TargetApi(18)
public class NevoNotificationListener extends NotificationListenerService {

    static Optional<Date> lastNotification = new Optional<Date>();

    final static int TIME_BETWEEN_TWO_NOTIFS = 5000;

    final String TAG = NevoNotificationListener.class.getName();

    @Override
    public void onNotificationPosted(StatusBarNotification arg0) {
        Log.d(TAG,"Incoming notification");
        if(arg0 == null) return;

        Notification mNotification = arg0.getNotification();

        if (mNotification != null) {

            //android 4.4.x new feature,support extras
            // Bundle extras = mNotification.extras;
            Log.d(TAG,"Notification : "+arg0.getPackageName()+" : "+mNotification.number);
            //incoming call or missed call
            if(arg0.getPackageName().equals("com.google.android.dialer")
                    || arg0.getPackageName().equals("com.android.phone")
                    || arg0.getPackageName().equals("com.android.dialer")) {

                //BLE keep-connect service will process this message
                sendNotification(NotificationType.Call, 1);
            }

            //native mms or hangouts
            else if(arg0.getPackageName().equals("com.google.android.talk")
                    || arg0.getPackageName().equals("com.android.mms")
                    || arg0.getPackageName().equals("com.sonyericsson.conversations")
                    ) {

                //BLE keep-connect service will process this message
                sendNotification(NotificationType.SMS, mNotification.number);
            }

            //email,native email or gmail client
            else if(arg0.getPackageName().equals("com.android.email")
                    || arg0.getPackageName().equals("com.google.android.email")
                    || arg0.getPackageName().equals("com.google.android.gm")
                    || arg0.getPackageName().equals("com.outlook.Z7")){
                //BLE keep-connect service will process this message
                sendNotification(NotificationType.Email, mNotification.number);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification arg0) {
        //How do we remove incoming notifications from the watch ?
    }

    void sendNotification(NotificationType type, int num) {

        //We can't accept notifications if we just received one X ms ago
        if(lastNotification.notEmpty() && new Date().getTime()-lastNotification.get().getTime() < TIME_BETWEEN_TWO_NOTIFS) return ;

        lastNotification.set(new Date());

        QuickBT bt = QuickBT.Factory.newInstance(getSharedPreferences(Constants.PREF_NAME, 0).getString(Constants.SAVE_MAC_ADDRESS, ""), this);

        bt.send(new SendNotificationNevoRequest(type, num));
    }


}
