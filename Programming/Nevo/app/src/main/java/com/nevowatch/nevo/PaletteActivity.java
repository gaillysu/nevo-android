package com.nevowatch.nevo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ColorPanelActivity
 */
public class PaletteActivity extends Activity implements View.OnClickListener{

    private ImageView mBlue;
    private ImageView mGrassGreen;
    private ImageView mGreen;
    private ImageView mOrange;
    private ImageView mRed;
    private ImageView mYellow;
    public static final int BLUE = 1;
    public static final int GRASSGREEN = 2;
    public static final int GREEN = 3;
    public static final int ORANGE = 4;
    public static final int RED = 5;
    public static final int YELLOW = 6;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.palette_activity);

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
        initLayout(mPosition);
    }

    private void initLayout(int position){
        switch (position){
            case 0:
                mChoosenColor = getTeleChoosenColorFromPreference(this);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.call_string));
                break;
            case 1:
                mChoosenColor = getEmailChoosenColorFromPreference(this);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.email_string));
                break;
            case 2:
                mChoosenColor = getFaceChoosenColorFromPreference(this);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.facebook_string));
                break;
            case 3:
                mChoosenColor = getSmsChoosenColorFromPreference(this);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.sms_string));
                break;
            case 4:
                mChoosenColor = getCalChoosenColorFromPreference(this);
                setImageLight(mChoosenColor);
                mTitle.setText(getResources().getString(R.string.calendar_string));
                break;
            case 5:
                mChoosenColor = getWechatChoosenColorFromPreference(this);
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
                saveTeleChoosenColorToPreference(this, choosenColor);
                break;
            case 1:
                saveEmailChoosenColorToPreference(this, choosenColor);
                break;
            case 2:
                saveFaceChoosenColorToPreference(this, choosenColor);
                break;
            case 3:
                saveSmsChoosenColorToPreference(this, choosenColor);
                break;
            case 4:
                saveCalChoosenColorToPreference(this, choosenColor);
                break;
            case 5:
                saveWechatChoosenColorToPreference(this, choosenColor);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.blueImage:
                mChoosenColor = PaletteActivity.BLUE;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.grassGreenImage:
                mChoosenColor = PaletteActivity.GRASSGREEN;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.greenImage:
                mChoosenColor = PaletteActivity.GREEN;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.orangeImage:
                mChoosenColor = PaletteActivity.ORANGE;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.redImage:
                mChoosenColor = PaletteActivity.RED;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            case R.id.yellowImage:
                mChoosenColor = PaletteActivity.YELLOW;
                setImageLight(mChoosenColor);
                saveChoosenColor(mPosition, mChoosenColor);
                break;
            default:
                break;
        }
    }

    private void setImageLight(int type){
        switch (type){
            case PaletteActivity.BLUE:
                mBlue.setSelected(true);
                mGrassGreen.setSelected(false);
                mGreen.setSelected(false);
                mOrange.setSelected(false);
                mRed.setSelected(false);
                mYellow.setSelected(false);
                break;
            case PaletteActivity.GRASSGREEN:
                mBlue.setSelected(false);
                mGrassGreen.setSelected(true);
                mGreen.setSelected(false);
                mOrange.setSelected(false);
                mRed.setSelected(false);
                mYellow.setSelected(false);
                break;
            case PaletteActivity.GREEN:
                mBlue.setSelected(false);
                mGrassGreen.setSelected(false);
                mGreen.setSelected(true);
                mOrange.setSelected(false);
                mRed.setSelected(false);
                mYellow.setSelected(false);
                break;
            case PaletteActivity.ORANGE:
                mBlue.setSelected(false);
                mGrassGreen.setSelected(false);
                mGreen.setSelected(false);
                mOrange.setSelected(true);
                mRed.setSelected(false);
                mYellow.setSelected(false);
                break;
            case PaletteActivity.RED:
                mBlue.setSelected(false);
                mGrassGreen.setSelected(false);
                mGreen.setSelected(false);
                mOrange.setSelected(false);
                mRed.setSelected(true);
                mYellow.setSelected(false);
                break;
            case PaletteActivity.YELLOW:
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

    public static void saveTeleChoosenColorToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(TELECHOOSENCOLOR, value).apply();
    }

    public static int getTeleChoosenColorFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(TELECHOOSENCOLOR, -1);
    }

    public static void saveEmailChoosenColorToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(EMAILCHOOSENCOLOR, value).apply();
    }

    public static int getEmailChoosenColorFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(EMAILCHOOSENCOLOR, -1);
    }

    public static void saveFaceChoosenColorToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(FACECHOOSENCOLOR, value).apply();
    }

    public static int getFaceChoosenColorFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(FACECHOOSENCOLOR, -1);
    }

    public static void saveSmsChoosenColorToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(SMSCHOOSENCOLOR, value).apply();
    }

    public static int getSmsChoosenColorFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(SMSCHOOSENCOLOR, -1);
    }
    public static void saveCalChoosenColorToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(CALCHOOSENCOLOR, value).apply();
    }

    public static int getCalChoosenColorFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(CALCHOOSENCOLOR, -1);
    }
    public static void saveWechatChoosenColorToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(WECHATCHOOSENCOLOR, value).apply();
    }

    public static int getWechatChoosenColorFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(WECHATCHOOSENCOLOR, -1);
    }
}
