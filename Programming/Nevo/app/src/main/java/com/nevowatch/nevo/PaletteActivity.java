package com.nevowatch.nevo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.ble.model.request.SetNotificationNevoRequest;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;

/**
 * ColorPanelActivity
 */
public class PaletteActivity extends Activity
        implements View.OnClickListener, OnSyncControllerListener{

    private ImageView mBlue;
    private ImageView mGrassGreen;
    private ImageView mGreen;
    private ImageView mOrange;
    private ImageView mRed;
    private ImageView mYellow;
    private ImageView mBack;

    public static final int BLUE_LED = SetNotificationNevoRequest.SetNortificationRequestValues.BLUE_LED;
    public static final int LIGHTGREEN_LED = SetNotificationNevoRequest.SetNortificationRequestValues.LIGHTGREEN_LED;
    public static final int GREEN_LED = SetNotificationNevoRequest.SetNortificationRequestValues.GREEN_LED;
    public static final int ORANGE_LED = SetNotificationNevoRequest.SetNortificationRequestValues.ORANGE_LED;
    public static final int RED_LED = SetNotificationNevoRequest.SetNortificationRequestValues.RED_LED;
    public static final int YELLOW_LED = SetNotificationNevoRequest.SetNortificationRequestValues.YELLOW_LED;

    public static final String TELECHOOSENCOLOR = "telechoosencolor";
    public static final String EMAILCHOOSENCOLOR = "emailchoosencolor";
    public static final String FACECHOOSENCOLOR = "facechoosencolor";
    public static final String SMSCHOOSENCOLOR = "smschoosencolor";
    public static final String CALCHOOSENCOLOR = "calchoosencolor";
    public static final String WECHATCHOOSENCOLOR = "wechatchoosencolor";
    private int mChoosenColor = -1;
    private int mPosition = -1;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.palette_activity);

        initView();
        initLayout(mPosition);
        SyncController.Singleton.getInstance(this).startConnect(false, this);
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
        switch (position){
            case 0:
                mChoosenColor = getTypeChoosenColor(this, TELECHOOSENCOLOR);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.call_string));
                break;
            case 1:
                mChoosenColor = getTypeChoosenColor(this, EMAILCHOOSENCOLOR);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.email_string));
                break;
            case 2:
                mChoosenColor = getTypeChoosenColor(this, FACECHOOSENCOLOR);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.facebook_string));
                break;
            case 3:
                mChoosenColor = getTypeChoosenColor(this, SMSCHOOSENCOLOR);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.sms_string));
                break;
            case 4:
                mChoosenColor = getTypeChoosenColor(this, CALCHOOSENCOLOR);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.calendar_string));
                break;
            case 5:
                mChoosenColor = getTypeChoosenColor(this, WECHATCHOOSENCOLOR);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.wechat_string));
                break;
            default:
                break;
        }
    }

    private void saveChoosenColor(final int position, final int choosenColor){
        switch (position){
            case 0:
                saveTypeChoosenColor(this, TELECHOOSENCOLOR, choosenColor);
                break;
            case 1:
                saveTypeChoosenColor(this, EMAILCHOOSENCOLOR, choosenColor);
                break;
            case 2:
                saveTypeChoosenColor(this, FACECHOOSENCOLOR, choosenColor);
                break;
            case 3:
                saveTypeChoosenColor(this, SMSCHOOSENCOLOR, choosenColor);
                break;
            case 4:
                saveTypeChoosenColor(this, CALCHOOSENCOLOR, choosenColor);
                break;
            case 5:
                saveTypeChoosenColor(this, WECHATCHOOSENCOLOR, choosenColor);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.blueImage:
                mChoosenColor = PaletteActivity.BLUE_LED;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.grassGreenImage:
                mChoosenColor = PaletteActivity.LIGHTGREEN_LED;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.greenImage:
                mChoosenColor = PaletteActivity.GREEN_LED;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.orangeImage:
                mChoosenColor = PaletteActivity.ORANGE_LED;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.redImage:
                mChoosenColor = PaletteActivity.RED_LED;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.yellowImage:
                mChoosenColor = PaletteActivity.YELLOW_LED;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.backimage:
                finish();
                break;
            default:
                break;
        }
    }

    private void setImageLight(int type){
        switch (type){
            case PaletteActivity.BLUE_LED:
                mBlue.setSelected(true);
                mGrassGreen.setSelected(false);
                mGreen.setSelected(false);
                mOrange.setSelected(false);
                mRed.setSelected(false);
                mYellow.setSelected(false);
                break;
            case PaletteActivity.LIGHTGREEN_LED:
                mBlue.setSelected(false);
                mGrassGreen.setSelected(true);
                mGreen.setSelected(false);
                mOrange.setSelected(false);
                mRed.setSelected(false);
                mYellow.setSelected(false);
                break;
            case PaletteActivity.GREEN_LED:
                mBlue.setSelected(false);
                mGrassGreen.setSelected(false);
                mGreen.setSelected(true);
                mOrange.setSelected(false);
                mRed.setSelected(false);
                mYellow.setSelected(false);
                break;
            case PaletteActivity.ORANGE_LED:
                mBlue.setSelected(false);
                mGrassGreen.setSelected(false);
                mGreen.setSelected(false);
                mOrange.setSelected(true);
                mRed.setSelected(false);
                mYellow.setSelected(false);
                break;
            case PaletteActivity.RED_LED:
                mBlue.setSelected(false);
                mGrassGreen.setSelected(false);
                mGreen.setSelected(false);
                mOrange.setSelected(false);
                mRed.setSelected(true);
                mYellow.setSelected(false);
                break;
            case PaletteActivity.YELLOW_LED:
                mBlue.setSelected(false);
                mGrassGreen.setSelected(false);
                mGreen.setSelected(false);
                mOrange.setSelected(false);
                mRed.setSelected(false);
                mYellow.setSelected(true);
                break;
            default:
                break;
        }
    }

    public static void saveTypeChoosenColor(Context context, String tag, int value){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(tag.equals(TELECHOOSENCOLOR)){
            pref.edit().putInt(TELECHOOSENCOLOR, value).apply();
        }else if(tag.equals(EMAILCHOOSENCOLOR)){
            pref.edit().putInt(EMAILCHOOSENCOLOR, value).apply();
        }else if(tag.equals(FACECHOOSENCOLOR)){
            pref.edit().putInt(FACECHOOSENCOLOR, value).apply();
        }else if(tag.equals(SMSCHOOSENCOLOR)){
            pref.edit().putInt(SMSCHOOSENCOLOR, value).apply();
        }else if(tag.equals(CALCHOOSENCOLOR)){
            pref.edit().putInt(CALCHOOSENCOLOR, value).apply();
        }else if(tag.equals(WECHATCHOOSENCOLOR)){
            pref.edit().putInt(WECHATCHOOSENCOLOR, value).apply();
        }
    }

    public static int getTypeChoosenColor(Context context, String tag){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(tag.equals(TELECHOOSENCOLOR)){
            return pref.getInt(TELECHOOSENCOLOR, YELLOW_LED);
        }else if(tag.equals(EMAILCHOOSENCOLOR)){
            return pref.getInt(EMAILCHOOSENCOLOR, BLUE_LED);
        }else if(tag.equals(FACECHOOSENCOLOR)){
            return pref.getInt(FACECHOOSENCOLOR, LIGHTGREEN_LED);
        }else if(tag.equals(SMSCHOOSENCOLOR)){
            return pref.getInt(SMSCHOOSENCOLOR, RED_LED);
        }else if(tag.equals(CALCHOOSENCOLOR)){
            return pref.getInt(CALCHOOSENCOLOR, GREEN_LED);
        }else if(tag.equals(WECHATCHOOSENCOLOR)){
            return pref.getInt(WECHATCHOOSENCOLOR, ORANGE_LED);
        }
        return -1;
    }

    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        if(!isConnected){
            finish();
        }
    }
}
