package com.medcorp.nevo.view.customfontview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.view.fontstrategy.FontStrategy;
import com.medcorp.nevo.view.fontstrategy.RalewayBoldFontStrategy;
import com.medcorp.nevo.view.fontstrategy.RalewayFontStrategy;


/**
 * Created by Karl on 9/29/15.
 */
public class RalewayTextView extends TextView {
    private FontStrategy strategy;

    public RalewayTextView(Context context) {
        super(context);
        strategy = new RalewayFontStrategy(context);
        strategy.execute(this);
    }

    public RalewayTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomRaleWayAttr);
        changeToBold(context, a.getBoolean(R.styleable.CustomRaleWayAttr_bold, false));
    }

    public RalewayTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomRaleWayAttr, defStyleAttr, 0);
        changeToBold(context, a.getBoolean(R.styleable.CustomRaleWayAttr_bold, false));
    }

    public void changeToBold(Context context,boolean bold){
        if (bold){
            strategy = new RalewayBoldFontStrategy(context);
        }else{
            strategy = new RalewayFontStrategy(context);
        }
        strategy.execute(this);
    }
}
