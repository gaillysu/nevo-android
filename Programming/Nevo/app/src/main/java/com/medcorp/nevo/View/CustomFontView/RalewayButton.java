package com.medcorp.nevo.View.customfontview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.medcorp.nevo.View.fontstrategy.FontStrategy;
import com.medcorp.nevo.View.fontstrategy.RalewayFontStrategy;

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
    }

    public RalewayButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        strategy = new RalewayFontStrategy(context);
        strategy.execute(this);
    }

}
