package com.medcorp.view.graphs;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.medcorp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

/**
 * Created by karl-john on 18/8/2016.
 */

public class MainStepsBarChart extends BarChart {
    private int[] steps = new int[0];
    public MainStepsBarChart(Context context) {
        super(context);
        initGraph();
    }

    public MainStepsBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraph();
    }

    public MainStepsBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initGraph();
    }

    public void initGraph(){
        final float widthLines = 1.0f;
        getLegend().setEnabled(false);
//        setOnChartValueSelectedListener(this);
        setPinchZoom(false);
        setDrawValueAboveBar(false);
        setDoubleTapToZoomEnabled(false);
        setDragEnabled(false);
        setSelected(false);
        setScaleEnabled(false);
        setDescription("");
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.window_background_color));
        setGridBackgroundColor(ContextCompat.getColor(getContext(), R.color.window_background_color));
        setBorderWidth(widthLines);
        setBorderColor(Color.BLACK);
        setDrawBorders(true);

        YAxis leftAxis = getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.BLACK);
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setAxisLineWidth(widthLines);
        leftAxis.setGridLineWidth(widthLines);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisMinValue(0.0f);
        leftAxis.setDrawLabels(true);
        leftAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                int resValue = (int) value;
                return resValue + "";
            }
        });

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(true);
        rightAxis.setDrawGridLines(false);
        rightAxis.setGridColor(Color.BLACK);
        rightAxis.setAxisLineColor(Color.BLACK);
        rightAxis.setDrawLimitLinesBehindData(true);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawLabels(false);
        rightAxis.setGridLineWidth(widthLines);

        XAxis xAxis = getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(true);
        xAxis.setGridLineWidth(widthLines);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelsToSkip(5);
        xAxis.setSpaceBetweenLabels(-10);
        xAxis.setDrawLimitLinesBehindData(true);

    }

    public void setDataInChart(int[] stepsArray) {
        if (stepsArray.length != 24){
            int[] stepsArrayCopy = stepsArray;
            stepsArray = new int[24];
            for (int i = 0; i < 24; i++){
                if (i > stepsArrayCopy.length -1 ){
                    stepsArray[i] = 0;
                }else{
                    stepsArray[i] = stepsArrayCopy[i];
                }
            }
        }
        List<String> xVals = new ArrayList<String>();
        List<BarEntry> yValue = new ArrayList<BarEntry>();
        int maxValue = 0;
        final int stepsModulo = 200;
        Random r = new Random();
        for (int i = 0; i < stepsArray.length; i++) {
            yValue.add(new BarEntry(stepsArray[i], i));
            xVals.add(i + ":00");
            if (stepsArray[i] > maxValue) {
                maxValue = stepsArray[i];
            }
        }
        int labelCount = 6;
        if (maxValue == 0) {
            maxValue = 500;
        } else {
            maxValue = maxValue + abs(stepsModulo - (maxValue % stepsModulo));
            labelCount = (maxValue/stepsModulo) +1;
        }
        getAxisLeft().setAxisMaxValue(maxValue);
        getAxisLeft().setLabelCount(labelCount,true);

        setScaleMinima((.14f), 1f);
        BarDataSet dataSet = new BarDataSet(yValue, "");
        dataSet.setDrawValues(false);
        dataSet.setColors(new int[]{ContextCompat.getColor(getContext(), R.color.colorPrimaryDark)});
        List<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(dataSet);
        BarData data = new BarData(xVals, dataSets);
        setData(data);
        invalidate();
    }

    public int getTotalSteps(){
        int totalSteps = 0;
        for (int i = 0; i < steps.length; i++){
            totalSteps += steps[i];
        }
        return totalSteps;
    }
}
