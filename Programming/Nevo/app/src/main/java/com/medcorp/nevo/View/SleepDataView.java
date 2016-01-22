package com.medcorp.nevo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gaillysu on 15/8/17.
 */
public class SleepDataView extends View {
    private Paint mPaint;
    private int mRoundColor;
    private int mTextColor;
    private int mWakeSleepColor;
    private int mLightSleepColor;
    private int mDeepSleepColor;
    private float mTextSize;
    private float mRoundWidth;
    private int mMax;
    private int mProgress;
    private boolean mTextIsDisplayable;
    private JSONObject mSleepAnalysisResult = new JSONObject();

    public static final int STROKE = 0;
    public static final int FILL = 1;

    float[] sweep = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    final float SWEEP_INC = 0.5f;
    enum SLEEPSTATUS{
        ACTIVITY,WAKE,LIGHT,DEEP
    }

    List<Float> sleepDegree = new ArrayList<Float>();
    List<Paint> sleepColor = new ArrayList<Paint>();

    public SleepDataView(Context context) {
        this(context, null);
    }

    public SleepDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepDataView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);
        //get custom attributes
        mRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        mTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.BLUE);
        mTextSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 60);
        mRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        mMax = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        mTextIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        mWakeSleepColor = mTypedArray.getColor(R.styleable.RoundProgressBar_sleepWakeColor, Color.GREEN);
        mLightSleepColor = mTypedArray.getColor(R.styleable.RoundProgressBar_sleepLightColor, Color.LTGRAY);
        mDeepSleepColor = mTypedArray.getColor(R.styleable.RoundProgressBar_sleepDeepColor, Color.BLUE);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * draw outside circle
         */

        int radius = (int) (Math.min(getWidth()/2,getHeight()/2) - mRoundWidth/2);
        mPaint.setColor(mRoundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(getWidth()/2, getHeight()/2, radius, mPaint);

        /**
         * draw mProgress
         */
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent = (int)(((float) mProgress / (float) mMax) * 100);
        float textWidth = mPaint.measureText(percent + "%");

        RectF oval = new RectF(getWidth()/2 - radius, getHeight()/2 - radius, getWidth()/2
                + radius, getHeight()/2 + radius);

        int colors[] = {mWakeSleepColor,mLightSleepColor,mDeepSleepColor,Color.TRANSPARENT};
        Paint[] mPaints;
        mPaints = new Paint[colors.length];
        for (int i = 0; i < colors.length; i++) {
            mPaints[i] = new Paint();
            mPaints[i].setAntiAlias(true);
            mPaints[i].setStrokeWidth(mRoundWidth);
            mPaints[i].setStyle(Paint.Style.STROKE);
            mPaints[i].setColor(colors[i]);
        }

        float start = 0;
        float end = 0;
        float initstart = 0;
        float initend = 0;

        if(mSleepAnalysisResult.has("startDateTime") && mSleepAnalysisResult.has("endDateTime"))
        {
            try {
                long startsleep = mSleepAnalysisResult.getLong("startDateTime");
                long endsleep = mSleepAnalysisResult.getLong("endDateTime");
                if(startsleep == 0 || endsleep ==0 || startsleep==endsleep)
                {
                    if(mTextIsDisplayable)
                    canvas.drawText("----", getWidth()/2 - textWidth / 2, getHeight()/2 + mTextSize /2 + 60, mPaint);
                    return;
                }

                String[] hhmm = new SimpleDateFormat("HH:mm").format(new Date(startsleep)).split(":");

                //calculator the start degree:0~360
                start = ( ((Integer.parseInt(hhmm[0]))%12)*60 )/2.0f;
                if(((Integer.parseInt(hhmm[0]))%12)>=3) start = start - 90;
                else {
                    start = start + 270;
                }
                initstart = start;

                hhmm = new SimpleDateFormat("HH:mm").format(new Date(endsleep)).split(":");
                end = ( ((Integer.parseInt(hhmm[0]))%12)*60 + Integer.parseInt(hhmm[1]))/2.0f;
                if(((Integer.parseInt(hhmm[0]))%12)>=3) end = end - 90;
                else {
                    end = end + 270;
                }
                initend = end;

                int [] values1 = DatabaseHelper.string2IntArray(mSleepAnalysisResult.getString("mergeHourlyWakeTime"));
                int [] values2 = DatabaseHelper.string2IntArray(mSleepAnalysisResult.getString("mergeHourlyLightTime"));
                int [] values3 = DatabaseHelper.string2IntArray(mSleepAnalysisResult.getString("mergeHourlyDeepTime"));

                if(values1.length>0 && values2.length>0 && values3.length>0)
                {
                    // normally, values1 + values2 + values3 = 60
                    // but sometimes , values1 + values2 + values3 < 60 when user break sleep and continue sleep
                    long total = 0; //(endsleep - startsleep) / 1000 / 60;
                    for(int k:values1) total+=k;
                    for(int k:values2) total+=k;
                    for(int k:values3) total+=k;

                    int sleeptotal = 0;
                    for(int k:values2) sleeptotal+=k;
                    for(int k:values3) sleeptotal+=k;

                    //draw text
                    percent = (int)(((float) sleeptotal / (float) total) * 100);
                    textWidth = mPaint.measureText(percent + "%");
                    if(mTextIsDisplayable)
                    canvas.drawText(percent + "%", getWidth()/2 - textWidth / 2, getHeight()/2 + mTextSize /2 + 60, mPaint);

                    //draw circle
                    sleepDegree.clear();
                    sleepColor.clear();

                    //if user 's sleep had got broken sometimes, after have a rest and restart one new sleep.
                    //so the circle view should display the rest time as "blank" color
                    //for let the graph keep good continuity,I follow this rule to draw the circle
                    //the sleep order is active/wake/light/deep ,please see @lastHourlySleepStatus
                    SLEEPSTATUS lastHourlySleepStatus = SLEEPSTATUS.ACTIVITY;

                    for (int j = 0; j < values1.length; j++)
                    {
                        if(lastHourlySleepStatus == SLEEPSTATUS.ACTIVITY)
                        {
                            sleepDegree.add((60-values1[j]-values2[j]-values3[j])/2.0f);
                            sleepColor.add(mPaints[3]);
                            sleepDegree.add(values1[j]/2.0f);
                            sleepColor.add(mPaints[0]);
                            sleepDegree.add(values2[j]/2.0f);
                            sleepColor.add(mPaints[1]);
                            sleepDegree.add(values3[j]/2.0f);
                            sleepColor.add(mPaints[2]);
                            lastHourlySleepStatus = values3[j]>0?SLEEPSTATUS.DEEP:(values2[j]>0?SLEEPSTATUS.LIGHT:(values1[j]>0?SLEEPSTATUS.WAKE:SLEEPSTATUS.ACTIVITY));
                        }
                        else if(lastHourlySleepStatus == SLEEPSTATUS.WAKE)
                        {
                            sleepDegree.add((60-values1[j]-values2[j]-values3[j])/2.0f);
                            sleepColor.add(mPaints[3]);
                            sleepDegree.add(values1[j]/2.0f);
                            sleepColor.add(mPaints[0]);
                            sleepDegree.add(values2[j]/2.0f);
                            sleepColor.add(mPaints[1]);
                            sleepDegree.add(values3[j]/2.0f);
                            sleepColor.add(mPaints[2]);
                            lastHourlySleepStatus = values3[j]>0?SLEEPSTATUS.DEEP:(values2[j]>0?SLEEPSTATUS.LIGHT:(values1[j]>0?SLEEPSTATUS.WAKE:SLEEPSTATUS.ACTIVITY));
                        }
                        else if(lastHourlySleepStatus == SLEEPSTATUS.LIGHT)
                        {
                            sleepDegree.add(values2[j]/2.0f);
                            sleepColor.add(mPaints[1]);
                            sleepDegree.add(values3[j]/2.0f);
                            sleepColor.add(mPaints[2]);
                            sleepDegree.add(values1[j]/2.0f);
                            sleepColor.add(mPaints[0]);
                            sleepDegree.add((60-values1[j]-values2[j]-values3[j])/2.0f);
                            sleepColor.add(mPaints[3]);
                            lastHourlySleepStatus = values3[j]>0?SLEEPSTATUS.DEEP:(values2[j]>0?SLEEPSTATUS.LIGHT:(values1[j]>0?SLEEPSTATUS.WAKE:SLEEPSTATUS.ACTIVITY));
                        }
                        else if(lastHourlySleepStatus == SLEEPSTATUS.DEEP)
                        {
                            sleepDegree.add(values3[j]/2.0f);
                            sleepColor.add(mPaints[2]);
                            sleepDegree.add(values2[j]/2.0f);
                            sleepColor.add(mPaints[1]);
                            sleepDegree.add(values1[j]/2.0f);
                            sleepColor.add(mPaints[0]);
                            sleepDegree.add((60-values1[j]-values2[j]-values3[j])/2.0f);
                            sleepColor.add(mPaints[3]);
                            lastHourlySleepStatus = values3[j]>0?SLEEPSTATUS.DEEP:(values2[j]>0?SLEEPSTATUS.LIGHT:(values1[j]>0?SLEEPSTATUS.WAKE:SLEEPSTATUS.ACTIVITY));
                        }

                    }//end for
                    start = initstart;
                    int i =0;
                    for(Float f:sleepDegree)
                    {
                        canvas.drawArc(oval, start,sweep[i], false, sleepColor.get(i));
                        start += f;
                        if (sweep[i] < f) {
                            sweep[i] += SWEEP_INC;
                        }
                        i = i+1;
                    }
                }

                //start/end/total
                Date startDate = new Date(startsleep);
                Date endDate = new Date(endsleep);
                long total = (endDate.getTime() - startDate.getTime())/1000/60;
                mPaint.setColor(Color.WHITE);
                mPaint.setTypeface(Typeface.DEFAULT);
                mPaint.setTextSize(24f);

                //canvas.drawText("St: "+new SimpleDateFormat("dd/MM HH:mm").format(startDate), oval.right-45,oval.top+30, mPaint);
                //canvas.drawText("Ed: "+new SimpleDateFormat("dd/MM HH:mm").format(endDate), oval.right-45,oval.top+54, mPaint);
                //canvas.drawText("Du: "+ (total>=60?(total/60 + "h "):"") + (total%60) + "min", oval.right-45,oval.top+78, mPaint);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            if(mTextIsDisplayable)
            canvas.drawText("----", getWidth()/2 - textWidth / 2, getHeight()/2 + mTextSize /2 + 60, mPaint);
        }
        //call invalidate to redraw again
        invalidate();
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

    public synchronized void setSleepAnalysisResult(JSONObject result)
    {
        mSleepAnalysisResult = result;
        postInvalidate();
    }

}
