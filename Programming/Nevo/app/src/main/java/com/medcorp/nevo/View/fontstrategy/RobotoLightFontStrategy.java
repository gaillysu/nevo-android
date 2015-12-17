package com.medcorp.nevo.view.fontstrategy;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by karl-john on 14/12/15.
 */
public class RobotoLightFontStrategy implements FontStrategy {

    private Typeface tf;
    public RobotoLightFontStrategy(Context context) {
        tf = Typeface.createFromAsset(context.getAssets(),
                "font/Roboto-Light.ttf");
    }

    @Override
    public void execute(Button button) {
        button.setTypeface(tf);
    }

    @Override
    public void execute(EditText editText) {
        editText.setTypeface(tf);
    }

    @Override
    public void execute(TextView textView) {
        textView.setTypeface(tf);
    }
}