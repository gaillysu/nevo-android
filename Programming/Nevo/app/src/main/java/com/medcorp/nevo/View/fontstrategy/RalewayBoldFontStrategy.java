package com.medcorp.nevo.view.fontstrategy;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Karl on 9/29/15.
 */
public class RalewayBoldFontStrategy implements FontStrategy {

    private Typeface tf;
    public RalewayBoldFontStrategy(Context context) {
        tf = Typeface.createFromAsset(context.getAssets(),
                //TODO put into config.xml
                "font/Raleway-Bold.ttf");
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
