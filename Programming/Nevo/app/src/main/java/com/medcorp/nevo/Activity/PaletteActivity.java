package com.medcorp.nevo.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.ble.model.color.BlueLed;
import com.medcorp.nevo.ble.model.color.GreenLed;
import com.medcorp.nevo.ble.model.color.LightGreenLed;
import com.medcorp.nevo.ble.model.color.NevoLed;
import com.medcorp.nevo.ble.model.color.OrangeLed;
import com.medcorp.nevo.ble.model.color.RedLed;
import com.medcorp.nevo.ble.model.color.UnknownLed;
import com.medcorp.nevo.ble.model.color.YellowLed;
import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitable;
import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitor;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorGetter;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorSaver;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationVisitor;

/**
 * ColorPanelActivity
 */
public class PaletteActivity extends BaseActivity
        implements View.OnClickListener, ActivityObservable {

    private ImageView mBlue;
    private ImageView mGrassGreen;
    private ImageView mGreen;
    private ImageView mOrange;
    private ImageView mRed;
    private ImageView mYellow;
    private ImageView mBack;

    private NevoLed chosenLed = new UnknownLed();
    private int mPosition = -1;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.palette_activity);
        initView();
        initLayout(mPosition);
    }

    private void initView(){
        mBlue = (ImageView) findViewById(R.id.blueImage);
        mBlue.setOnClickListener(this);
        mGrassGreen = (ImageView) findViewById(R.id.grassGreenImage);
        mGrassGreen.setOnClickListener(this);
        mGreen = (ImageView) findViewById(R.id.greenImage);
        mGreen.setOnClickListener(this);
        mOrange = (ImageView) findViewById(R.id.orangeImage);
        mOrange.setOnClickListener(this);
        mRed = (ImageView) findViewById(R.id.redImage);
        mRed.setOnClickListener(this);
        mYellow = (ImageView) findViewById(R.id.yellowImage);
        mYellow.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.typetext);
        mPosition = getIntent().getIntExtra("Position", -1);
        mBack = (ImageView) findViewById(R.id.backimage);
        mBack.setOnClickListener(this);
    }

    private void initLayout(int position){
        NotificationColorGetter getter = new NotificationColorGetter(this);
        switch (position){
            case 0:
                chosenLed = new TelephoneNotification().accept(getter);
                setImageLight(chosenLed);
                mTitle.setText(getResources().getString(R.string.call_string));
                break;
            case 1:
                chosenLed = new EmailNotification().accept(getter);
                setImageLight(chosenLed);
                mTitle.setText(getResources().getString(R.string.email_string));
                break;
            case 2:
                chosenLed = new FacebookNotification().accept(getter);
                setImageLight(chosenLed);
                mTitle.setText(getResources().getString(R.string.facebook_string));
                break;
            case 3:
                chosenLed = new SmsNotification().accept(getter);
                setImageLight(chosenLed);
                mTitle.setText(getResources().getString(R.string.sms_string));
                break;
            case 4:
                chosenLed = new CalendarNotification().accept(getter);
                setImageLight(chosenLed);
                mTitle.setText(getResources().getString(R.string.calendar_string));
                break;
            case 5:
                chosenLed = new WeChatNotification().accept(getter);
                setImageLight(chosenLed);
                mTitle.setText(getResources().getString(R.string.wechat_string));
                break;
            case 6:
                chosenLed = new WhatsappNotification().accept(getter);
                setImageLight(chosenLed);
                mTitle.setText(getResources().getString(R.string.whatsapp_string));
                break;
            default:
                break;
        }
    }

    private void saveChoosenColor(final int position, NevoLed chosenLed){
        switch (position){
            case 0:
                saveTypeChoosenColor(this, new TelephoneNotification(), chosenLed);
                break;
            case 1:
                saveTypeChoosenColor(this, new EmailNotification(), chosenLed);
                break;
            case 2:
                saveTypeChoosenColor(this, new FacebookNotification(), chosenLed);
                break;
            case 3:
                saveTypeChoosenColor(this, new SmsNotification(), chosenLed);
                break;
            case 4:
                saveTypeChoosenColor(this, new CalendarNotification(), chosenLed);
                break;
            case 5:
                saveTypeChoosenColor(this, new WeChatNotification(), chosenLed);
                break;
            case 6:
                saveTypeChoosenColor(this, new WhatsappNotification(), chosenLed);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.blueImage:
                chosenLed = new BlueLed();
                setImageLight(chosenLed);
                saveChoosenColor(mPosition, chosenLed);
                break;
            case R.id.grassGreenImage:
                chosenLed = new LightGreenLed();
                setImageLight(chosenLed);
                saveChoosenColor(mPosition, chosenLed);
                break;
            case R.id.greenImage:
                chosenLed = new GreenLed();
                setImageLight(chosenLed);
                saveChoosenColor(mPosition, chosenLed);
                break;
            case R.id.orangeImage:
                chosenLed = new OrangeLed();
                setImageLight(chosenLed);
                saveChoosenColor(mPosition, chosenLed);
                break;
            case R.id.redImage:
                chosenLed = new RedLed();
                setImageLight(chosenLed);
                saveChoosenColor(mPosition, chosenLed);
                break;
            case R.id.yellowImage:
                chosenLed = new YellowLed();
                setImageLight(chosenLed);
                saveChoosenColor(mPosition, chosenLed);
                break;
            case R.id.backimage:
                finish();
                break;
            default:
                chosenLed = new UnknownLed();
                break;
        }
    }

    private void setImageLight(NevoLed led){
        NevoLedVisitor visitor = new ColorLedVisitor();
        mBlue.setSelected(false);
        mGrassGreen.setSelected(false);
        mGreen.setSelected(false);
        mOrange.setSelected(false);
        mRed.setSelected(false);
        mYellow.setSelected(false);
        led.accept(visitor);
    }

    public static void saveTypeChoosenColor(Context context, Notification applicationNotification, NevoLed value){
        NotificationVisitor saver = new NotificationColorSaver(context,value);
        applicationNotification.accept(saver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getModel().setActiveActivity(this);
    }

    @Override
    public void notifyDatasetChanged() {

    }

    @Override
    public void notifyOnConnected() {

    }

    @Override
    public void notifyOnDisconnected() {
            finish();
    }

    @Override
    public void batteryInfoReceived(Battery battery) {

    }

    @Override
    public void findWatchSuccess() {

    }

    private class ColorLedVisitor implements NevoLedVisitor<Void>{

        @Override
        public Void visit(BlueLed led) {
            mBlue.setSelected(true);
            return null;
        }

        @Override
        public Void visit(GreenLed led) {
            mGreen.setSelected(true);
            return null;
        }

        @Override
        public Void visit(LightGreenLed led) {
            mGrassGreen.setSelected(true);
            return null;
        }

        @Override
        public Void visit(OrangeLed led) {
            mOrange.setSelected(true);
            return null;
        }

        @Override
        public Void visit(RedLed led) {
            mRed.setSelected(true);
            return null;
        }

        @Override
        public Void visit(YellowLed led) {
            mYellow.setSelected(true);
            return null;
        }

        @Override
        public Void visit(NevoLedVisitable led) {
            return null;
        }

    }
}
