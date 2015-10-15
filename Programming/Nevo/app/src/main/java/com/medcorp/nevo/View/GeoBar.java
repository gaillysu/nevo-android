package com.medcorp.nevo.view;

/*
 * 用于实现柱状图的动态效果
 * */
import java.math.BigDecimal;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GeoBar extends SurfaceView implements
        SurfaceHolder.Callback {

    public static enum DATASOURCETYPE{
        StepCount,
        Distance,
        Calories,
        SleepTime,
        SleepAnalysisTime
    };
    private int currentX;
    private int oldX;
    private SurfaceHolder sfh;

    private  float[] values1;
    private  float[] values2;
    private  float[] values3;

    private  int x_count = 24; // by day/week/month
    private final int y_count = 7;
    private DATASOURCETYPE  mDataType = DATASOURCETYPE.StepCount;

    private String[] xDateTime = { "00", "01", "02", "03", "04", "05", "06", "07",
            "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18",
            "19", "20", "21", "22", "23" ,"24"};// 一天的时间24H

    private final int tick = 10; // 时间间隔(ms)
    private final int lift = 30; // 坐标系左边距离框架左边框的距离
    private final int top = 30; // 坐标系顶端距离框架顶端框的距离

    //below is calculator on called "onMeasure"
    private int right; // 坐标系右边距离框架左边的距离(!)
    private int bottom ; // 坐标系地段距离框架顶端的距离
    private int gapX; // 两根竖线间的间隙(!)
    private int gapY; // 两根横线间的间隙

    private boolean isNodata()
    {
        float total = 0;
        for(float f:values1) total +=f;
        return total==0;
    }
    public void initData(DATASOURCETYPE type,float[] values1,float[] values2,float[] values3)
    {
        this.mDataType = type;
        this.values1 = values1;
        this.values2 = values2;
        this.values3 = values3;
        this.x_count = values1.length;
    }
    public void initXDateTime(String[] xDateTime)
    {
        this.xDateTime = xDateTime;
    }
    private void init()
    {
        setZOrderOnTop(true);// 设置置顶（不然实现不了透明）
        sfh = this.getHolder();
        sfh.addCallback(this);
        sfh.setFormat(PixelFormat.TRANSLUCENT);// 设置背景透明
    }
    public GeoBar(Context context) {
        super(context);
        init();
    }

    // 在这里初始化才是最初始化的。
    public GeoBar(Context context, AttributeSet atr) {
        super(context, atr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        bottom = height - 2*top;
        right = width - lift;
        gapX = (width - lift) / (x_count);
        gapY = (bottom - top)/(y_count-1);
    }

    /**
     * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        currentX = 0;
        clearCanvas();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                drawChartLine();
            }
        });
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }

    protected void GridDraw(Canvas canvas) {
        if (canvas == null || values1 == null) {
            return;
        }
        int h = getHeight();
        float temMax = 0;
        float temMin = 0;
        float space = 0f;// 平均值
        for (int i = 0; i < values1.length; i++) {
            if (temMax < values1[i]) {
                temMax = values1[i];
            }
        }
        if(mDataType == DATASOURCETYPE.SleepTime)
        {
            temMin = 0;
            temMax = 60;
        }
        else if(mDataType == DATASOURCETYPE.SleepAnalysisTime)
        {
            temMin = 0;
            temMax = 12;//hour
        }
        space = (temMax - temMin) / (y_count-1);// 6段有效显示范围
        // textY=Math.round(temMin + space * i);

        Paint mbackLinePaint = new Paint();// 用来画坐标系了
        mbackLinePaint.setColor(Color.WHITE);
        mbackLinePaint.setAntiAlias(true);
        mbackLinePaint.setStrokeWidth(1);
        mbackLinePaint.setStyle(Style.STROKE);
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
        mbackLinePaint.setPathEffect(effects);

        Paint mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        // mTextPaint.setTextAlign(Align.RIGHT);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(24F);// 设置温度值的字体大小
        // 绘制坐标系
        for (int i = 0; i < y_count; i++) {
            if(i==y_count-1) mbackLinePaint.setPathEffect(null);
            else       mbackLinePaint.setPathEffect(effects);
            canvas.drawLine(lift, top + gapY * i, lift + gapX * x_count, top + gapY
                    * i, mbackLinePaint);
            mTextPaint.setTextAlign(Align.LEFT);

            float result = temMin + space * i;// 精确的各个节点的值
            BigDecimal b = new BigDecimal(result);// 新建一个BigDecimal
            float displayVar = b.setScale(1, BigDecimal.ROUND_HALF_UP)
                    .floatValue();// 进行小数点一位保留处理现实在坐标系上的数值
            canvas.drawText("" + (int)result, 0, bottom + 3 - gapY * i,
                    mTextPaint);

        }
        for (int i = 0; i <( x_count +1); i++) {
            if(i==0) mbackLinePaint.setPathEffect(null);
            else       mbackLinePaint.setPathEffect(effects);
            canvas.drawLine(lift + gapX * i, top, lift + gapX * i, bottom,
                    mbackLinePaint);
        }
        for (int i = 0; i < (x_count+1); i++) {
            mTextPaint.setTextAlign(Align.CENTER);
            canvas.drawText(xDateTime[i], lift + gapX * i , bottom + 24,
                    mTextPaint);
        }
    }

    private void drawChartLine() {
        while (true) {
            try {
                if(currentX<100) clearCanvas();
                drawChart(currentX);// 绘制
                currentX+=10;// 往前进

                if (currentX >=getHeight()) {
                    currentX = 0;
                    return;
                }

                try {
                    Thread.sleep(tick);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * add merge rule when draw sleep time.
     * merge rule: wake-->light-->deep-->deep-->light-->wake
     * @param length
     */
    void drawChart(int length) {
        if(isNodata())
        {
            Canvas canvas = sfh.lockCanvas();
            Paint nodata = new Paint();
            nodata.setColor(Color.WHITE);
            nodata.setAntiAlias(true);
            nodata.setStyle(Paint.Style.STROKE);
            nodata.setTextSize(30F);
            final String nodatastring = "No data";
            float textWidth = nodata.measureText(nodatastring);
            canvas.drawText(nodatastring, getWidth()/2 - textWidth / 2, getHeight()/2, nodata);
            sfh.unlockCanvasAndPost(canvas);
            return;
        }
        int h = getHeight();
        int w = getWidth();
        Canvas canvas = sfh.lockCanvas(new Rect(lift, h - length, w, h));// 范围选取正确
        Paint mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setColor(Color.YELLOW);

        Paint mLinePaint = new Paint();
        mLinePaint.setColor(Color.YELLOW);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setStyle(Style.FILL);

        float temMax = 0;
        float temMin = 0;
        float space = 0f;// 平均值
        for (int i = 0; i < values1.length; i++) {
            if (temMax < values1[i]) {
                temMax = values1[i];
            }
        }
        if(mDataType == DATASOURCETYPE.SleepTime)
        {
            temMin = 0;
            temMax = 60;
        }
        else if(mDataType == DATASOURCETYPE.SleepAnalysisTime)
        {
            temMin = 0;
            temMax = 12;// hour
        }

        if(temMax == temMin) space = 0;
        else space = (bottom - top) / (temMax - temMin);// 平均每个值说占用的像素值

        float cx = 0f;
        float cy = 0f;
        float dx = 0f;
        float dy = 0f;

        if(mDataType != DATASOURCETYPE.SleepTime && mDataType != DATASOURCETYPE.SleepAnalysisTime)
        {
            if(values1!=null)
            for (int j = 0; j < values1.length; j++) {
                cx  = lift + gapX * j;
                cy = bottom - (values1[j] - temMin) * space;
                dx = lift + gapX * (j + 1) - gapX*0.15f;

                if (values1[j] == 0) {
                //    canvas.drawRect(new RectF(cx, bottom - 2, dx - 2, bottom),
                //            mLinePaint);// 当值是0时，绘制2px的矩形，表示这里有值
                } else {
                    canvas.drawRect(new RectF(cx, cy, dx - 2, bottom ),
                            mLinePaint);
                }
            }
        }
        else if(mDataType == DATASOURCETYPE.SleepTime)
        {
            //sleep data
            int colors[] = {Color.GREEN ,Color.LTGRAY, Color.BLUE};
            int last = values3[0]>0?3:(values2[0]>0?2:1);
            if(values1!=null && values2!=null && values3!=null)
            {
                for (int j = 0; j < values1.length; j++) {
                    /** //no merge, draw graph with fixed order:wake/light/deep
                    cx = lift + gapX * j;
                    dx = lift + gapX * (j + 1) - gapX*0.15f;
                    dy = 0;
                    cy = (values1[j] - temMin) * space;

                    //wake bar
                    mLinePaint.setColor(colors[0]);
                    canvas.drawRect(new RectF(cx, bottom -dy-cy, dx - 2, bottom-dy),
                                mLinePaint);
                    //light bar
                    dy = (values1[j] - temMin) * space;
                    cy = (values2[j] - temMin) * space;
                    mLinePaint.setColor(colors[1]);
                    canvas.drawRect(new RectF(cx, bottom - dy-cy, dx - 2, bottom-dy),
                            mLinePaint);
                    //deep bar
                    dy = (values1[j] - temMin) * space + (values2[j] - temMin) * space;
                    cy = (values3[j] - temMin) * space;
                    mLinePaint.setColor(colors[2]);
                    canvas.drawRect(new RectF(cx, bottom - dy-cy, dx - 2, bottom-dy),
                            mLinePaint);
                    */

                    //values1[j] + values2[j] + values3[j] == 60 or 0
                    //when sleep got broken, reset last to 1
                    if(values1[j] + values2[j] + values3[j] == 0) last = 1;
                    if(last == 1) {
                        cx = lift + gapX * j;
                        dx = lift + gapX * (j + 1) - gapX*0.15f;
                        dy = 0;
                        cy = (values1[j] - temMin) * space;

                        //wake bar
                        mLinePaint.setColor(colors[0]);
                        canvas.drawRect(new RectF(cx, bottom - dy - cy, dx - 2, bottom - dy),
                                mLinePaint);
                        //light bar
                        dy = (values1[j] - temMin) * space;
                        cy = (values2[j] - temMin) * space;
                        mLinePaint.setColor(colors[1]);
                        canvas.drawRect(new RectF(cx, bottom - dy - cy, dx - 2, bottom - dy),
                                mLinePaint);
                        //deep bar
                        dy = (values1[j] - temMin) * space + (values2[j] - temMin) * space;
                        cy = (values3[j] - temMin) * space;
                        mLinePaint.setColor(colors[2]);
                        canvas.drawRect(new RectF(cx, bottom - dy - cy, dx - 2, bottom - dy),
                                mLinePaint);
                        last = values3[j]>0?3:(values2[j]>0?2:1);
                    }
                    else if(last == 2)
                    {
                            cx = lift + gapX * j;
                            dx = lift + gapX * (j + 1) - gapX*0.15f;
                            dy = 0;
                            cy = (values2[j] - temMin) * space;

                            //light bar
                            mLinePaint.setColor(colors[1]);
                            canvas.drawRect(new RectF(cx, bottom - dy - cy, dx - 2, bottom - dy),
                                    mLinePaint);
                            //deep bar
                            dy = (values2[j] - temMin) * space;
                            cy = (values3[j] - temMin) * space;
                            mLinePaint.setColor(colors[2]);
                            canvas.drawRect(new RectF(cx, bottom - dy - cy, dx - 2, bottom - dy),
                                mLinePaint);

                            //wake bar
                            dy = (values2[j] - temMin) * space + (values3[j] - temMin) * space;
                            cy = (values1[j] - temMin) * space;
                            mLinePaint.setColor(colors[0]);
                            canvas.drawRect(new RectF(cx, bottom - dy - cy, dx - 2, bottom - dy),
                                    mLinePaint);

                            last = values1[j]>0?1:(values3[j]>0?3:2);
                    }
                    else if(last == 3)
                    {
                        cx = lift + gapX * j;
                        dx = lift + gapX * (j + 1) - gapX*0.15f;
                        dy = 0;
                        cy = (values3[j] - temMin) * space;

                        //deep bar
                        mLinePaint.setColor(colors[2]);
                        canvas.drawRect(new RectF(cx, bottom - dy - cy, dx - 2, bottom - dy),
                                mLinePaint);

                        //light bar
                        dy = (values3[j] - temMin) * space;
                        cy = (values2[j] - temMin) * space;
                        mLinePaint.setColor(colors[1]);
                        canvas.drawRect(new RectF(cx, bottom - dy - cy, dx - 2, bottom - dy),
                                mLinePaint);
                        //wake bar
                        dy = (values2[j] - temMin) * space+(values3[j] - temMin) * space;
                        cy = (values1[j] - temMin) * space;
                        mLinePaint.setColor(colors[0]);
                        canvas.drawRect(new RectF(cx, bottom - dy - cy, dx - 2, bottom - dy),
                                mLinePaint);

                        last = values1[j]>0?1:(values2[j]>0?2:3);
                    }
                }
            }
        }
        else if(mDataType == DATASOURCETYPE.SleepAnalysisTime)
        {
            //sleep data
            int colors[] = {Color.GREEN ,Color.LTGRAY, Color.BLUE};
            if(values1!=null && values2!=null && values3!=null)
            {
                for (int j = 0; j < values1.length; j++) {
                    //no merge, draw graph with fixed order:wake/light/deep
                     cx = lift + gapX * j;
                     dx = lift + gapX * (j + 1) - gapX*0.15f;
                     dy = 0;
                     cy = (values1[j] - temMin) * space;

                     //wake bar
                     mLinePaint.setColor(colors[0]);
                     canvas.drawRect(new RectF(cx, bottom -dy-cy, dx - 2, bottom-dy),
                     mLinePaint);
                     //light bar
                     dy = (values1[j] - temMin) * space;
                     cy = (values2[j] - temMin) * space;
                     mLinePaint.setColor(colors[1]);
                     canvas.drawRect(new RectF(cx, bottom - dy-cy, dx - 2, bottom-dy),
                     mLinePaint);
                     //deep bar
                     dy = (values1[j] - temMin) * space + (values2[j] - temMin) * space;
                     cy = (values3[j] - temMin) * space;
                     mLinePaint.setColor(colors[2]);
                     canvas.drawRect(new RectF(cx, bottom - dy-cy, dx - 2, bottom-dy),
                     mLinePaint);
                }
            }
        }

        sfh.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
    }

    /**
     * 把画布擦干净，准备绘图使用。
     */
    private void clearCanvas() {

        Canvas canvas = sfh.lockCanvas();

        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);// 清除画布

        GridDraw(canvas);

        sfh.unlockCanvasAndPost(canvas);
    }
}
