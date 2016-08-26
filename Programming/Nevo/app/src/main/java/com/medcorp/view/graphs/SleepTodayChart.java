package com.medcorp.view.graphs;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.medcorp.R;
import com.medcorp.model.SleepData;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by karl-john on 19/8/2016.
 */

public class SleepTodayChart extends LineChart {


    public SleepTodayChart(Context context) {
        super(context);
        initGraph();
    }

    public SleepTodayChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraph();
    }

    public SleepTodayChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initGraph();
    }

    public void initGraph(){
        setContentDescription("");
        setDescription("");
        setNoDataTextDescription("");
        setNoDataText("");
        setDragEnabled(false);
        setScaleEnabled(false);
        setPinchZoom(false);
        getLegend().setEnabled(false);

        YAxis leftAxis = getAxisLeft();
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawLabels(true);
        leftAxis.setAxisMaxValue(2.5f);
        leftAxis.setValueFormatter(new YAxisValueFormatter());
        leftAxis.setLabelCount(3);

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setAxisLineColor(Color.BLACK);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLimitLinesBehindData(false);
        rightAxis.setDrawLabels(false);

        XAxis xAxis = getXAxis();
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawLimitLinesBehindData(false);
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

    }

    public void setDataInChart(SleepData sleepData){
        SimpleDateFormat sdf = new SimpleDateFormat("d'/'M", Locale.US);
        List<Entry> yValue = new ArrayList<Entry>();
        int interval = 5;
        List<Integer> intList = new ArrayList<>();
        int lightSleepContinue = -1;
        int deepSleepContinue = -1;
        int[] hourlyWakeTime = sleepData.getHourlyWakeInt();
        int[] hourlyLightSleepTime = sleepData.getHourlyLightInt();
        int[] hourlyDeepSleepTime = sleepData.getHourlyDeepInt();

        int sleptHours = Collections.max(Arrays.asList(hourlyWakeTime.length,hourlyLightSleepTime.length,hourlyDeepSleepTime.length));
        for (int hour = 0; hour < sleptHours; hour++) {
            int awakeMinutes = hourlyWakeTime[hour];
            int lightSleepMinutes = hourlyLightSleepTime[hour];
            int deepSleepMinutes = hourlyDeepSleepTime[hour];
            if (awakeMinutes == 0 && lightSleepMinutes == 0 && deepSleepMinutes == 0 ){
                continue;
            }
            if (awakeMinutes > 0){
                int consecutiveMinutes = awakeMinutes/interval;
                for (int i = 0; i <consecutiveMinutes; i++){
                    intList.add(0);
                }
            }
            if (lightSleepMinutes > 0){
                int consecutiveFiveMinutes = (int) lightSleepMinutes/interval;
                for (int i = 0; i <consecutiveFiveMinutes; i++){
                    intList.add(1);
                }
            }
            if (deepSleepMinutes> 0){
                int consecutiveFiveMinutes = deepSleepMinutes/interval;
                for (int i = 0; i <consecutiveFiveMinutes; i++){
                    intList.add(2);
                }
            }
        }

        for (int i = 0; i < intList.size(); i++) {
            Log.w("Karl","Value = " + intList.get(i));
            yValue.add(new Entry(i, intList.get(i)));
        }
        LineDataSet set = new LineDataSet(yValue, "");
        set.setColor(Color.BLACK);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(1.0f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(false);
        set.setFillAlpha(128);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawFilled(true);
        set.setDrawValues(false);
        set.setCircleColorHole(Color.BLACK);
        set.setFillColor(getResources().getColor(R.color.colorPrimaryDark));
//        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.chart_gradient);
//        set.setFillDrawable(drawable);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set);
        LineData data = new LineData(dataSets);
        setData(data);

        animateY(2, Easing.EasingOption.EaseInCirc);
        invalidate();
        getXAxis().setLabelCount(sleptHours);
        getXAxis().setValueFormatter(new XAxisValueFormatter(intList.size(), interval, new DateTime(sleepData.getSleepStart())));
    }

    public void setEmptyChart() {

    }

    private class YAxisValueFormatter implements AxisValueFormatter{

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value == 0.0){
                return getContext().getResources().getString(R.string.sleep_awake);
            }else if (value >= 0.5 && value  <= 1.5){
                return getContext().getResources().getString(R.string.sleep_light_sleep);
            }else if (value >= 1.6 && value  <= 2.6){
                return getContext().getResources().getString(R.string.sleep_deep_sleep);
            }
            return "?";
        }
        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

    private class XAxisValueFormatter implements AxisValueFormatter{

        private final int interval;
        private final int size;
        private final DateTime startDate;

        public XAxisValueFormatter(int size, int interval, DateTime startDate){
            this.size = size;
            this.interval = interval;
            this.startDate = startDate;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            DateTime newDate = startDate.plusHours((int) value/10);
            return String.valueOf(newDate.getHourOfDay());
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }
}