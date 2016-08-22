package com.medcorp.view.graphs;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

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
import org.joda.time.DateTime;
import com.medcorp.R;
import com.medcorp.model.SleepData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by karl-john on 19/8/2016.
 */

public class SleepChart extends LineChart {


    public SleepChart(Context context) {
        super(context);
        initGraph();
    }

    public SleepChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraph();
    }

    public SleepChart(Context context, AttributeSet attrs, int defStyle) {
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
        leftAxis.setAxisMinValue(0.0f);
        leftAxis.setAxisMaxValue(11.0f);
        leftAxis.setValueFormatter(new YAxisValueFormatter());
        leftAxis.setLabelCount(3);

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setAxisLineColor(Color.BLACK);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLimitLinesBehindData(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setAxisMaxValue(10.0f);

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
                    if (i >= 5){
                        intList.add(5);
                    }else{
                        intList.add(i+1);
                    }
                }
            }
            if (deepSleepMinutes> 0){
                int consecutiveFiveMinutes = deepSleepMinutes/interval;
                for (int i = 0; i <consecutiveFiveMinutes; i++){
                    if (i >= 5){
                        intList.add(10);
                    }else{
                        intList.add(i+5);
                    }
                }
            }
        }
        for (int i = 0; i < intList.size(); i++) {
            yValue.add(new Entry(i, intList.get(i)));
        }
        LineDataSet set = new LineDataSet(yValue, "");
        set.setColor(Color.BLACK);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(1.5f);
        set.setDrawCircles(false);
        set.setDrawCircleHole(true);
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
            }else if (value > 0.0 && value <= 5.0){
                return getContext().getResources().getString(R.string.sleep_light_sleep);
            }else if (value >= 5.0){
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
            return String.valueOf(newDate.getHourOfDay()+":00");
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }
}
