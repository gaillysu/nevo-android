package com.medcorp.nevo.view.customfontview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.medcorp.nevo.R;
import com.medcorp.nevo.view.fontstrategy.FontStrategy;
import com.medcorp.nevo.view.fontstrategy.RalewayFontStrategy;
import com.medcorp.nevo.view.fontstrategy.RobotoFontStrategy;
import com.medcorp.nevo.view.fontstrategy.RobotoLightFontStrategy;

/**
 * Created by Karl on 9/29/15.
 */
public class RobotoTextView extends EditText {

    private FontStrategy strategy;

    public RobotoTextView(Context context) {
        super(context);
        strategy = new RobotoFontStrategy(context);
        strategy.execute(this);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        strategy = new RalewayFontStrategy(context);
        strategy.execute(this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontAttr);
        setBold(context, a.getBoolean(R.styleable.CustomFontAttr_light, false));
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontAttr, defStyleAttr, 0);
        setBold(context, a.getBoolean(R.styleable.CustomFontAttr_light, false));
    }

    public void setBold(Context context, boolean bold){
        if (bold){
            strategy = new RobotoFontStrategy(context);
        }else{
            strategy = new RobotoLightFontStrategy(context);
        }
        strategy.execute(this);
    }
}
