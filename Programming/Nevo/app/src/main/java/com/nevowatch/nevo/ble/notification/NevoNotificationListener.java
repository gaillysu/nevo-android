package com.nevowatch.nevo.ble.notification;

/**
 * Created by gaillysu on 15/4/10.
 * /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 */
import java.util.Date;


import com.nevowatch.nevo.Fragment.NotificationFragmentAdapter;
import com.nevowatch.nevo.PaletteActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.kernel.QuickBT;
import com.nevowatch.nevo.ble.model.request.LedLightOnOffNevoRequest;
import com.nevowatch.nevo.Model.Notification.NotificationType;
import com.nevowatch.nevo.ble.model.request.SendNotificationNevoRequest;
import com.nevowatch.nevo.ble.model.request.SetNotificationNevoRequest;
import com.nevowatch.nevo.ble.util.Optional;
import com.nevowatch.nevo.ble.util.Constants;
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
import android.util.Log;

@TargetApi(18)
public class NevoNotificationListener extends NotificationListenerService implements NotificationCallback{

    static Optional<Date> lastNotification = new Optional<Date>();

    final static int TIME_BETWEEN_TWO_NOTIFS = 5000;

    final String TAG = NevoNotificationListener.class.getName();

    @Override
    public void onNotificationPosted(StatusBarNotification arg0) {
        if(arg0 == null) return;

        Notification mNotification = arg0.getNotification();

        if (mNotification != null) {

            //android 4.4.x new feature,support extras
            // Bundle exras = mNotification.extras;
            //incoming call or missed call
            if(arg0.getPackageName().equals("com.google.android.dialer")
                    || arg0.getPackageName().equals("com.android.incallui")
                    || arg0.getPackageName().equals("com.android.phone")
                    || arg0.getPackageName().equals("com.android.dialer")) {
                Log.w(TAG, "Notification : " + arg0.getPackageName() + " : " + mNotification.number);
                //BLE keep-connect service will process this message
                if(NotificationFragmentAdapter.getTypeNFState(this,NotificationFragmentAdapter.TELETYPE))
                sendNotification(PaletteActivity.getTypeChoosenColor(this,PaletteActivity.TELECHOOSENCOLOR));
            }

            //native mms or hangouts
            else if(arg0.getPackageName().equals("com.google.android.talk")
                    || arg0.getPackageName().equals("com.android.mms")
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

        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification arg0) {
        //How do we remove incoming notifications from the watch ?
    }

    void sendNotificationApi(NotificationType type, int num) {

        //We can't accept notifications if we just received one X ms ago
        if(lastNotification.notEmpty() && new Date().getTime()-lastNotification.get().getTime() < TIME_BETWEEN_TWO_NOTIFS) return ;

        lastNotification.set(new Date());

        QuickBT bt = QuickBT.Factory.newInstance(getSharedPreferences(Constants.PREF_NAME, 0).getString(Constants.SAVE_MAC_ADDRESS, ""), this);
        bt.send(new SendNotificationNevoRequest(type, num));
    }
    void sendNotification(int ledcolor) {

        //We can't accept notifications if we just received one X ms ago
        if(lastNotification.notEmpty() && new Date().getTime()-lastNotification.get().getTime() < TIME_BETWEEN_TWO_NOTIFS) return ;

        lastNotification.set(new Date());

        QuickBT bt = QuickBT.Factory.newInstance(getSharedPreferences(Constants.PREF_NAME, 0).getString(Constants.SAVE_MAC_ADDRESS, ""), this);

        bt.send(new LedLightOnOffNevoRequest(ledcolor,true));

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
    public void process(int titleID, int msgID) {
        SyncController.Singleton.getInstance(null).showMessage(titleID,msgID);
    }
}
