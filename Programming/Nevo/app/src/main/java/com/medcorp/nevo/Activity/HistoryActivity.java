package com.medcorp.nevo.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
import com.medcorp.nevo.R;
import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.SleepData;
import com.medcorp.nevo.util.SleepDataHandler;
import com.medcorp.nevo.util.SleepSorter;

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
        findViewById(R.id.backImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        SimpleDateFormat sdf = new SimpleDateFormat("d'/'M");
        List<String> xVals = new ArrayList<String>();
        List<BarEntry> yValue = new ArrayList<BarEntry>();
        SleepDatabaseHelper helper = new SleepDatabaseHelper(this);
        List<Sleep> sleepList = helper.getAll();
        SleepSorter sorter = new SleepSorter();
        Collections.sort(sleepList,sorter);
        SleepDataHandler handler = new SleepDataHandler(sleepList);
        int i = 0;
        sleepDataList = handler.getSleepData();
        for (SleepData sleepData:sleepDataList) {
            yValue.add(new BarEntry(new float[]{sleepData.getLightSleep(), sleepData.getDeepSleep()}, i));
            xVals.add(sdf.format(new Date(sleepData.getDate())));
            i++;
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
        deepSleep.setText(getHours(data.getDeepSleep()) + " " + hoursAnd + " " + getLeftoverMinutes(data.getDeepSleep()) + " " + minutes);
        lightSleep.setText(getHours(data.getLightSleep() + data.getAwake() )+ " " + hoursAnd+ " " + getLeftoverMinutes(data.getLightSleep() + data.getAwake())+ " " + minutes );
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
}
