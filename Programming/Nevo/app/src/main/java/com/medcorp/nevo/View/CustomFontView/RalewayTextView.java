package com.medcorp.nevo.View.CustomFontView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.medcorp.nevo.View.FontStrategy.FontStrategy;
import com.medcorp.nevo.View.FontStrategy.RalewayFontStrategy;

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
        strategy = new RalewayFontStrategy(context);
        strategy.execute(this);
    }

    public RalewayTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        strategy = new RalewayFontStrategy(context);
        strategy.execute(this);
    }
}
