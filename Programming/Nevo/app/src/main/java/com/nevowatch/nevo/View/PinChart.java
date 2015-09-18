package com.nevowatch.nevo.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.json.JSONObject;

/**
 * Created by gaillysu on 15/8/6.
 */
public class PinChart extends View {

    public static enum RESULTTYPE{
        Sleep,
        Activity
    };

    RESULTTYPE mResultType = RESULTTYPE.Activity;

    private Paint[] mPaints;
    private Paint mTextPaint;
    private RectF mBigOval;
    float[] mSweep = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private static final float SWEEP_INC = 1;

    public  float[] humidity = {0,0,0};
    public final String[] SleepStatus = {"Wake","Light Sleep","Deep Sleep"};
    public final String[] FinishedStatus = {"Done","Todo"};
    private int totalStep;
    private int totalSleep;

    private boolean isNodata()
    {
        float total = 0;
        for(float f:humidity) total +=f;
        return total==0;
    }
    public void initData(RESULTTYPE type,float[] data,int totalValue)
    {
        mResultType = type;
        humidity = data;
        if(type == RESULTTYPE.Activity ) totalStep = totalValue;
        if(type == RESULTTYPE.Sleep ) totalSleep = totalValue;
    }
    public PinChart(Context context) {
        super(context);
    }

    public PinChart(Context context, AttributeSet atr) {
        super(context, atr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float height = getHeight();
        float width = getWidth();
        canvas.drawColor(Color.TRANSPARENT);// 设置背景颜色(透明)

        int colors[] = {Color.GREEN ,Color.LTGRAY, Color.BLUE};
        mPaints = new Paint[humidity.length];
        for (int i = 0; i < humidity.length; i++) {
            mPaints[i] = new Paint();
            mPaints[i].setAntiAlias(true);
            mPaints[i].setStyle(Paint.Style.FILL);
            //mPaints[i].setStrokeWidth(20);
            //mPaints[i].setColor(0x880FF000 + i * 0x019e8860);
            mPaints[i].setColor(colors[i]);
        }
        int top = 75;
        int radius = (int) (Math.min(getWidth()/2,getHeight()/2)-top);
        Paint nodata = new Paint();
        nodata.setColor(Color.WHITE);
        nodata.setAntiAlias(true);
        nodata.setStyle(Paint.Style.STROKE);
        nodata.setTextSize(30F);
        final String nodatastring = "No data";
        float textWidth = nodata.measureText(nodatastring);
        mBigOval = new RectF(getWidth()/2 - radius , getHeight()/2 - radius, getWidth()/2 + radius, getHeight()/2 + radius);// 饼图的四周边界

        mTextPaint = new Paint();// 绘制文本
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(24F);
        float start = 0;

        for (int i = 0; i < humidity.length; i++) {

            if(isNodata()) {
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, nodata);
                canvas.drawText(nodatastring, getWidth()/2 - textWidth / 2, getHeight()/2, nodata);
            }
            else
                canvas.drawArc(mBigOval, start,mSweep[i], true, mPaints[i]);

            start += humidity[i];
            if (mSweep[i] < humidity[i]) {
                mSweep[i] += SWEEP_INC;
            }
            canvas.drawRect(new RectF(getWidth()/2 + radius -20, getHeight()/2 - radius+ 30 * i, getWidth()/2 + radius  , getHeight()/2 - radius + 20 + 30 * i),
                    mPaints[i]);
            if(mResultType == RESULTTYPE.Sleep )
                canvas.drawText(SleepStatus[i] + ": " + (isNodata()?"----":String.format("%1.1f%s", humidity[i]*100.0/360,"%")), getWidth()/2 + radius +3 , getHeight()/2 - radius + 30 * i+20, mTextPaint);
            else
                canvas.drawText(FinishedStatus[i] + ": " + (isNodata()?"----":String.format("%1.1f%s", humidity[i]*100.0/360,"%")), getWidth()/2 + radius+3 , getHeight()/2 - radius + 30 * i+20, mTextPaint);
        }

        if(mResultType == RESULTTYPE.Sleep )
        canvas.drawText("Total Sleep:" +  (isNodata()?"----":(totalSleep>=60?(totalSleep/60 +"h "+ totalSleep%60 + "min"):(totalSleep%60 + "min"))), getWidth()/2 + radius-20,getHeight()/2 - radius + 120, mTextPaint);
        else
        canvas.drawText("Total Steps:" + (isNodata()?"----":totalStep), getWidth()/2 + radius-20,getHeight()/2 - radius + 120, mTextPaint);

        invalidate();
    }

}

