package com.medcorp.ble.notification;

/**
 * Created by gaillysu on 15/4/10.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

import android.app.AlertDialog;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.medcorp.ble.datasource.NotificationDataHelper;
import com.medcorp.ble.model.notification.CalendarNotification;
import com.medcorp.ble.model.notification.FacebookNotification;
import com.medcorp.ble.model.notification.SmsNotification;
import com.medcorp.ble.model.request.LedLightOnOffRequest;
import com.medcorp.util.Preferences;
import com.medcorp.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.ble.model.notification.EmailNotification;
import com.medcorp.ble.model.notification.WeChatNotification;
import com.medcorp.ble.model.notification.WhatsappNotification;
import com.medcorp.R;
import com.medcorp.ble.model.notification.TelephoneNotification;

import net.medcorp.library.ble.controller.ConnectionController;
import net.medcorp.library.ble.exception.BaseBLEException;
import net.medcorp.library.ble.notification.NotificationCallback;
import net.medcorp.library.ble.util.Optional;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NevoNotificationListener extends NotificationBaseListenerService implements NotificationCallback {

    static Optional<Date> lastNotification = new Optional<Date>();

    final static int TIME_BETWEEN_TWO_NOTIFS = 5000;

    final static int LIGHTTIMES = 3;

    final String TAG = NevoNotificationListener.class.getName();

    private TelephonyManager mTm;

    private CallStateListener mListener;


    // listen incoming call and then send led command to nevo watch
    class CallStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    NotificationDataHelper helper = new NotificationDataHelper(NevoNotificationListener.this);
                    if(helper.getState(new TelephoneNotification()).isOn()) {
                        sendNotification(Preferences.getNotificationColor(NevoNotificationListener.this, new TelephoneNotification()).getHexColor());
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //init PhoneStateListener
        mTm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mListener = new CallStateListener();
        mTm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        if(statusBarNotification == null) {
            return;
        }

        Notification notification = statusBarNotification.getNotification();
        NotificationDataHelper helper = new NotificationDataHelper(this);
        if (notification != null) {
            if(statusBarNotification.getPackageName().equals("com.google.android.talk")
                    || statusBarNotification.getPackageName().equals("com.android.mms")
                    || statusBarNotification.getPackageName().equals("com.google.android.apps.messaging")
                    || statusBarNotification.getPackageName().equals("com.sonyericsson.conversations")
                    || statusBarNotification.getPackageName().equals("com.htc.sense.mms")
                    || statusBarNotification.getPackageName().equals("com.google.android.talk")
                    ) {
                //BLE keep-connect service will process this message
                if(helper.getState(new SmsNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new SmsNotification()).getHexColor());
            } else if(statusBarNotification.getPackageName().equals("com.android.email")
                    || statusBarNotification.getPackageName().equals("com.google.android.email")
                    || statusBarNotification.getPackageName().equals("com.google.android.gm")
                    || statusBarNotification.getPackageName().equals("com.kingsoft.email")
                    || statusBarNotification.getPackageName().equals("com.tencent.androidqqmail")
                    || statusBarNotification.getPackageName().equals("com.outlook.Z7")){
                if(helper.getState(new EmailNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new EmailNotification()).getHexColor());
            } else if(statusBarNotification.getPackageName().equals("com.google.android.calendar")
                    || statusBarNotification.getPackageName().equals("com.android.calendar")){
                if(helper.getState(new CalendarNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new CalendarNotification()).getHexColor());
            } else if(statusBarNotification.getPackageName().equals("com.facebook.katana")){
                if(helper.getState(new FacebookNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new FacebookNotification()).getHexColor());
            } else if(statusBarNotification.getPackageName().equals("com.tencent.mm")){
                if(helper.getState(new WeChatNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new WeChatNotification()).getHexColor());
            } else if(statusBarNotification.getPackageName().equals("com.whatsapp")){
                if(helper.getState(new WhatsappNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new WhatsappNotification()).getHexColor());
            } else {
                Log.v(TAG, "Unknown Notification : "+statusBarNotification.getPackageName());
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification arg0) {
    }

    /**
     * @param count :the flash times, total light on times,should double it, means light on/off follow:1.2s on,0.5s off,1.2s on,0.5s off,1.2s on, then off by Nevo self
     * @param ledColor: which led light on
     */
    private void showNotification(final int count,final int ledColor)
    {
        if(count == 0) return;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ConnectionController.Singleton.getInstance(NevoNotificationListener.this, new GattAttributesDataSourceImpl(NevoNotificationListener.this))
                        .sendRequest(new LedLightOnOffRequest(getApplicationContext(), count%2==0?ledColor:0, count%2==0));
                showNotification(count-1,ledColor);
            }
        },count%2==0?(count==LIGHTTIMES*2?0:500):1200); //first time should do right now, here have 0ms
    }
    void sendNotification(final int ledColor) {

        //We can't accept notifications if we just received one X ms ago
        if(lastNotification.notEmpty() && new Date().getTime()-lastNotification.get().getTime() < TIME_BETWEEN_TWO_NOTIFS) return ;

        lastNotification.set(new Date());

        //when OTA doing,discard the notification
        if(ConnectionController.Singleton.getInstance(this, new GattAttributesDataSourceImpl(this)).inOTAMode()){
            return;
        }

        ConnectionController.Singleton.getInstance(this, new GattAttributesDataSourceImpl(this)).connect();

        showNotification(LIGHTTIMES*2,ledColor);
    }

    public static void getNotificationAccessPermission(final Context ctx) {
        ContentResolver contentResolver = ctx.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = ctx.getPackageName();

        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
        {
            // Let's ask the user to enable notifications
            new AlertDialog.Builder(ctx).setTitle(R.string.notification_access_title).setMessage(R.string.notification_access_message)
                    .setNegativeButton(android.R.string.no, null).setPositiveButton(android.R.string.yes, new AlertDialog.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    ctx.startActivity(intent);

                }

            }).show();
        }
    }

    @Override
    public void onErrorDetected(BaseBLEException e) {
        Log.w("Karl","Couldn't give notification due to bluetooth problems.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTm.listen(mListener, PhoneStateListener.LISTEN_NONE);
    }
}
