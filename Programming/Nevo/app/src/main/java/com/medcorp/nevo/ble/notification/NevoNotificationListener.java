package com.medcorp.nevo.ble.notification;

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

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.datasource.GattAttributesDataSourceImpl;
import com.medcorp.nevo.ble.datasource.NotificationDataHelper;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;
import com.medcorp.nevo.ble.model.request.LedLightOnOffNevoRequest;
import com.medcorp.nevo.util.Preferences;

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
            //sms
            if(statusBarNotification.getPackageName().equals("com.google.android.talk")
                    || statusBarNotification.getPackageName().equals("com.android.mms")
                    || statusBarNotification.getPackageName().equals("com.google.android.apps.messaging")
                    || statusBarNotification.getPackageName().equals("com.sonyericsson.conversations")
                    ) {
                //BLE keep-connect service will process this message
                if(helper.getState(new SmsNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new SmsNotification()).getHexColor());
            }

            //email,native email or gmail client
            else if(statusBarNotification.getPackageName().equals("com.android.email")
                    || statusBarNotification.getPackageName().equals("com.google.android.email")
                    || statusBarNotification.getPackageName().equals("com.google.android.gm")
                    || statusBarNotification.getPackageName().equals("com.kingsoft.email")
                    || statusBarNotification.getPackageName().equals("com.tencent.androidqqmail")
                    || statusBarNotification.getPackageName().equals("com.outlook.Z7")){
                //BLE keep-connect service will process this message
                if(helper.getState(new EmailNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new EmailNotification()).getHexColor());
            }
            //calendar
            else if(statusBarNotification.getPackageName().equals("com.google.android.calendar")
                    || statusBarNotification.getPackageName().equals("com.android.calendar")){
                //BLE keep-connect service will process this message
                if(helper.getState(new CalendarNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new CalendarNotification()).getHexColor());
            }
            //facebook
            else if(statusBarNotification.getPackageName().equals("com.facebook.katana")){
                //BLE keep-connect service will process this message
                if(helper.getState(new FacebookNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new FacebookNotification()).getHexColor());
            }
            //wechat
            else if(statusBarNotification.getPackageName().equals("com.tencent.mm")){
                //BLE keep-connect service will process this message
                if(helper.getState(new WeChatNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new WeChatNotification()).getHexColor());
            }
            //whatsapp
            else if(statusBarNotification.getPackageName().equals("com.whatsapp")){
                //BLE keep-connect service will process this message
                if(helper.getState(new WhatsappNotification()).isOn())
                    sendNotification(Preferences.getNotificationColor(this, new WhatsappNotification()).getHexColor());
            }

            else {
                Log.v(TAG, "Unknown Notification : "+statusBarNotification.getPackageName());
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification arg0) {
        //How do we remove incoming notifications from the watch ?
    }

    /**
     *
     * @param count :the flash times, total light on times,should double it, means light on/off follow:1.2s on,0.5s off,1.2s on,0.5s off,1.2s on, then off by Nevo self
     * @param ledcolor: which led light on
     */
    private void showNotification(final int count,final int ledcolor)
    {
        if(count == 0) return;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ConnectionController.Singleton.getInstance(NevoNotificationListener.this, new GattAttributesDataSourceImpl(NevoNotificationListener.this))
                        .sendRequest(new LedLightOnOffNevoRequest(getApplicationContext(), count%2==0?ledcolor:0, count%2==0?true:false));
                showNotification(count-1,ledcolor);
            }
        },count%2==0?(count==LIGHTTIMES*2?0:500):1200); //first time should do right now, here have 0ms
    }
    void sendNotification(final int ledcolor) {

        //We can't accept notifications if we just received one X ms ago
        if(lastNotification.notEmpty() && new Date().getTime()-lastNotification.get().getTime() < TIME_BETWEEN_TWO_NOTIFS) return ;

        lastNotification.set(new Date());

        //when OTA doing,discard the notification
        if(ConnectionController.Singleton.getInstance(this, new GattAttributesDataSourceImpl(this)).inOTAMode()){
            return;
        }

        ConnectionController.Singleton.getInstance(this, new GattAttributesDataSourceImpl(this)).connect();

        showNotification(LIGHTTIMES*2,ledcolor);
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
        else
        {
//              doSomethingThatRequiresNotificationAccessPermission();
        }
    }

    @Override
    public void onErrorDetected(BaseBLEException e) {
        int titleID = R.string.ble_notification_title;
        // TODO replace this. This doesn't make sense.
//        int msgID = e.getWarningMessageId();
        //unknown exception, discard it
//        getModel().getSyncController().showMessage(titleID,msgID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregister PhoneStateListener
        mTm.listen(mListener, PhoneStateListener.LISTEN_NONE);
    }
}
