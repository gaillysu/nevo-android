package com.medcorp.nevo.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.medcorp.nevo.fragment.base.BaseFragment;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.SleepData;
import com.medcorp.nevo.util.SleepDataHandler;
import com.medcorp.nevo.util.SleepSorter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Karl on 12/10/15.
 */

public class SleepHistoryFragment extends BaseFragment implements OnChartValueSelectedListener {

    @Bind(R.id.fragment_sleep_history_bar)
    BarChart barChart;


    @Bind(R.id.fragment_sleep_history_sleep_duration)
    TextView sleepDuration;

    @Bind(R.id.fragment_sleep_history_deep)
    TextView sleepDeepDuration;

    @Bind(R.id.fragment_sleep_history_light)
    TextView sleepLightDuration;

    @Bind(R.id.fragment_sleep_history_start)
    TextView sleepStart;

    @Bind(R.id.fragment_sleep_history_wake)
    TextView sleepEnd;

    @Bind(R.id.fragment_sleep_history_wake_duration)
    TextView sleepWakeDuration;


    private BarDataSet dataSet;
    private List<SleepData> sleepDataList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_history, container, false);
        ButterKnife.bind(this, view);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                getString(R.string.font_roboto_bold_path));
        barChart.setDescription("");
        barChart.setNoDataTextDescription("");
        barChart.getLegend().setEnabled(false);
        barChart.setOnChartValueSelectedListener(this);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.setScaleEnabled(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setViewPortOffsets(0.0f, 0.0f, 0.0f, 80.0f);
        barChart.setDragEnabled(true);
        barChart.setDrawHighlightArrow(true);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setEnabled(false);
        yAxis = barChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        yAxis.setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTypeface(tf);
        SimpleDateFormat sdf = new SimpleDateFormat("d'/'M");
        List<String> xVals = new ArrayList<String>();
        List<BarEntry> yValue = new ArrayList<BarEntry>();

        SleepDatabaseHelper helper = new SleepDatabaseHelper(getContext());
        List<Sleep> sleepList = helper.convertToNormalList(helper.getAll(getModel().getNevoUser().getNevoUserID()));
        SleepSorter sorter = new SleepSorter();
        Collections.sort(sleepList, sorter);
        SleepDataHandler handler = new SleepDataHandler(sleepList);
        int i = 0;
        sleepDataList = handler.getSleepData();
        for (SleepData sleepData:sleepDataList) {
            yValue.add(new BarEntry(new float[]{sleepData.getDeepSleep(),sleepData.getLightSleep(),sleepData.getAwake()}, i));
            xVals.add(sdf.format(new Date(sleepData.getDate())));
            i++;
        }
        if (sleepDataList.size() < 7) {
            barChart.setScaleMinima((.14f), 1f);
        }else{
            barChart.setScaleMinima((sleepDataList.size()/6f),1f);
        }

        dataSet = new BarDataSet(yValue, "");
        dataSet.setDrawValues(false);
        dataSet.setColors(new int[]{getResources().getColor(R.color.deep_sleep), getResources().getColor(R.color.light_sleep), getResources().getColor(R.color.wake_sleep)});
        dataSet.setHighlightEnabled(true);
        dataSet.setHighLightColor(getResources().getColor(R.color.white));
        dataSet.setHighLightAlpha(100);

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
        if(sleepDataList.size()>0)
        {
            barChart.highlightValue(sleepDataList.size() - 1, 0);
            SleepData sleepData = sleepDataList.get(sleepDataList.size() - 1);
            setDashboard(new Dashboard(sleepData.getTotalSleep(), sleepData.getDeepSleep(), sleepData.getLightSleep(), sleepData.getSleepStart(), sleepData.getSleepEnd(), sleepData.getAwake()));
        }
        return view;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        barChart.highlightValue(e.getXIndex(), dataSetIndex);
        SleepData sleepData = sleepDataList.get(e.getXIndex());
        setDashboard(new Dashboard(sleepData.getTotalSleep(), sleepData.getDeepSleep(), sleepData.getLightSleep(), sleepData.getSleepStart(), sleepData.getSleepEnd(), sleepData.getAwake()));
    }

    @Override
    public void onNothingSelected() {

    }

    public void setDashboard(final Dashboard dashboard)
    {
        sleepDuration.setText(dashboard.formatDuration(dashboard.sleepDuration));
        sleepDeepDuration.setText(dashboard.formatDuration(dashboard.sleepDeepDuration));
        sleepLightDuration.setText(dashboard.formatDuration(dashboard.sleepLightDuration));
        sleepStart.setText(dashboard.formatTimeStamp(dashboard.sleepStart));
        sleepEnd.setText(dashboard.formatTimeStamp(dashboard.sleepEnd));
        sleepWakeDuration.setText(dashboard.formatDuration(dashboard.sleepWakeDuration));
    }

    class Dashboard{
        int sleepDuration;
        int sleepDeepDuration;
        int sleepLightDuration;
        long sleepStart;
        long sleepEnd;
        int sleepWakeDuration;

        Dashboard(int sleepDuration,int sleepDeepDuration,int sleepLightDuration,long sleepStart,long sleepEnd,int sleepWakeDuration)
        {
            this.sleepDuration = sleepDuration;
            this.sleepDeepDuration = sleepDeepDuration;
            this.sleepLightDuration = sleepLightDuration;
            this.sleepStart = sleepStart;
            this.sleepEnd = sleepEnd;
            this.sleepWakeDuration = sleepWakeDuration;
        }
        String formatDuration(int durationMinute)
        {
            return durationMinute/60 + "h" + durationMinute%60 + "m";
        }
        String formatTimeStamp(long timeStamp)
        {
            return new SimpleDateFormat("HH:mm").format(new Date(timeStamp));
        }
    }
}