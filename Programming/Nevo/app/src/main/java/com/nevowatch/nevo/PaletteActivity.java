package com.nevowatch.nevo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
    public static final String CHOOSENCOLOR = "choosencolor";
    private int choosenColor = -1;
    private int positon;

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

        positon = getIntent().getIntExtra("Position", -1);
        initLayout(positon);
        choosenColor = getChoosenColorFromPreference(this);
        setImageLight(choosenColor);
    }

    private void initLayout(int position){
        switch (position){
            case 0:
                Log.d("Position", "0");
                break;
            case 1:
                Log.d("Position", "1");
                break;
            case 2:
                Log.d("Position", "2");
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
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
                saveChoosenColorToPreference(this, choosenColor);
                break;
            case R.id.grassGreenImage:
                choosenColor = PaletteActivity.GRASSGREEN;
                setImageLight(choosenColor);
                saveChoosenColorToPreference(this, choosenColor);
                break;
            case R.id.greenImage:
                choosenColor = PaletteActivity.GREEN;
                setImageLight(choosenColor);
                saveChoosenColorToPreference(this, choosenColor);
                break;
            case R.id.orangeImage:
                choosenColor = PaletteActivity.ORANGE;
                setImageLight(choosenColor);
                saveChoosenColorToPreference(this, choosenColor);
                break;
            case R.id.redImage:
                choosenColor = PaletteActivity.RED;
                setImageLight(choosenColor);
                saveChoosenColorToPreference(this, choosenColor);
                break;
            case R.id.yellowImage:
                choosenColor = PaletteActivity.YELLOW;
                setImageLight(choosenColor);
                saveChoosenColorToPreference(this, choosenColor);
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

    public static void saveChoosenColorToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(CHOOSENCOLOR, value).apply();
    }

    public static int getChoosenColorFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(CHOOSENCOLOR, -1);
    }
}
