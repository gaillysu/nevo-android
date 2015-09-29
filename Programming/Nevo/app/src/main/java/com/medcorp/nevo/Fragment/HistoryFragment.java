package com.medcorp.nevo.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.packet.DailyTrackerInfoNevoPacket;
import com.medcorp.nevo.ble.model.packet.DailyTrackerNevoPacket;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.request.ReadDailyTrackerInfoNevoRequest;
import com.medcorp.nevo.ble.model.request.ReadDailyTrackerNevoRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.history.DateAdapter;
import com.medcorp.nevo.history.SpecialCalendar;
import com.medcorp.nevo.history.database.DatabaseHelper;
import com.medcorp.nevo.history.database.IDailyHistory;
import com.medcorp.nevo.model.DailyHistory;
import com.medcorp.nevo.view.GeoBar;
import com.medcorp.nevo.view.GeoLine;
import com.medcorp.nevo.view.RoundProgressBar;
import com.medcorp.nevo.view.SleepDataView;
import com.medcorp.nevo.view.StepPickerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * GoalFragment aims to set goals including Moderate, Intensive, Sportive and Custom
 */
public class HistoryFragment extends Fragment implements OnSyncControllerListener,OnGestureListener {


    private static final String TAG="HistoryFragment";
    public static final String HISTORYFRAGMENT= "HistoryFragment";
    public static final int HISTORYPOSITION = 5;
    private Context mCtx;

    private int TotalHistory;
    private int currentHistory;
    private boolean syncAllFlag = true;

    private ViewFlipper flipper1 = null;
    private GridView gridView = null;
    private GestureDetector gestureDetector = null;
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;
    private int week_c = 0;
    private int week_num = 0;
    private String currentDate = "";
    private static int jumpWeek = 0;
    private static int jumpMonth = 0;
    private static int jumpYear = 0;
    private DateAdapter dateAdapter;
    private int daysOfMonth = 0; // 某月的天数
    private int dayOfWeek = 0; // 具体某一天是星期几
    private int weeksOfMonth = 0;
    private SpecialCalendar sc = null;
    private boolean isLeapyear = false; // 是否为闰年
    private int selectPostion = 0;
    private String dayNumbers[] = new String[7];
    private TextView tvDate;
    private int currentYear;
    private int currentMonth;
    private int currentWeek;
    private int currentDay;
    private int currentNum;
    private boolean isStart;
    private Date mCurrentDate = new Date();
    float currentStep = 0;
    float totalSleep = 0;

    float[] wake;
    float[] light;
    float[] deep;
    float[] stepCount;
    String[] xTimeDate;

    float goalStepCount = 10000;
    float percent[] = {0,360};
    float degree[];
    JSONObject sleepAnalysisResult = new JSONObject();

    LinearLayout mLayoutGraphContent;
    HorizontalScrollView mHScrollView ;
    RelativeLayout mLayoutHeader;
    TextView mDailyTextView;
    TextView mWeeklyTextView;
    TextView mMonthlyTextView;

    enum VIEWMODE{
    Daily,Weekly,Monthly
    }
    private VIEWMODE mViewMode = VIEWMODE.Daily;

    private GestureDetector gDetector;

    private void resetZero()
    {
        wake = new float[24];
        light = new float[24];
        deep = new float[24];
        degree = new float[]{0,0,0};
        stepCount = new float[24];
        percent = new float[]{0,0};
        currentStep = 0;
        totalSleep = 0;
        sleepAnalysisResult = new JSONObject();
    }

