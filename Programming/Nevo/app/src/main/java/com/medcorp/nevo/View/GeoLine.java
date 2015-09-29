package com.medcorp.nevo.View;


import java.math.BigDecimal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GeoLine extends SurfaceView implements SurfaceHolder.Callback {
	
	public static enum DATASOURCETYPE{ 
		Sleep,
		StepCount,
        SleepEfficiency,
        StepCountPercent
	};	
	private Context mContext;
	private int currentX;
	private int oldX;
	private SurfaceHolder sfh;
	
	private  float[] values1 ;			 
	private  float[] values2 ;			 
	private  float[] values3 ;

    private  int x_count = 24; // by day/week/month
    private final int y_count = 7;

    final float SLEEP_H = 80.0f;
    final float SLEEP_L = 30.0f;
    final float STEPS_H = 80.0f;
    final float STEPS_L = 30.0f;
	
	private DATASOURCETYPE  mDataType = DATASOURCETYPE.Sleep;
	
	private  String[] xDateTime = { "00", "01", "02", "03", "04", "05", "06", "07",
			"08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18",
			"19", "20", "21", "22", "23","24" };// 一天的时间24H
	
	private final String[] weekly = { "S", "M", "T", "W", "Th", "F", "Su"};

    private final int tick = 10; // 时间间隔(ms)
    private final int lift = 30; // 坐标系左边距离框架左边框的距离
    private final int top = 30; // 坐标系顶端距离框架顶端框的距离

    //below is calculator on called "onMeasure"
    private int right; // 坐标系右边距离框架左边的距离(!)
    private int bottom ; // 坐标系地段距离框架顶端的距离
    private int gapX; // 两根竖线间的间隙(!)
    private int gapY; // 两根横线间的间隙
	
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
	public GeoLine(Context context) {
		super(context);
        mContext = context;
		init();		
	}

	public GeoLine(Context context, AttributeSet atr) {
		super(context, atr);
        mContext = context;
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
		final Thread thread = new Thread(new Runnable() {
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
	
		float temMax = 0;	
		float temMin = 0;
		float space = 0f;// 平均值
		for (int i = 0; i < values1.length; i++) {
			if (temMax < values1[i]) {
				temMax = values1[i];
			}		
		}
        //0~100%
        if(mDataType == DATASOURCETYPE.SleepEfficiency || mDataType == DATASOURCETYPE.StepCountPercent )
        {
            temMin = 0;
            temMax = 100;
        }
		
		space = (temMax - temMin) / (y_count -1);// 6段有效显示范围
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
		for (int i = 0; i <y_count; i++) {
			if(i==y_count-1) mbackLinePaint.setPathEffect(null);
			else       mbackLinePaint.setPathEffect(effects);
			canvas.drawLine(lift, top + gapY * i, lift + gapX *( x_count), top + gapY
					* i, mbackLinePaint);
			mTextPaint.setTextAlign(Align.LEFT);
			float result = temMin + space * i;// 精确的各个节点的值
			BigDecimal b = new BigDecimal(result);// 新建一个BigDecimal
			float displayVar = b.setScale(1, BigDecimal.ROUND_HALF_UP)
					.floatValue();// 进行小数点一位保留处理现实在坐标系上的数值
			String y_unit="0";
			if(mDataType == DATASOURCETYPE.Sleep)
				y_unit = "" +10*i;
            else if(mDataType == DATASOURCETYPE.SleepEfficiency || mDataType == DATASOURCETYPE.StepCountPercent )
            {
                y_unit = "" + (int)result + "%";
            }
			else
				//y_unit = "" + displayVar;
				y_unit = "" + (int)result ;
			canvas.drawText(y_unit, 0, bottom + 3 - gapY * i,
					mTextPaint);
		}
		for (int i = 0; i < x_count+1; i++) {
			if(i==0) mbackLinePaint.setPathEffect(null);
			else       mbackLinePaint.setPathEffect(effects);
			canvas.drawLine(lift + gapX * i, top, lift + gapX * i, bottom,
					mbackLinePaint);
			mTextPaint.setTextAlign(Align.CENTER);
			canvas.drawText(xDateTime[i], lift + gapX * i, bottom + 24, mTextPaint);
		}
	}

	private void drawChartLine() {
		while (true) {
			try {
				if(currentX<100)clearCanvas();
				drawChart(currentX);// 绘制

				currentX+=10;// 往前进

				if (currentX >= right) {
                    currentX = 0;
                    outPutComments();
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

	void drawChart(int length) {
		if (length == 0)
			oldX = 0;
		//Canvas canvas = sfh.lockCanvas();
		Canvas canvas = sfh.lockCanvas(new Rect(oldX, 0, oldX + length, bottom));// 范围选取正确
		
		int colors[] = {Color.GREEN ,Color.LTGRAY, Color.BLUE};
		
		Paint mPointPaint = new Paint();
		mPointPaint.setAntiAlias(true);
		mPointPaint.setColor(colors[0]);
		
		Paint mLinePaint = new Paint();// 用来画折线
		mLinePaint.setColor(colors[0]);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStrokeWidth(2);
		mLinePaint.setStyle(Style.FILL);

	
		float temMax = 0;
		float temMin = 0;
		float spacePX = 0f;// 平均像素值
		for (int i = 0; i < values1.length; i++) {
			if (temMax < values1[i]) {
				temMax = values1[i];
			}
		}
        //0~100%
        if(mDataType == DATASOURCETYPE.SleepEfficiency || mDataType == DATASOURCETYPE.StepCountPercent )
        {
            temMin = 0;
            temMax = 100;
            mPointPaint.setColor(Color.BLUE);
            mLinePaint.setColor(Color.BLUE);
        }
		if(mDataType == DATASOURCETYPE.Sleep)
		{
			temMax = 60;
			temMin = 0;
		}
		spacePX = (bottom - top) / (temMax - temMin);// 平均每个值说占用的像素值

		float cx = 0f;
		float cy = 0f;
		float dx = 0f;
		float dy = 0f;
		float xoffset = gapX/2;
        float yoffset = 3;
        float circle_r = 5;
		if(values1 != null)
		for (int j = 0; j < values1.length ; j++) {
			cx = lift + gapX * j + xoffset;
			cy = bottom - (values1[j] - temMin) * spacePX - yoffset;
			canvas.drawCircle(cx , cy, circle_r, mPointPaint);
			if(j != values1.length -1)
			{
			dx = lift + gapX * (j + 1) + xoffset;
			dy = bottom - (values1[j + 1] - temMin) * spacePX - yoffset;
			canvas.drawLine(cx, cy, dx, dy, mLinePaint);
			}
		}
		
		mPointPaint.setColor(colors[1]);
		mLinePaint.setColor(colors[1]);
		if(values2 != null)
		for (int j = 0; j < values2.length; j++) {
			cx = lift + gapX * j + xoffset;
			cy = bottom - (values2[j] - temMin) * spacePX - yoffset;
			canvas.drawCircle(cx, cy, circle_r, mPointPaint);
			if(j != values2.length -1)
			{
			dx = lift + gapX * (j + 1) + xoffset;
			dy = bottom - (values2[j + 1] - temMin) * spacePX - yoffset;
			canvas.drawLine(cx, cy, dx, dy, mLinePaint);
			}
		}
		
		mPointPaint.setColor(colors[2]);
		mLinePaint.setColor(colors[2]);
		if(values3 != null)
		for (int j = 0; j < values3.length; j++) {
			cx = lift + gapX * j + xoffset;
			cy = bottom - (values3[j] - temMin) * spacePX - yoffset;
			canvas.drawCircle(cx, cy, circle_r, mPointPaint);
			if(j != values3.length -1)
			{
			dx = lift + gapX * (j + 1)+xoffset;
			dy = bottom - (values3[j + 1] - temMin) * spacePX - yoffset;
			canvas.drawLine(cx, cy, dx, dy, mLinePaint);
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
    private void outPutComments()
    {
        if(values1.length==0) return;

        float total = 0;
        int vaild_number=0;
        for (int i = 0; i < values1.length; i++)
        {
            if(values1[i]>0) {total += values1[i];vaild_number +=1;}
        }
        float average = total / vaild_number;

        String  comments = "";
        Boolean isLow = false;
        if(mDataType == DATASOURCETYPE.StepCountPercent) {
            if (average > STEPS_H) {
                comments = "Goal completion is good,please Keep on!";
            } else if (average < STEPS_L) {
                comments ="Goal completion is too low,please Come on!";
                isLow = true;
            }
        }
        else if(mDataType == DATASOURCETYPE.SleepEfficiency)
        {
            if(average>SLEEP_H) {
                comments = "High quality sleep,Congratulations on you!";
            }
            else if(average<SLEEP_L) {
                comments ="Low quality sleep,Please do more exercise!";
                isLow = true;
            }
        }

        Canvas canvas = sfh.lockCanvas();
        Paint mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(isLow?Color.RED:Color.BLUE);
        mTextPaint.setTextSize(48F);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Align.CENTER);
        canvas.drawText(comments,getWidth()/2,getHeight()/2, mTextPaint);
        sfh.unlockCanvasAndPost(canvas);
    }
	
}
