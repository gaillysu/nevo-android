package com.medcorp.nevo.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.medcorp.nevo.fragment.listener.OnStateListener;
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

    @Bind(R.id.fragment_steps_history_steps)
    TextView steps;

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
                "font/Roboto-Bold.ttf");
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
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTypeface(tf);

        SimpleDateFormat sdf = new SimpleDateFormat("d'/'M");
        List<String> xVals = new ArrayList<String>();
        List<BarEntry> yValue = new ArrayList<BarEntry>();

        stepsList = getModel().getAllSteps();
        Collections.sort(stepsList, new StepsSorter());

        int i = 0;
        for(Steps steps:stepsList)
        {
            yValue.add(new BarEntry(new float[]{steps.getSteps()}, i));
            xVals.add(sdf.format(new Date(steps.getDate())));
            i++;
        }
        if (stepsList.size() < 7) {
            barChart.setScaleMinima((.14f), 1f);
        }else{
            barChart.setScaleMinima((stepsList.size()/6f),1f);
        }

        dataSet = new BarDataSet(yValue, "");
        dataSet.setDrawValues(false);
        dataSet.setColors(new int[]{getResources().getColor(R.color.customGray)});
        dataSet.setHighlightEnabled(true);
        dataSet.setHighLightColor(getResources().getColor(R.color.customOrange));
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
        steps.setText(dashboard.formatSteps(dashboard.steps));
    }

    private class Dashboard{
        int steps;

        Dashboard(int steps,int distance,int calories,int walkSteps,int walkDistance,int walkDuration,int runSteps,int runDistance,int runDuration)
        {
            this.steps = steps;
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
