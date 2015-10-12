package com.medcorp.nevo;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.medcorp.nevo.History.database.DatabaseHelper;
import com.medcorp.nevo.History.database.IDailyHistory;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by gaillysu on 15/10/7.
 */
public class HistoryActivity extends Activity implements OnChartValueSelectedListener {

    private BarChart barChart;
    private BarDataSet dataSet;
    private TextView totalSleep;
    private TextView lightSleep;
    private TextView deepSleep;
    private TextView totalSleepTitle;
    private TextView lightSleepTitle;
    private TextView deepSleepTitle;
    private List<SleepData> sleepDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sleepDataList = new ArrayList<SleepData>();
        setContentView(R.layout.layout_history_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Typeface tf = Typeface.createFromAsset(getAssets(),
                "font/Raleway-Light.ttf");
        totalSleep = (TextView) findViewById(R.id.total_sleep_textview);
        lightSleep= (TextView) findViewById(R.id.light_sleep_textview);
        deepSleep= (TextView) findViewById(R.id.deep_sleep_textview);
        totalSleepTitle = (TextView)findViewById(R.id.total_title);
        lightSleepTitle = (TextView)findViewById(R.id.light_title);
        deepSleepTitle = (TextView)findViewById(R.id.deep_title);



        FontManager.changeBoldFonts(new View[]{totalSleep, lightSleep, deepSleep}, this);
        FontManager.changeFonts(new View[]{totalSleepTitle, deepSleepTitle, lightSleepTitle}, this);
                ((ImageView) findViewById(R.id.backImage)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HistoryActivity.this.finish();
                    }
                });

        barChart = (BarChart)findViewById(R.id.history_bar);
        barChart.setDescription("");
        barChart.setNoDataTextDescription("");
        barChart.getLegend().setEnabled(false);
        barChart.setOnChartValueSelectedListener(this);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setScaleEnabled(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setViewPortOffsets(0.0f, 0.0f, 0.0f, 0.0f);
        barChart.setDragEnabled(true);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setEnabled(false);
        yAxis.setSpaceTop(60f);
        yAxis.setGridColor(getResources().getColor(R.color.transparent));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(8f);
        xAxis.setGridColor(getResources().getColor(R.color.transparent));
        xAxis.setTypeface(tf);

        List<IDailyHistory> history = new ArrayList<IDailyHistory>();
        try {
            history  = DatabaseHelper.getInstance(this).getDailyHistoryDao().queryBuilder().orderBy("created", false).query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> xVals = new ArrayList<String>();
        List<BarEntry> yValue = new ArrayList<BarEntry>();
        Collections.reverse(history);
        int i = 0;
        for (IDailyHistory daily: history) {
            Date historyDate = new Date(daily.getCreated()); // getCreated() return millsecond from 1970.1.1 00:00:00
            SimpleDateFormat sdf = new SimpleDateFormat("d'/'M");
            JSONObject sleepAnalysisResult = DatabaseHelper.getInstance(this).getSleepZone(historyDate);

            try {
                long startsleep = sleepAnalysisResult.getLong("startDateTime");
                long endsleep = sleepAnalysisResult.getLong("endDateTime");
                if(startsleep == 0 || endsleep ==0 || startsleep==endsleep) {
                    continue;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            try {
                int [] wakeTimes = DatabaseHelper.string2IntArray(sleepAnalysisResult.getString("mergeHourlyWakeTime"));
                int [] lightSleepTimes = DatabaseHelper.string2IntArray(sleepAnalysisResult.getString("mergeHourlyLightTime"));
                int [] deepSleepTimes = DatabaseHelper.string2IntArray(sleepAnalysisResult.getString("mergeHourlyDeepTime"));

                if(wakeTimes.length > 0 || lightSleepTimes.length > 0 || deepSleepTimes.length > 0) {

                    int awake = 0;
                    for (int k = 0; k < wakeTimes.length; k++) {
                        awake += wakeTimes[k];
                    }
                    int lightSleep = 0;
                    for (int k = 0; k < lightSleepTimes.length; k++) {
                        lightSleep += lightSleepTimes[k];
                    }
                    int deepSleep = 0;
                    for (int k = 0; k < deepSleepTimes.length; k++) {
                        deepSleep += deepSleepTimes[k];
                    }
                    SleepData sleepData = new SleepData(awake+lightSleep+deepSleep, deepSleep, lightSleep, awake);
                    sleepDataList.add(sleepData);
                    yValue.add(new BarEntry(new float[]{sleepData.getLightSleep(), sleepData.getDeepSleep()}, i));
                    xVals.add(sdf.format(historyDate));
                    i++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(sleepDataList.isEmpty()){
            totalSleep.setText(getString(R.string.sleep_no_data));
            lightSleep.setText(getString(R.string.sleep_no_data));
            deepSleep.setText(getString(R.string.sleep_no_data));
        }

        if (sleepDataList.size() < 7) {
            barChart.setScaleMinima((.14f), 1f);
        }else{
            barChart.setScaleMinima((sleepDataList.size()/6f),1f);
        }

        dataSet = new BarDataSet(yValue, "");
        dataSet.setDrawValues(false);
        dataSet.setColors(new int[]{getResources().getColor(R.color.light_sleep), getResources().getColor(R.color.deep_sleep)});
//        dataSet.setHighLightColor(R.color.white);
        List<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(dataSet);
        BarData data = new BarData(xVals, dataSets);
        barChart.setData(data);
        barChart.animateY(2000, Easing.EasingOption.EaseInOutCirc);
        barChart.postOnAnimation(new Runnable() {
            @Override
            public void run() {
                barChart.moveViewToX(sleepDataList.size());
            }
        });
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        barChart.highlightValue(e.getXIndex(), dataSetIndex);
        SleepData data = sleepDataList.get(e.getXIndex());
        String hoursAnd = getString(R.string.hours_and);
        String minutes= getString(R.string.minutes);
        totalSleep.setText(getHours(data.getTotalSleep())+ " " + hoursAnd + " "+ getLeftoverMinutes(data.getTotalSleep())+ " " + minutes );
        deepSleep.setText(getHours(data.getDeepSleep())+ " " + hoursAnd+ " " + getLeftoverMinutes(data.getDeepSleep())+ " " + minutes );
        lightSleep.setText(getHours(data.getLightSleep())+ " " + hoursAnd+ " " + getLeftoverMinutes(data.getLightSleep())+ " " + minutes );
    }

    @Override
    public void onNothingSelected() {
    }

    private int getHours(int i){
        double hours = i/60;
        return (int) hours;
    }

    private int getLeftoverMinutes(int i){
        return i%60;
    }


    private class SleepData {

        private int deepSleep;
        private int totalSleep;
        private int lightSleep;
        private int awake;

        public SleepData(int totalSleep, int deepSleep, int lightSleep, int awake) {
            this.deepSleep = deepSleep;
            this.totalSleep = totalSleep;
            this.lightSleep = lightSleep;
            this.awake = awake;
        }

        public int getLightSleep() {
            return lightSleep+awake;
        }

        public int getDeepSleep() {
            return deepSleep;
        }

        public int getTotalSleep() {
            return totalSleep;
        }
    }


}
