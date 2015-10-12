package com.medcorp.nevo;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Font Controller
 */

public class FontManager {

    public static void changeFonts(View[] array, Activity act) {

        Typeface tf = Typeface.createFromAsset(act.getAssets(),
                "font/Raleway-Light.ttf");
        for (int i = 0; i < array.length; i++) {
            if (array[i] instanceof TextView){
                TextView textView = (TextView)array[i];
                textView.setTypeface(tf);
            }else if (array[i] instanceof Button){
                Button button = (Button)array[i];
                button.setTypeface(tf);
            }else if (array[i] instanceof EditText){
                EditText edtext = (EditText)array[i];
                edtext.setTypeface(tf);
            }
        }
    }

    public static void changeBoldFonts(View[] array, Activity act) {

        Typeface tf = Typeface.createFromAsset(act.getAssets(),
                "font/Raleway-Bold.ttf");
        for (int i = 0; i < array.length; i++) {
            if (array[i] instanceof TextView){
                TextView textView = (TextView)array[i];
                textView.setTypeface(tf);
            }else if (array[i] instanceof Button){
                Button button = (Button)array[i];
                button.setTypeface(tf);
            }else if (array[i] instanceof EditText){
                EditText edtext = (EditText)array[i];
                edtext.setTypeface(tf);
            }
        }

    }
}
