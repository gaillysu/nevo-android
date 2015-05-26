package com.nevowatch.nevo.ble.notification;

/**
 * Created by gaillysu on 15/4/10.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.nevowatch.nevo.Fragment.NotificationFragmentAdapter;
import com.nevowatch.nevo.PaletteActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.ble.controller.ConnectionController;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.kernel.QuickBTSendTimeoutException;
import com.nevowatch.nevo.ble.kernel.QuickBTUnBindNevoException;
import com.nevowatch.nevo.ble.model.request.LedLightOnOffNevoRequest;
import com.nevowatch.nevo.ble.util.Optional;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(18)
public class NevoNotificationListener extends NotificationListenerService implements NotificationCallback{

    static Optional<Date> lastNotification = new Optional<Date>();

    final static int TIME_BETWEEN_TWO_NOTIFS = 5000;

    final String TAG = NevoNotificationListener.class.getName();

    private TelephonyManager mTm;

    private CallStateListener mListener;

    // listen incoming call and then send led command to nevo watch
    class CallStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    if(NotificationFragmentAdapter.getTypeNFState(NevoNotificationListener.this,NotificationFragmentAdapter.TELETYPE))
                        sendNotification(PaletteActivity.getTypeChoosenColor(NevoNotificationListener.this,PaletteActivity.TELECHOOSENCOLOR));
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
    public void onNotificationPosted(StatusBarNotification arg0) {
        if(arg0 == null) return;

        Notification mNotification = arg0.getNotification();

        if (mNotification != null) {
            //sms
            if(arg0.getPackageName().equals("com.google.android.talk")
                    || arg0.getPackageName().equals("com.android.mms")
                    || arg0.getPackageName().equals("com.google.android.apps.messaging")
                    || arg0.getPackageName().equals("com.sonyericsson.conversations")
                    ) {
                Log.w(TAG, "Notification : " + arg0.getPackageName() + " : " + mNotification.number);
                //BLE keep-connect service will process this message
                if(NotificationFragmentAdapter.getTypeNFState(this,NotificationFragmentAdapter.SMSTYPE))
                    sendNotification(PaletteActivity.getTypeChoosenColor(this,PaletteActivity.SMSCHOOSENCOLOR));
            }

            //email,native email or gmail client
            else if(arg0.getPackageName().equals("com.android.email")
                    || arg0.getPackageName().equals("com.google.android.email")
                    || arg0.getPackageName().equals("com.google.android.gm")
                    || arg0.getPackageName().equals("com.kingsoft.email")
                    || arg0.getPackageName().equals("com.tencent.androidqqmail")
                    || arg0.getPackageName().equals("com.outlook.Z7")){
                Log.w(TAG, "Notification : " + arg0.getPackageName() + " : " + mNotification.number);
                //BLE keep-connect service will process this message
                if(NotificationFragmentAdapter.getTypeNFState(this,NotificationFragmentAdapter.EMAILTYPE))
                    sendNotification(PaletteActivity.getTypeChoosenColor(this,PaletteActivity.EMAILCHOOSENCOLOR));
            }
            //calendar
            else if(arg0.getPackageName().equals("com.google.android.calendar")
                    || arg0.getPackageName().equals("com.android.calendar")){
                Log.w(TAG, "Notification : " + arg0.getPackageName() + " : " + mNotification.number);
                //BLE keep-connect service will process this message
                if(NotificationFragmentAdapter.getTypeNFState(this,NotificationFragmentAdapter.CALTYPE))
                    sendNotification(PaletteActivity.getTypeChoosenColor(this,PaletteActivity.CALCHOOSENCOLOR));
            }
            //facebook
            else if(arg0.getPackageName().equals("com.facebook.katana")){
                Log.w(TAG, "Notification : " + arg0.getPackageName() + " : " + mNotification.number);
                //BLE keep-connect service will process this message
                if(NotificationFragmentAdapter.getTypeNFState(this,NotificationFragmentAdapter.FACETYPE))
                    sendNotification(PaletteActivity.getTypeChoosenColor(this,PaletteActivity.FACECHOOSENCOLOR));
            }
            //wechat
            else if(arg0.getPackageName().equals("com.tencent.mm")){
                Log.w(TAG, "Notification : " + arg0.getPackageName() + " : " + mNotification.number);
                //BLE keep-connect service will process this message
                if(NotificationFragmentAdapter.getTypeNFState(this,NotificationFragmentAdapter.WEICHATTYPE))
                    sendNotification(PaletteActivity.getTypeChoosenColor(this,PaletteActivity.WECHATCHOOSENCOLOR));
            }
            //whatsapp
            else if(arg0.getPackageName().equals("com.whatsapp")){
                Log.w(TAG, "Notification : " + arg0.getPackageName() + " : " + mNotification.number);
                //BLE keep-connect service will process this message
                if(NotificationFragmentAdapter.getTypeNFState(this,NotificationFragmentAdapter.WHATSTYPE))
                    sendNotification(PaletteActivity.getTypeChoosenColor(this,PaletteActivity.WHATSAPPCHOOSENCOLOR));
            }

            else {
                Log.v(TAG, "Unknown Notification : "+arg0.getPackageName());
            }

        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification arg0) {
        //How do we remove incoming notifications from the watch ?
    }

    void sendNotification(final int ledcolor) {

        //We can't accept notifications if we just received one X ms ago
        if(lastNotification.notEmpty() && new Date().getTime()-lastNotification.get().getTime() < TIME_BETWEEN_TWO_NOTIFS) return ;

        lastNotification.set(new Date());

        ConnectionController.Singleton.getInstance(this).connect();

        ConnectionController.Singleton.getInstance(this)
                .sendRequest(new LedLightOnOffNevoRequest(ledcolor, true));

        Timer autoReconnectTimer = new Timer();
        autoReconnectTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ConnectionController.Singleton.getInstance(NevoNotificationListener.this)
                        .sendRequest(new LedLightOnOffNevoRequest(ledcolor, false));
            }
        }, 2000 );


    }

    public static void getNotificationAccessPermission(final Context ctx) {
        ContentResolver contentResolver = ctx.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = ctx.getPackageName();

        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
        {
            // Let's ask the user to enable notifications
            new AlertDialog.Builder(ctx).setTitle(R.string.notifAccess).setMessage(R.string.notifAccessMessage)
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
            //  doSomethingThatRequiresNotificationAccessPermission();


        }
    }

    @Override
    public void onErrorDetected(Exception e) {
        int titleID = R.string.ble_notification_title;
        int msgID = 0;
        if (e instanceof QuickBTUnBindNevoException)
            msgID = R.string.ble_notification_message;
        else if  (e instanceof QuickBTSendTimeoutException)
            msgID = R.string.ble_connecttimeout;
        else
        {
            //unknown exception, discard it
            return;
        }

        SyncController.Singleton.getInstance(null).showMessage(titleID,msgID);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //unregister PhoneStateListener
        mTm.listen(mListener, PhoneStateListener.LISTEN_NONE);
    }
}