    private int initGraphData(List<IDailyHistory> historyarray)
    {
        resetZero();
        //if data sync not finished, return;
        if(historyarray.isEmpty()) {return -1;}

        float total_wake = 0;
        float total_light = 0;
        float total_deep = 0;

        float[] temp_wake = new float[historyarray.size()];
        float[] temp_light= new float[historyarray.size()];
        float[] temp_deep= new float[historyarray.size()];
        float[] tempStepCount= new float[historyarray.size()];
        xTimeDate = new String[historyarray.size()+1];
        xTimeDate[historyarray.size()]="--";
        int index =0;
        for(IDailyHistory history:historyarray) {
                try {
                    JSONObject json = new JSONObject(history.getRemarks());
                    xTimeDate[index] = json.getString("createDate").split("-")[2];
                } catch (JSONException e) {
                    e.printStackTrace();
                    xTimeDate[index] = "--";
                }
                if(history.getHourlySleepTime().isEmpty() || history.getHourlysteps().isEmpty())
                {
                  index++;
                  continue;
                }
                //remove '[' ,']'
                String s = history.getHourlyWakeTime().substring(1, history.getHourlyWakeTime().length() - 1);
                String[] temp = s.split(",");
                for (int i = 0; i < temp.length; i++)
                    wake[i] = Integer.parseInt(temp[i].trim());

                s = history.getHourlyLightTime().substring(1, history.getHourlyLightTime().length() - 1);
                temp = s.split(",");
                for (int i = 0; i < temp.length; i++)
                    light[i] = Integer.parseInt(temp[i].trim());

                s = history.getHourlDeepTime().substring(1, history.getHourlDeepTime().length() - 1);
                temp = s.split(",");
                for (int i = 0; i < temp.length; i++)
                    deep[i] = Integer.parseInt(temp[i].trim());

                s = history.getHourlysteps().substring(1, history.getHourlysteps().length() - 1);
                temp = s.split(",");
                for (int i = 0; i < temp.length; i++)
                    stepCount[i] = Integer.parseInt(temp[i].trim());

                currentStep = history.getSteps();
                total_wake = history.getTotalWakeTime();
                total_light = history.getTotalLightTime();
                total_deep = history.getTotalDeepTime();
                totalSleep = history.getTotalSleepTime();
                sleepAnalysisResult = DatabaseHelper.getInstance(mCtx).getSleepZone(new Date(history.getCreated()));
                if(mViewMode != VIEWMODE.Daily) {

                    tempStepCount[index] = history.getSteps();

                    try {
                        temp_wake[index] =0;
                        temp_light[index] =0;
                        temp_deep[index] =0;

                        long startsleep = sleepAnalysisResult.getLong("startDateTime");
                        long endsleep = sleepAnalysisResult.getLong("endDateTime");

                        if(startsleep == 0 || endsleep ==0 || startsleep==endsleep)
                        {
                            //the day has no sleep, go to next day
                            index++;
                            continue;
                        }

                        int [] values1 = DatabaseHelper.string2IntArray(sleepAnalysisResult.getString("mergeHourlyWakeTime"));
                        int [] values2 = DatabaseHelper.string2IntArray(sleepAnalysisResult.getString("mergeHourlyLightTime"));
                        int [] values3 = DatabaseHelper.string2IntArray(sleepAnalysisResult.getString("mergeHourlyDeepTime"));

                        for(int i:values1)temp_wake[index] +=i;temp_wake[index] /=60.0f;
                        for(int i:values2)temp_light[index] +=i;temp_light[index] /=60.0f;
                        for(int i:values3)temp_deep[index] +=i;temp_deep[index] /=60.0f;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                //next day
                index++;
        }
        //end fill
        if(mViewMode != VIEWMODE.Daily)
        {
            //refill data which comes from tempStepCount/temp_wake/temp_light/temp_deep
            stepCount = tempStepCount;
            wake = temp_wake;
            light = temp_light;
            deep = temp_deep;
        }
        else
        {
            xTimeDate = new String[]{ "00", "01", "02", "03", "04", "05", "06", "07",
                    "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18",
                    "19", "20", "21", "22", "23","24" };
        }
        if(totalSleep>0)
            degree = new float[]{360.0f*total_wake/totalSleep,
                360.0f*total_light/totalSleep,
                360.0f*total_deep/totalSleep
            };
        else
            degree = new float[]{360,0,0};

        goalStepCount = Integer.valueOf(StepPickerView.getStepTextFromPreference(mCtx));

        percent = new float[]{360.0f*currentStep/goalStepCount, currentStep>goalStepCount?0:360.0f*(goalStepCount-currentStep)/goalStepCount};

        return 1;
    }

    private void showGraph()
    {
        int res = -1;
        if(mViewMode == VIEWMODE.Weekly)
        {
            //res = initGraphData(getLastweekHistory(mCurrentDate));
            res = initGraphData(getCurrentweekHistory());
        }
        else if(mViewMode == VIEWMODE.Monthly)
        {
            //res = initGraphData(getLastmonthHistory(mCurrentDate));
            res = initGraphData(getCurrentmonthHistory());
        }
        else {
            res = initGraphData(getDailyHistory(mCurrentDate));
        }
        if(res == 0) {
            //Toast.makeText(mCtx,"No records,please wait a few moment for synchronous finished!",Toast.LENGTH_LONG).show();
            if(mViewMode != VIEWMODE.Daily)
            {
                return;
            }
        }
        if(res < 0) {
           //Toast.makeText(mCtx,"No records for this date, "+ new SimpleDateFormat("yyyy MMM dd").format(mCurrentDate),Toast.LENGTH_LONG).show();
            if(mViewMode != VIEWMODE.Daily)
            {
                return;
            }
        }

        DisplayMetrics d = mCtx.getResources().getDisplayMetrics();
        int w = d.widthPixels;
        int h = d.heightPixels;

        final int headerHeight = (int)mCtx.getResources().getDimensionPixelSize(R.dimen.header_height);
        h = h - headerHeight;
        final int gridHeight = (int)(h*0.152f);
        final int graphHeight = (int)(h*0.424f);

        //layoutGraphContent
        mLayoutGraphContent.removeAllViews();

        int modeFlag = 1; //1,2,3
        if(mViewMode == VIEWMODE.Daily) modeFlag = 1;
        else modeFlag = 2;
        //动态代码加入新页: mode1--睡眠一屏，运动一屏，左右滑屏切换显示
        if(modeFlag == 1)
        for(int k =0;k<1;k++)
        {
                LinearLayout l3 = new LinearLayout(mCtx);
                l3.setLayoutParams(new LinearLayout.LayoutParams(
                        w, LayoutParams.WRAP_CONTENT));
                l3.setOrientation(LinearLayout.VERTICAL);

                final LinearLayout  l31 = new LinearLayout(mCtx);
                l31.setLayoutParams(new LinearLayout.LayoutParams(
                        w, graphHeight));
                final GeoBar barSleep = new GeoBar(mCtx);
                final GeoBar barStep = new GeoBar(mCtx);
                final RelativeLayout sleepView = (RelativeLayout) LayoutInflater.from(mCtx).inflate(R.layout.layout_sleepanalysis,null);
                //final PinChart  pinchart = new PinChart(mCtx);
                final RelativeLayout stepView = (RelativeLayout) LayoutInflater.from(mCtx).inflate(R.layout.layout_dailysteps_result,null);

                if(k==0) {
                    barSleep.initData(GeoBar.DATASOURCETYPE.SleepTime, wake, light, deep);
                    barSleep.setLayoutParams(new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    l31.setBackgroundResource(android.R.color.darker_gray);
                    l31.addView(barSleep);

                    //start sleep analysis data
                    SleepDataView sleepGraph = (SleepDataView) sleepView.findViewById(R.id.roundProgressBar);
                    sleepGraph.setSleepAnalysisResult(sleepAnalysisResult);
                    sleepView.setVisibility(View.GONE);
                    l31.addView(sleepView);
                }

                LinearLayout  l32 = new LinearLayout(mCtx);
                l32.setLayoutParams(new LinearLayout.LayoutParams(
                        w, graphHeight));
                l32.setOrientation(LinearLayout.HORIZONTAL);

                if(k==0) {
                    barStep.initData(GeoBar.DATASOURCETYPE.StepCount, stepCount,null,null);
                    barStep.setLayoutParams(new LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    l32.setBackgroundResource(android.R.color.holo_blue_light);
                    l32.addView(barStep);

                    //pinchart.initData(PinChart.RESULTTYPE.Activity,percent,(int)currentStep);
                    //pinchart.setLayoutParams(new LinearLayout.LayoutParams(
                    //        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    //pinchart.setVisibility(View.GONE);
                    //l32.addView(pinchart);

                    //check No data
                    float total = 0;
                    for(float f:percent) total +=f;
                    RoundProgressBar stepRoundProgressBar = (RoundProgressBar)stepView.findViewById(R.id.roundProgressBar);
                    stepRoundProgressBar.setProgressWithValue((int)(currentStep*100.0/goalStepCount),total==0?-1:(int)currentStep);
                    stepView.setVisibility(View.GONE);
                    l32.addView(stepView);

                }

            l3.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gDetector.onTouchEvent(event);
                }
            });

            l3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(barSleep.getVisibility()==View.VISIBLE)
                        {
                            barSleep.setVisibility(View.GONE);
                            barStep.setVisibility(View.GONE);
                            sleepView.setVisibility(View.VISIBLE);
                            //pinchart.setVisibility(View.VISIBLE);
                            stepView.setVisibility(View.VISIBLE);
                            sleepView.startAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.anim_enter));
                            //pinchart.startAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.anim_enter));
                            stepView.startAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.anim_enter));
                        }
                        else
                        {
                            sleepView.startAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.anim_exit));
                            //pinchart.startAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.anim_exit));
                            stepView.startAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.anim_exit));
                            barSleep.setVisibility(View.VISIBLE);
                            barStep.setVisibility(View.VISIBLE);
                            sleepView.setVisibility(View.GONE);
                            //pinchart.setVisibility(View.GONE);
                            stepView.setVisibility(View.GONE);
                        }
                    }
                });

                l3.addView(l31);
                l3.addView(l32);
                mLayoutGraphContent.addView(l3);
        } //end for

        //动态代码加入新页: mode2--睡眠、运动共用一屏，点击切换显示
        else if(modeFlag == 2)
        for(int k =0;k<1;k++)
        {
            LinearLayout l3 = new LinearLayout(mCtx);
            l3.setLayoutParams(new LinearLayout.LayoutParams(
                    w, LayoutParams.WRAP_CONTENT));
            l3.setOrientation(LinearLayout.VERTICAL);

            final LinearLayout  l31 = new LinearLayout(mCtx);
            l31.setLayoutParams(new LinearLayout.LayoutParams(
                    w, graphHeight));
            l31.setBackgroundResource(android.R.color.darker_gray);
            l31.setOrientation(LinearLayout.HORIZONTAL);
            final GeoBar bar = new GeoBar(mCtx);
            bar.initXDateTime(xTimeDate);
            bar.initData(GeoBar.DATASOURCETYPE.SleepAnalysisTime, wake, light, deep);
            bar.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            l31.addView(bar);

            final GeoLine geoline = new GeoLine(mCtx);

            float[] sleepEfficiency = new float[wake.length];
            for(int i=0;i<sleepEfficiency.length;i++)
            {
                if((wake[i]+light[i]+deep[i])>0)
                  sleepEfficiency[i] =  100.0f*(light[i]+deep[i])/(wake[i]+light[i]+deep[i]);
                else
                  sleepEfficiency[i] = 0;
            }
            geoline.initData(GeoLine.DATASOURCETYPE.SleepEfficiency, sleepEfficiency, null, null);
            geoline.initXDateTime(xTimeDate);
            geoline.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            geoline.setVisibility(View.GONE);
            l31.addView(geoline);


            LinearLayout  l32 = new LinearLayout(mCtx);
            l32.setLayoutParams(new LinearLayout.LayoutParams(
                    w, graphHeight));
            l32.setOrientation(LinearLayout.HORIZONTAL);
            l32.setBackgroundResource(android.R.color.holo_blue_light);
            final GeoBar bar2 = new GeoBar(mCtx);
            bar2.initXDateTime(xTimeDate);
            bar2.initData(GeoBar.DATASOURCETYPE.StepCount, stepCount,null,null);
            bar2.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            l32.addView(bar2);

            final GeoLine geoline2 = new GeoLine(mCtx);
            float[] stepCountPercent = new float[stepCount.length];
            for(int i=0;i<stepCountPercent.length;i++)
            {
                stepCountPercent[i] = stepCount[i]>goalStepCount?100.0f:100.0f*stepCount[i]/goalStepCount;
            }
            geoline2.initData(GeoLine.DATASOURCETYPE.StepCountPercent, stepCountPercent, null, null);
            geoline2.initXDateTime(xTimeDate);
            geoline2.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            geoline2.setVisibility(View.GONE);
            l32.addView(geoline2);

            l3.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gDetector.onTouchEvent(event);
                }
            });
            l3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if(bar.getVisibility() == View.VISIBLE) {
                       bar.setVisibility(View.GONE);
                       bar2.setVisibility(View.GONE);
                       geoline.setVisibility(View.VISIBLE);
                       geoline2.setVisibility(View.VISIBLE);
                   }
                   else
                   {
                       bar.setVisibility(View.VISIBLE);
                       bar2.setVisibility(View.VISIBLE);
                       geoline.setVisibility(View.GONE);
                       geoline2.setVisibility(View.GONE);
                   }
                }
            });

            l3.addView(l31);
            l3.addView(l32);
            mLayoutGraphContent.addView(l3);

        } //end for
        /**
        //动态代码加入新页: mode3--睡眠、运动共用一屏，点击切换显示,并且支持左右连续滑动（mode1+mode2)
        else if(modeFlag == 3)
        for(int k = SyncController.Singleton.getInstance(getActivity()).getHistory(mSelectdays).size()-1;k>=0;k--)
        {
            List<IDailyHistory> historyarray = SyncController.Singleton.getInstance(getActivity()).getHistory(mSelectdays);

            mCurrentDate = new Date(historyarray.get(k).getCreated());
            initGraphData();

            PinChart pinchart = new PinChart(mCtx);
            PinChart pinchart2 = new PinChart(mCtx);
            GeoLine geoline = new GeoLine(mCtx);
            GeoBar bar = new GeoBar(mCtx);

            LinearLayout l3 = new LinearLayout(mCtx);
            l3.setLayoutParams(new LinearLayout.LayoutParams(
                    w, LayoutParams.WRAP_CONTENT));
            l3.setOrientation(LinearLayout.VERTICAL);

            LinearLayout  l31 = new LinearLayout(mCtx);
            l31.setLayoutParams(new LinearLayout.LayoutParams(
                    w, h/2-130));
            l31.setBackgroundResource(android.R.color.holo_blue_light);
            l31.setOrientation(LinearLayout.HORIZONTAL);

            pinchart2.initData(PinChart.RESULTTYPE.Activity,percent,(int)currentStep);
            pinchart2.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            //l31.addView(pinchart2);

            pinchart.initData(PinChart.RESULTTYPE.Sleep, degree,(int)totalSleep);
            pinchart.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            l31.addView(pinchart);


            LinearLayout  l32 = new LinearLayout(mCtx);
            l32.setLayoutParams(new LinearLayout.LayoutParams(
                    w, h/2-128));
            l32.setBackgroundResource(android.R.color.black);
            l32.setOrientation(LinearLayout.HORIZONTAL);


            bar.initData(GeoBar.DATASOURCETYPE.StepCount, hourlyStepCount,null,null);
            bar.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            //l32.addView(bar);

            geoline.initData(GeoLine.DATASOURCETYPE.Sleep, wake, light, deep);
            geoline.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            l32.addView(geoline);

            l3.addView(l31);
            l3.addView(l32);
            mLayoutGraphContent.addView(l3);
        } //end for
        */

        //mHScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_graph, container, false);
        mCtx = getActivity();
        mLayoutHeader = (RelativeLayout) rootView.findViewById(R.id.layoutheader);
        mLayoutGraphContent = (LinearLayout) rootView.findViewById(R.id.layoutGraphContent);
        mHScrollView = (HorizontalScrollView) rootView.findViewById(R.id.scrollView1);
        mDailyTextView = (TextView)rootView.findViewById(R.id.btnDaily);
        mDailyTextView.setTypeface(null, Typeface.BOLD);
        mWeeklyTextView = (TextView)rootView.findViewById(R.id.btnWeekly);
        mMonthlyTextView = (TextView)rootView.findViewById(R.id.btnMonthly);

        mDailyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewMode = VIEWMODE.Daily;
                mDailyTextView.setTypeface(null, Typeface.BOLD);
                mWeeklyTextView.setTypeface(null, Typeface.NORMAL);
                mMonthlyTextView.setTypeface(null, Typeface.NORMAL);
                mCurrentDate = new Date();
                resetGridCalendar();
                showGraph();
            }
        });
        mWeeklyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewMode = VIEWMODE.Weekly;
                mDailyTextView.setTypeface(null, Typeface.NORMAL);
                mWeeklyTextView.setTypeface(null, Typeface.BOLD);
                mMonthlyTextView.setTypeface(null, Typeface.NORMAL);
                mCurrentDate = new Date();
                resetGridCalendar();
                showGraph();
            }
        });
        mMonthlyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewMode = VIEWMODE.Monthly;
                mDailyTextView.setTypeface(null, Typeface.NORMAL);
                mWeeklyTextView.setTypeface(null, Typeface.NORMAL);
                mMonthlyTextView.setTypeface(null, Typeface.BOLD);
                mCurrentDate = new Date();
                resetGridCalendar();
                showGraph();
            }
        });

        //add Data Selector
        initCalendar();
        tvDate = (TextView) rootView.findViewById(R.id.tv_date);
        tvDate.setText(year_c + "年" + month_c + "月" + day_c + "日");
        gestureDetector = new GestureDetector(this);
        flipper1 = (ViewFlipper) rootView.findViewById(R.id.flipper1);
        dateAdapter = new DateAdapter(mCtx, getResources(), currentYear,
                currentMonth, currentWeek, currentNum, selectPostion,
                currentWeek == 1 ? true : false);
        addGridView();
        dayNumbers = dateAdapter.getDayNumbers();
        gridView.setAdapter(dateAdapter);
        selectPostion = dateAdapter.getTodayPosition();
        gridView.setSelection(selectPostion);
        flipper1.addView(gridView, 0);
        //end added

        resetZero();
        showGraph();

        gDetector = new GestureDetector(new OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
                if (motionEvent.getX() - motionEvent2.getX() > 10
                        || motionEvent.getY() - motionEvent2.getY() > 10) {
                // 向左滑
                // next day
                if(mViewMode == VIEWMODE.Daily)
                {
                    mCurrentDate = getNextDay(mCurrentDate);
                    //refresh data
                    showGraph();
                    //end refresh
                    refreshGridCalendarDaily();
                }
                // next week
                else if(mViewMode == VIEWMODE.Weekly)
                {
                    refreshGridCalendarWeekly(true);
                }
                // next month
                else if(mViewMode == VIEWMODE.Monthly)
                {
                    refreshGridCalendarMonthly(true);
                }
                return true;
                }
                else if (motionEvent.getX() - motionEvent2.getX() < -10
                        || motionEvent.getY() - motionEvent2.getY() < -10) {
                // 向右滑
                    // last day
                    if(mViewMode == VIEWMODE.Daily)
                    {
                        mCurrentDate = getLastDay(mCurrentDate);
                        //refresh data
                        showGraph();
                        //end refresh
                        refreshGridCalendarDaily();
                    }
                    // last week
                    else if(mViewMode == VIEWMODE.Weekly)
                    {
                        refreshGridCalendarWeekly(false);
                    }
                    // last month
                    else if(mViewMode == VIEWMODE.Monthly)
                    {
                        refreshGridCalendarMonthly(false);
                    }
                return true;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SyncController.Singleton.getInstance(getActivity()).isConnected()) {
            //if sync not done,force sync all days(max 7 days) once
            if(getDailyHistory(new Date()).isEmpty())
            {
                TotalHistory = 0;
                currentHistory = 0;
                syncAllFlag = true;
                SyncController.Singleton.getInstance(getActivity()).getDailyTrackerInfo(syncAllFlag);
            }
            else //only sync current day
            {
                TotalHistory = 1;
                currentHistory = 0;
                syncAllFlag = false;
                SyncController.Singleton.getInstance(getActivity()).getDailyTrackerInfo(syncAllFlag);
            }
        }
    }

    @Override
    public void packetReceived(NevoPacket packet) {
        if((byte) ReadDailyTrackerInfoNevoRequest.HEADER == packet.getHeader())
        {
            DailyTrackerInfoNevoPacket infopacket = packet.newDailyTrackerInfoNevoPacket();
            TotalHistory = infopacket.getDailyTrackerInfo().size();
        }
        else if((byte) ReadDailyTrackerNevoRequest.HEADER == packet.getHeader()) {
            DailyTrackerNevoPacket thispacket = packet.newDailyTrackerNevoPacket();
            currentHistory++;
            if(currentHistory == TotalHistory)
            {
                //if current day step count and sleep time both has no change, do nothing
                if(!syncAllFlag)
                {
                    List<IDailyHistory> list = getDailyHistory(mCurrentDate);
                    if(!list.isEmpty())
                    {
                        if(thispacket.getDailySteps() == list.get(0).getSteps()
                                && thispacket.getTotalSleepTime() == list.get(0).getTotalSleepTime())
                            return;
                    }
                }
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //refresh data after 1s for save local database done.
                        mCurrentDate = new Date();
                        selectPostion = dateAdapter.getTodayPosition();
                        dateAdapter.setSeclection(selectPostion);
                        dateAdapter.notifyDataSetChanged();
                        showGraph();
                        //end refresh
                    }
                },1000);
            }
        }
    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        ((MainActivity)getActivity()).replaceFragment(isConnected?HistoryFragment.HISTORYPOSITION:ConnectAnimationFragment.CONNECTPOSITION, isConnected?HistoryFragment.HISTORYFRAGMENT:ConnectAnimationFragment.CONNECTFRAGMENT);
    }
    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

    }

    private void initCalendar()
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        currentDate = sdf.format(date);
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);
        currentYear = year_c;
        currentMonth = month_c;
        currentDay = day_c;
        sc = new SpecialCalendar();
        getCalendar(year_c, month_c);
        week_num = getWeeksOfMonth();
        currentNum = week_num;
        if (dayOfWeek == 7) {
            week_c = day_c / 7 + 1;
        } else {
            if (day_c <= (7 - dayOfWeek)) {
                week_c = 1;
            } else {
                if ((day_c - (7 - dayOfWeek)) % 7 == 0) {
                    week_c = (day_c - (7 - dayOfWeek)) / 7 + 1;
                } else {
                    week_c = (day_c - (7 - dayOfWeek)) / 7 + 2;
                }
            }
        }
        currentWeek = week_c;
        getCurrent();
    }
    /**
     * 判断某年某月所有的星期数
     *
     * @param year
     * @param month
     */
    public int getWeeksOfMonth(int year, int month) {
        // 先判断某月的第一天为星期几
        int preMonthRelax = 0;
        int dayFirst = getWhichDayOfWeek(year, month);
        int days = sc.getDaysOfMonth(sc.isLeapYear(year), month);
        if (dayFirst != 7) {
            preMonthRelax = dayFirst;
        }
        if ((days + preMonthRelax) % 7 == 0) {
            weeksOfMonth = (days + preMonthRelax) / 7;
        } else {
            weeksOfMonth = (days + preMonthRelax) / 7 + 1;
        }
        return weeksOfMonth;

    }

    /**
     * 判断某年某月的第一天为星期几
     *
     * @param year
     * @param month
     * @return
     */
    public int getWhichDayOfWeek(int year, int month) {
        return sc.getWeekdayOfMonth(year, month);

    }

    /**
     *
     * @param year
     * @param month
     */
    public int getLastDayOfWeek(int year, int month) {
        return sc.getWeekDayOfLastMonth(year, month,
                sc.getDaysOfMonth(isLeapyear, month));
    }

    public void getCalendar(int year, int month) {
        isLeapyear = sc.isLeapYear(year); // 是否为闰年
        daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
        dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
    }

    public int getWeeksOfMonth() {
        // getCalendar(year, month);
        int preMonthRelax = 0;
        if (dayOfWeek != 7) {
            preMonthRelax = dayOfWeek;
        }
        if ((daysOfMonth + preMonthRelax) % 7 == 0) {
            weeksOfMonth = (daysOfMonth + preMonthRelax) / 7;
        } else {
            weeksOfMonth = (daysOfMonth + preMonthRelax) / 7 + 1;
        }
        return weeksOfMonth;
    }
    private void addGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        gridView = new GridView(mCtx);
        gridView.setNumColumns(7);
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);

        gridView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i(TAG, "day:" + dayNumbers[position]);
                selectPostion = position;
                dateAdapter.setSeclection(position);
                dateAdapter.notifyDataSetChanged();
                tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                        + dateAdapter.getCurrentMonth(selectPostion) + "月"
                        + dayNumbers[position] + "日");
                //refresh data
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                    mCurrentDate = format.parse(String.format("%04d%02d%02d000000",dateAdapter.getCurrentYear(selectPostion),dateAdapter.getCurrentMonth(selectPostion),Integer.valueOf(dayNumbers[selectPostion])));
                    showGraph();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //end refresh

            }
        });
        gridView.setLayoutParams(params);
    }
    /**
     * 重新计算当前的年月
     */
    public void getCurrent() {
        if (currentWeek > currentNum) {
            if (currentMonth + 1 <= 12) {
                currentMonth++;
            } else {
                currentMonth = 1;
                currentYear++;
            }
            currentWeek = 1;
            currentNum = getWeeksOfMonth(currentYear, currentMonth);
        } else if (currentWeek == currentNum) {
            if (getLastDayOfWeek(currentYear, currentMonth) == 6) {
            } else {
                if (currentMonth + 1 <= 12) {
                    currentMonth++;
                } else {
                    currentMonth = 1;
                    currentYear++;
                }
                currentWeek = 1;
                currentNum = getWeeksOfMonth(currentYear, currentMonth);
            }

        } else if (currentWeek < 1) {
            if (currentMonth - 1 >= 1) {
                currentMonth--;
            } else {
                currentMonth = 12;
                currentYear--;
            }
            currentNum = getWeeksOfMonth(currentYear, currentMonth);
            currentWeek = currentNum - 1;
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        int gvFlag = 0;
        if (e1.getX() - e2.getX() > 80) {
            // 向左滑
            addGridView();
            currentWeek++;
            getCurrent();
            dateAdapter = new DateAdapter(mCtx, getResources(), currentYear,
                    currentMonth, currentWeek, currentNum, selectPostion,
                    currentWeek == 1 ? true : false);
            dayNumbers = dateAdapter.getDayNumbers();
            gridView.setAdapter(dateAdapter);
            tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                    + dateAdapter.getCurrentMonth(selectPostion) + "月"
                    + dayNumbers[selectPostion] + "日");
            gvFlag++;
            flipper1.addView(gridView, gvFlag);
            dateAdapter.setSeclection(selectPostion);
            this.flipper1.setInAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_left_in));
            this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_left_out));
            this.flipper1.showNext();
            flipper1.removeViewAt(0);
            //refresh data
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                mCurrentDate = format.parse(String.format("%04d%02d%02d000000",dateAdapter.getCurrentYear(selectPostion),dateAdapter.getCurrentMonth(selectPostion),Integer.valueOf(dayNumbers[selectPostion])));
                showGraph();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //end refresh
            return true;

        } else if (e1.getX() - e2.getX() < -80) {
            addGridView();
            currentWeek--;
            getCurrent();
            dateAdapter = new DateAdapter(mCtx, getResources(), currentYear,
                    currentMonth, currentWeek, currentNum, selectPostion,
                    currentWeek == 1 ? true : false);
            dayNumbers = dateAdapter.getDayNumbers();
            gridView.setAdapter(dateAdapter);
            tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                    + dateAdapter.getCurrentMonth(selectPostion) + "月"
                    + dayNumbers[selectPostion] + "日");
            gvFlag++;
            flipper1.addView(gridView, gvFlag);
            dateAdapter.setSeclection(selectPostion);
            this.flipper1.setInAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_right_in));
            this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_right_out));
            this.flipper1.showPrevious();
            flipper1.removeViewAt(0);

            //refresh data
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                mCurrentDate = format.parse(String.format("%04d%02d%02d000000",dateAdapter.getCurrentYear(selectPostion),dateAdapter.getCurrentMonth(selectPostion),Integer.valueOf(dayNumbers[selectPostion])));
                showGraph();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //end refresh
            return true;
        }
        return false;
    }

    /**
     * called when select day/week/month
     */
    private void resetGridCalendar()
    {
        initCalendar();
        dateAdapter = new DateAdapter(mCtx, getResources(), currentYear,
                currentMonth,currentWeek, currentNum, selectPostion,
                currentWeek == 1 ? true : false);
        addGridView();
        dayNumbers = dateAdapter.getDayNumbers();
        gridView.setAdapter(dateAdapter);
        selectPostion = dateAdapter.getTodayPosition();
        gridView.setSelection(selectPostion);

        flipper1.addView(gridView, 1);
        dateAdapter.setSeclection(selectPostion);
        flipper1.setInAnimation(AnimationUtils.loadAnimation(mCtx,
                R.anim.push_right_in));
        flipper1.setOutAnimation(AnimationUtils.loadAnimation(mCtx,
                R.anim.push_right_out));
        flipper1.showNext();
        flipper1.removeViewAt(0);
    }

    private void refreshGridCalendarDaily()
    {
        String strDate = new SimpleDateFormat("yyyy-M-d").format(mCurrentDate);
        String day = strDate.split("-")[2];

        int i = 0;
        for(i=0;i<dayNumbers.length;i++)
        {
            if(dayNumbers[i].equals(day))break;
        }
        //no found, rebuild grid
        if(i == dayNumbers.length)
        {
            if(selectPostion==0) {
                selectPostion = 6;

                addGridView();
                currentWeek--;
                getCurrent();
                dateAdapter = new DateAdapter(mCtx, getResources(), currentYear,
                        currentMonth, currentWeek, currentNum, selectPostion,
                        currentWeek == 1 ? true : false);
                dayNumbers = dateAdapter.getDayNumbers();
                gridView.setAdapter(dateAdapter);
                tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                        + dateAdapter.getCurrentMonth(selectPostion) + "月"
                        + dayNumbers[selectPostion] + "日");
                flipper1.addView(gridView, 1);
                dateAdapter.setSeclection(selectPostion);
                this.flipper1.setInAnimation(AnimationUtils.loadAnimation(mCtx,
                        R.anim.push_right_in));
                this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(mCtx,
                        R.anim.push_right_out));
                this.flipper1.showPrevious();
                flipper1.removeViewAt(0);
            }

            else if(selectPostion==6) {
                selectPostion = 0;
                addGridView();
                currentWeek++;
                getCurrent();
                dateAdapter = new DateAdapter(mCtx, getResources(), currentYear,
                        currentMonth, currentWeek, currentNum, selectPostion,
                        currentWeek == 1 ? true : false);
                dayNumbers = dateAdapter.getDayNumbers();
                gridView.setAdapter(dateAdapter);
                tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                        + dateAdapter.getCurrentMonth(selectPostion) + "月"
                        + dayNumbers[selectPostion] + "日");
                flipper1.addView(gridView, 1);
                dateAdapter.setSeclection(selectPostion);
                this.flipper1.setInAnimation(AnimationUtils.loadAnimation(mCtx,
                        R.anim.push_left_in));
                this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(mCtx,
                        R.anim.push_left_out));
                this.flipper1.showNext();
                flipper1.removeViewAt(0);
            }
            return;
        }

        selectPostion = i;
        dateAdapter.setSeclection(selectPostion);
        dateAdapter.notifyDataSetChanged();
    }
    private void refreshGridCalendarWeekly(final boolean bNext)
    {
        addGridView();
        if(bNext)  currentWeek++;
        else currentWeek--;

        getCurrent();
        dateAdapter = new DateAdapter(mCtx, getResources(), currentYear,
                currentMonth, currentWeek, currentNum, selectPostion,
                currentWeek == 1 ? true : false);
        dayNumbers = dateAdapter.getDayNumbers();
        gridView.setAdapter(dateAdapter);
        tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                + dateAdapter.getCurrentMonth(selectPostion) + "月"
                + dayNumbers[selectPostion] + "日");

        flipper1.addView(gridView, 1);
        dateAdapter.setSeclection(selectPostion);
        if(bNext) {
            this.flipper1.setInAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_left_in));
            this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_left_out));
            this.flipper1.showNext();
        }
        else {
            this.flipper1.setInAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_right_in));
            this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_right_out));

            this.flipper1.showPrevious();
        }

        flipper1.removeViewAt(0);
        //refresh data
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            mCurrentDate = format.parse(String.format("%04d%02d%02d000000",dateAdapter.getCurrentYear(selectPostion),dateAdapter.getCurrentMonth(selectPostion),Integer.valueOf(dayNumbers[selectPostion])));
            showGraph();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //end refresh
    }
    private void refreshGridCalendarMonthly(final boolean bNext)
    {
        addGridView();
        if(bNext)  currentWeek +=5;
        else currentWeek-=5;

        getCurrent();
        dateAdapter = new DateAdapter(mCtx, getResources(), currentYear,
                currentMonth, currentWeek, currentNum, selectPostion,
                currentWeek == 1 ? true : false);
        dayNumbers = dateAdapter.getDayNumbers();
        gridView.setAdapter(dateAdapter);
        tvDate.setText(dateAdapter.getCurrentYear(selectPostion) + "年"
                + dateAdapter.getCurrentMonth(selectPostion) + "月"
                + dayNumbers[selectPostion] + "日");

        flipper1.addView(gridView, 1);
        dateAdapter.setSeclection(selectPostion);

        if(bNext) {
            this.flipper1.setInAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_left_in));
            this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_left_out));
            this.flipper1.showNext();
        }
        else {
            this.flipper1.setInAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_right_in));
            this.flipper1.setOutAnimation(AnimationUtils.loadAnimation(mCtx,
                    R.anim.push_right_out));

            this.flipper1.showPrevious();
        }

        flipper1.removeViewAt(0);
        //refresh data
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            mCurrentDate = format.parse(String.format("%04d%02d%02d000000",dateAdapter.getCurrentYear(selectPostion),dateAdapter.getCurrentMonth(selectPostion),Integer.valueOf(dayNumbers[selectPostion])));
            showGraph();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //end refresh

    }
    /**
     * get last week days, it is a duration value
     * @param from :  start which Date, the last day of this week
     *
     */
    List<Date> getLastweekDays(Date from)
    {
        List<Date> days = new ArrayList<Date>();
        //set theDay from 00:00:00
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(from);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date theday = calBeginning.getTime();
        long total = 7;
        long start = theday.getTime();
        long end = start - (total-1)*24*60*60*1000;
        for(long l = start;l>=end;l-=24*60*60*1000) days.add(new Date(l));

        return days;
    }

    /**
     * @return one week 7 days from the Calendar
     */
    List<Date> getCurrentweekDays()
    {
        List<Date> days = new ArrayList<Date>();
        for(int i =0;i<dayNumbers.length;i++)
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                Date date = format.parse(String.format("%04d%02d%02d000000",dateAdapter.getCurrentYear(i),dateAdapter.getCurrentMonth(i),Integer.valueOf(dayNumbers[i])));
                days.add(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return days;
    }

    /**
     * get last Month days, 28/29/30/31 days, it is a duration value
     * @param from : start which Date, the last day of this duration month
     *
     */
    List<Date> getLastmonthDays(Date from)
    {
        List<Date> days = new ArrayList<Date>();
        //set theDay from 00:00:00
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(from);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date theday = calBeginning.getTime();
        SpecialCalendar sc = new SpecialCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        String strDate = sdf.format(from);
        int year = Integer.parseInt(strDate.split("-")[0]);
        int month = Integer.parseInt(strDate.split("-")[1]);
        int day = Integer.parseInt(strDate.split("-")[2]);

        long daysofmonth = sc.getDaysOfMonth(sc.isLeapYear(year),month);
        long lastDaysOfMonth = sc.getDaysOfMonth(sc.isLeapYear(year), month - 1);
        //total value is 28/29/30/31
        long total = (daysofmonth == day)?daysofmonth:lastDaysOfMonth;
        long start = theday.getTime();
        long end = start - (total-1)*24*60*60*1000;
        for(long l = start;l>=end;l-=24*60*60*1000) days.add(new Date(l));
        return days;
    }

    /**
     *
     * @return current Month days from calendar:[1~31],[1~30],[1~29],[1~28]
     */
    List<Date> getCurrentmonthDays() {
        List<Date> days = new ArrayList<Date>();
        int year = dateAdapter.getCurrentYear(selectPostion);
        int month = dateAdapter.getCurrentMonth(selectPostion);
        SpecialCalendar sc = new SpecialCalendar();
        int dayNumber = sc.getDaysOfMonth(sc.isLeapYear(year),month);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            for(int i=1;i<=dayNumber;i++) {
                Date date = format.parse(String.format("%04d%02d%02d000000", year, month, i));
                days.add(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }
    /**
     * return one day's History
     * @param from
     * @return
     */
    List<IDailyHistory> getDailyHistory(Date from)
    {
        List<Long> days = new ArrayList<Long>();
        //set theDay from 00:00:00
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(from);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date theday = calBeginning.getTime();
        days.add(theday.getTime());
        try {
            return DatabaseHelper.getInstance(mCtx).getDailyHistoryDao().queryBuilder().orderBy("created", false).where().in("created",days).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<IDailyHistory>();
    }

    /**
     * return last week History
     * @param from
     * @return
     */
    List<IDailyHistory> getLastweekHistory(Date from)
    {
        List<Long> days = new ArrayList<Long>();
        List<Date> datedays = getLastweekDays(from);
        for(Date date:datedays)days.add(date.getTime());
        try {
            return DatabaseHelper.getInstance(mCtx).getDailyHistoryDao().queryBuilder().orderBy("created", true).where().in("created",days).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<IDailyHistory>();
    }

    /**
     *
     * @return return current week history, total number always 7
     */
    List<IDailyHistory> getCurrentweekHistory()
    {
        List<IDailyHistory> weeklist = new ArrayList<IDailyHistory>();
        List<Date> datedays = getCurrentweekDays();

        for(Date date:datedays)
        {
            try {
                List<IDailyHistory> list = DatabaseHelper.getInstance(mCtx).getDailyHistoryDao().queryBuilder().orderBy("created", true).where().eq("created", date.getTime()).query();
                if(list.isEmpty())
                {
                    weeklist.add(new IDailyHistory(new DailyHistory(date)));
                }
                else
                {
                    weeklist.add(list.get(0));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return weeklist;
    }
    /**
     * return last month History
     * @param from
     * @return
     */
    List<IDailyHistory> getLastmonthHistory(Date from)
    {
        List<Long> days = new ArrayList<Long>();
        List<Date> datedays = getLastmonthDays(from);
        for(Date date:datedays)days.add(date.getTime());
        try {
            return DatabaseHelper.getInstance(mCtx).getDailyHistoryDao().queryBuilder().orderBy("created", true).where().in("created",days).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<IDailyHistory>();
    }

    /**
     *
     * @return current month history, follow the calendar date.
     */
    List<IDailyHistory> getCurrentmonthHistory()
    {
        List<IDailyHistory> monthlist = new ArrayList<IDailyHistory>();

        List<Date> datedays = getCurrentmonthDays();

        for(Date date:datedays) {
            try {
                List<IDailyHistory> list =  DatabaseHelper.getInstance(mCtx).getDailyHistoryDao().queryBuilder().orderBy("created", true).where().eq("created", date.getTime()).query();
                if(list.isEmpty())
                {
                    monthlist.add(new IDailyHistory(new DailyHistory(date)));
                }
                else
                {
                    monthlist.add(list.get(0));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return monthlist;
    }

    Date getNextDay(Date from)
    {
        //set theDay from 00:00:00
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(from);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date theday = calBeginning.getTime();
        return  new Date(theday.getTime()+24*60*60*1000);
    }
    Date getLastDay(Date from)
    {
        //set theDay from 00:00:00
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(from);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date theday = calBeginning.getTime();
        return  new Date(theday.getTime()-24*60*60*1000);
    }


}