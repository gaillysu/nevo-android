package com.medcorp.view.graphs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.medcorp.R;
import com.medcorp.model.Goal;
import com.medcorp.model.Steps;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by Karl on 8/24/16.
 */

public class AnalysisStepsLineChart extends LineChart {

    private List<Steps> stepsList;
    private Goal goal;
    private int maxDays;

    public AnalysisStepsLineChart(Context context) {
        super(context);
        initGraph();

    }

    public AnalysisStepsLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGraph();
    }

    public AnalysisStepsLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initGraph();
    }

    private void initGraph() {
        setContentDescription("");
        setDescription("");
        setNoDataTextDescription("");
        setNoDataText("");
        setDragEnabled(false);
        setScaleEnabled(false);
        setPinchZoom(false);
        setClickable(false);
        setHighlightPerTapEnabled(false);
        setHighlightPerDragEnabled(false);
        dispatchSetSelected(false);
        getLegend().setEnabled(false);


        YAxis leftAxis = getAxisLeft();
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawLabels(true);
        leftAxis.setAxisMinValue(0.0f);

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

    public void addData(List<Steps> stepsList, Goal goal, int maxDays) {
        this.stepsList = stepsList;
        this.goal = goal;
        this.maxDays = maxDays;
        List<Entry> yValue = new ArrayList<Entry>();
        int maxValue = 0;

        final int stepsModulo = 500;
        for (int i = 0; i < stepsList.size(); i++) {
            if (i < stepsList.size()) {
                Steps steps = stepsList.get(i);
                if (steps.getSteps() > maxValue) {
                    maxValue = steps.getSteps();
                }
                yValue.add(new Entry(i, steps.getSteps()));
            } else {
                yValue.add(new Entry(i, 0));
            }
        }

        Log.w("Karl", "Max vlaue = " + maxValue);
        boolean putTop = false;
        if (maxValue == 0 || maxValue < goal.getSteps()) {
            maxValue = goal.getSteps() + stepsModulo;
        } else {
            putTop = true;
            maxValue = maxValue + abs(stepsModulo - (maxValue % stepsModulo));
        }

        LimitLine limitLine = new LimitLine(goal.getSteps(), "Goal: " + goal.getSteps());
        limitLine.setLineWidth(1.5f);
        limitLine.setLineColor(Color.BLACK);
        limitLine.setTextSize(18f);
        limitLine.setTextColor(Color.BLACK);

        if (putTop) {
            limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        } else {
            limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        }
        LineDataSet set = new LineDataSet(yValue, "");
        set.setColor(Color.BLACK);
        set.setCircleColor(R.color.transparent);
        set.setLineWidth(1.5f);

        set.setDrawCircles(false);
        set.setFillAlpha(128);
        set.setDrawFilled(true);
        set.setDrawValues(false);
        set.setCircleColorHole(Color.BLACK);
        set.setFillColor(getResources().getColor(R.color.colorPrimaryDark));

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.chart_gradient);
        set.setFillDrawable(drawable);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        if (stepsList.size() > 7) {
            getXAxis().setLabelCount(stepsList.size());
        }
        getXAxis().setValueFormatter(new XValueFormatter());
        dataSets.add(set);

        YAxis leftAxis = getAxisLeft();
        leftAxis.setValueFormatter(new YValueFormatter());
        leftAxis.addLimitLine(limitLine);
        leftAxis.setAxisMaxValue(maxValue * 1.0f);
        LineData data = new LineData(dataSets);
        setData(data);

        animateY(2, Easing.EasingOption.EaseInCirc);
        invalidate();
        setOnClickListener(null);
    }


    private class XValueFormatter implements AxisValueFormatter {

        private int count = 0;

        private XValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            if (stepsList.isEmpty()) {
                if (count >= maxDays) {
                    count = 0;
                }
                DateTime time = new DateTime().plusDays(count);
                count += 1;
                return time.toString("dd/MM");
            }

            if (stepsList.size() < 11) {
                if (count >= stepsList.size()) {
                    count = 0;
                }
                Steps steps = stepsList.get(count);
                count++;
                DateTime time = new DateTime(steps.getDate());
                return time.toString("dd/MM");
            } else {
                if (count == 0 || count % 5 == 0 || count == (stepsList.size() - 1)) {
                    if (count >= stepsList.size()) {
                        count = 0;
                    }
                    Steps steps = stepsList.get(count);
                    count++;

                    DateTime time = new DateTime(steps.getDate());
                    return time.toString("dd/MM");
                } else {
                    if (count >= stepsList.size()) {
                        count = 0;
                    }
                    count++;
                    return "";
                }
            }
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

    private class YValueFormatter implements AxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return String.valueOf(Math.round(value));
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

}
