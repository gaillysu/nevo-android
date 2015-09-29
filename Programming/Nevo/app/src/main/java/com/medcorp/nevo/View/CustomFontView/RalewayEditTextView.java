package com.medcorp.nevo.View.CustomFontView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.medcorp.nevo.View.FontStrategy.FontStrategy;
import com.medcorp.nevo.View.FontStrategy.RalewayFontStrategy;

/**
 * Created by Karl on 9/29/15.
 */
public class RalewayEditTextView extends EditText {

    private FontStrategy strategy;

    public RalewayEditTextView(Context context) {
        super(context);
        strategy = new RalewayFontStrategy(context);
        strategy.execute(this);
    }

    public RalewayEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        strategy = new RalewayFontStrategy(context);
        strategy.execute(this);
    }

    public RalewayEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        strategy = new RalewayFontStrategy(context);
        strategy.execute(this);
    }

}
