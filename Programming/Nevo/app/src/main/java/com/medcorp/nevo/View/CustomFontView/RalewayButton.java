package com.medcorp.nevo.view.customfontview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import com.medcorp.nevo.R;
import com.medcorp.nevo.view.fontstrategy.FontStrategy;
import com.medcorp.nevo.view.fontstrategy.RalewayBoldFontStrategy;
import com.medcorp.nevo.view.fontstrategy.RalewayFontStrategy;

/**
 * Created by Karl on 9/29/15.
 */
public class RalewayButton extends Button {

    private FontStrategy strategy;

    public RalewayButton(Context context) {
        super(context);
        strategy = new RalewayFontStrategy(context);
        strategy.execute(this);
    }

    public RalewayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        strategy = new RalewayFontStrategy(context);
        strategy.execute(this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontAttr);
        setBold(context, a.getBoolean(R.styleable.CustomFontAttr_bold, false));
    }

    public RalewayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontAttr, defStyleAttr, 0);
        setBold(context, a.getBoolean(R.styleable.CustomFontAttr_bold, false));
    }

    public void setBold(Context context, boolean bold){
        if (bold){
            strategy = new RalewayBoldFontStrategy(context);
        }else{
            strategy = new RalewayFontStrategy(context);
        }
        strategy.execute(this);
    }
}
