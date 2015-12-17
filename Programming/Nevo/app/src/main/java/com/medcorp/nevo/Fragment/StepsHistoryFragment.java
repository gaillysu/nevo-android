package com.medcorp.nevo.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.fragment.base.BaseFragment;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.util.StepsSorter;

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
public class StepsHistoryFragment extends BaseFragment implements OnChartValueSelectedListener {

    @Bind(R.id.fragment_steps_history_distance)
    TextView distance;

    @Bind(R.id.fragment_steps_history_steps)
    TextView steps;

    @Bind(R.id.fragment_steps_history_consume)
    TextView calories;

    @Bind(R.id.fragment_steps_history_walkingdistance)
    TextView walkingDistance;

    @Bind(R.id.fragment_steps_history_walkingduration)
    TextView walkingDuration;

    @Bind(R.id.fragment_steps_history_walkingcalories)
    TextView walkingCalories;

    @Bind(R.id.fragment_steps_history_runningdistance)
    TextView runningDistance;

    @Bind(R.id.fragment_steps_history_runningduration)
    TextView runningDuration;

    @Bind(R.id.fragment_steps_history_runningcalories)
    TextView runningCalories;

    @Bind(R.id.fragment_steps_history_bar)
    BarChart barChart;

    private BarDataSet dataSet;
    private List<Steps> stepsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps_history, container, false);
        ButterKnife.bind(this, view);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "font/Roboto-Light.ttf");
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

        stepsList = ((ApplicationModel)getActivity().getApplication()).getAllSteps();
        Collections.sort(stepsList, new StepsSorter());

        int i = 0;
        for(Steps steps:stepsList)
        {
            yValue.add(new BarEntry(new float[]{steps.getSteps()}, i));
            xVals.add(sdf.format(new Date(steps.getDate())));
            i++;
        }

        dataSet = new BarDataSet(yValue, "");
        dataSet.setDrawValues(false);
        dataSet.setColors(new int[]{getResources().getColor(R.color.customGray)});
        //dataSet.setBarShadowColor(getResources().getColor(R.color.customOrange));
        List<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(dataSet);
        BarData data = new BarData(xVals, dataSets);
        barChart.setData(data);
        barChart.animateY(2000, Easing.EasingOption.EaseInOutCirc);
        barChart.postOnAnimation(new Runnable() {
            @Override
            public void run() {
                barChart.moveViewToX(stepsList.size());
            }
        });

        return view;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        barChart.highlightValue(e.getXIndex(), dataSetIndex);
        Steps steps = stepsList.get(e.getXIndex());
        setDashboard(new Dashboard(steps.getSteps(),steps.getDistance(),steps.getCalories(),steps.getWalkSteps(),steps.getWalkDistance(),steps.getWalkDuration(),steps.getRunSteps(),steps.getRunDistance(),steps.getRunDuration()));
    }

    @Override
    public void onNothingSelected() {

    }

    private void setDashboard( Dashboard dashboard)
    {
        distance.setText(dashboard.formatDistance(dashboard.distance));
        steps.setText(dashboard.formatSteps(dashboard.steps));
        calories.setText(dashboard.formatConsume(dashboard.calories));
        walkingDistance.setText(dashboard.formatDistance(dashboard.walkDistance));
        walkingDuration.setText(dashboard.formatDuration(dashboard.walkDuration));
        walkingCalories.setText(dashboard.formatConsume(dashboard.calories));
        runningDistance.setText(dashboard.formatDistance(dashboard.runDistance));
        runningDuration.setText(dashboard.formatDuration(dashboard.runDuration));
        runningCalories.setText(dashboard.formatConsume(dashboard.calories));
    }

    private class Dashboard{
        int steps;
        int distance;
        int calories;
        int walkSteps;
        int walkDistance;
        int walkDuration;
        int runSteps;
        int runDistance;
        int runDuration;

        Dashboard(int steps,int distance,int calories,int walkSteps,int walkDistance,int walkDuration,int runSteps,int runDistance,int runDuration)
        {
            this.steps = steps;
            this.distance = distance;
            this.calories = calories;
            this.walkSteps = walkSteps;
            this.walkDistance = walkDistance;
            this.walkDuration = walkDuration;
            this.runSteps = runSteps;
            this.runDistance = runDistance;
            this.runDuration = runDuration;
        }
        String formatSteps(int steps)
        {
            return steps + " steps";
        }
        String formatDistance(int distanceMeter)
        {
            return distanceMeter/1000 + "KM";
        }
        String formatConsume(int calories)
        {
            return calories/1000 + "KCal";
        }
        String formatDuration(int durationMinute)
        {
            return durationMinute/60 + "h" + durationMinute%60 + "m";
        }
    }
}
