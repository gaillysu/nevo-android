package com.nevowatch.nevo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

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
    private int choosenColor = -1;

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

        if(getIntent() != null)
            initLayout(getIntent().getIntExtra("Position", -1));

        if(savedInstanceState != null){
            choosenColor = savedInstanceState.getInt("Position");
            initLayout(choosenColor);
        }
    }

    private void initLayout(int position){
        switch (position){
            case 0:
                choosenColor = getTeleChoosenColorFromPreference(this);
                setImageLight(choosenColor);
                break;
            case 1:
                choosenColor = getEmailChoosenColorFromPreference(this);
                setImageLight(choosenColor);
                break;
            case 2:
                choosenColor = getFaceChoosenColorFromPreference(this);
                setImageLight(choosenColor);
                break;
            case 3:
                choosenColor = getSmsChoosenColorFromPreference(this);
                setImageLight(choosenColor);
                break;
            case 4:
                choosenColor = getCalChoosenColorFromPreference(this);
                setImageLight(choosenColor);
                break;
            case 5:
                choosenColor = getWechatChoosenColorFromPreference(this);
                setImageLight(choosenColor);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.blueImage:
                choosenColor = PaletteActivity.BLUE;
                setImageLight(choosenColor);
                saveTeleChoosenColorToPreference(this, -1);
                break;
            case R.id.grassGreenImage:
                choosenColor = PaletteActivity.GRASSGREEN;
                setImageLight(choosenColor);
                saveEmailChoosenColorToPreference(this, -1);
                break;
            case R.id.greenImage:
                choosenColor = PaletteActivity.GREEN;
                setImageLight(choosenColor);
                saveFaceChoosenColorToPreference(this, -1);
                break;
            case R.id.orangeImage:
                choosenColor = PaletteActivity.ORANGE;
                setImageLight(choosenColor);
                saveSmsChoosenColorToPreference(this, -1);
                break;
            case R.id.redImage:
                choosenColor = PaletteActivity.RED;
                setImageLight(choosenColor);
                saveCalChoosenColorToPreference(this, -1);
                break;
            case R.id.yellowImage:
                choosenColor = PaletteActivity.YELLOW;
                setImageLight(choosenColor);
                saveWechatChoosenColorToPreference(this, -1);
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("Position", choosenColor);
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
