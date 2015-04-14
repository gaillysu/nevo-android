package com.nevowatch.nevo.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.nevowatch.nevo.R;

/**
 * Custom Circle Progress Bar in WelcomeFragment
 */
public class RoundProgressBar extends View {
    private Paint mPaint;
    private int mRoundColor;
    private int mRoundProgressColor;
    private int mTextColor;
    private float mTextSize;
    private float mRoundWidth;
    private int mMax;
    private int mProgress;
    private boolean mTextIsDisplayable;
    private int mStyle;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPaint = new Paint();


        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //get custom attributes
        mRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        mRoundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
        mTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
        mTextSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
        mRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        mMax = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        mTextIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        mStyle = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * draw outside circle
         */
        int centre = getWidth()/2;
        int radius = (int) (centre - mRoundWidth /2);
        mPaint.setColor(mRoundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(centre, centre, radius, mPaint);

        
        /**
         * draw mProgress
         */
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent = (int)(((float) mProgress / (float) mMax) * 100);
        float textWidth = mPaint.measureText(percent + "%");

        if(mTextIsDisplayable && percent != 0 && mStyle == STROKE){
            canvas.drawText(percent + "%", centre - textWidth / 2, centre + mTextSize /2, mPaint);
        }

        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setColor(mRoundProgressColor);
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);
        switch (mStyle) {
            case STROKE:{
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 0, 360 * mProgress / mMax, false, mPaint);
                break;
            }
            case FILL:{
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                if(mProgress !=0)
                    canvas.drawArc(oval, 0, 360 * mProgress / mMax, true, mPaint);
                break;
            }
        }

    }

    public synchronized void setMax(int max) {
        if(max < 0){
            throw new IllegalArgumentException("mMax not less than 0");
        }
        this.mMax = max;
    }

    public synchronized void setProgress(int progress) {
        if(progress < 0){
            throw new IllegalArgumentException("mProgress not less than 0");
        }
        if(progress > mMax){
            progress = mMax;
        }
        if(progress <= mMax){
            this.mProgress = progress;
            postInvalidate();
        }

    }

}
