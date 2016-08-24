package com.medcorp.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.adapter.AnalysisStepsChartAdapter;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.model.Steps;
import com.medcorp.util.Common;
import com.medcorp.util.Preferences;
import com.medcorp.view.graphs.StepsLineChart;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/21.
 */
public class AnalysisStepsFragment extends BaseFragment {

    @Bind(R.id.steps_fragment_average_steps_tv)
    TextView averageStepsText;
    @Bind(R.id.steps_fragment_total_steps_tv)
    TextView totalStepsText;

    @Bind(R.id.steps_fragment_title_tv)
    TextView analysisStepsText;
    @Bind(R.id.analysis_steps_fragment_content_chart_view_pager)
    ViewPager chartViewPager;

    private View thisWeekView;
    private View lastWeekView;
    private View lastMonthView;
    private List<View> stepsDataList;
    private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View stepsView = inflater.inflate(R.layout.analysis_fragment_child_steps_fragment, container, false);
        ButterKnife.bind(this, stepsView);

        String selectDate = Preferences.getSelectDate(this.getContext());
        if (selectDate == null) {
            userSelectDate = new Date();
        } else {
            try {
                userSelectDate = new SimpleDateFormat("yyyy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        initView(inflater);
        initData(userSelectDate);
        return stepsView;
    }

    private void initData(Date userSelectDate) {
        // TODO  get right data from the database
        StepsLineChart thisWeekChart = (StepsLineChart) thisWeekView.findViewById(R.id.this_week_steps_fragment_chart);
        StepsLineChart lastWeekChart = (StepsLineChart) lastWeekView.findViewById(R.id.last_week_steps_fragment_chart);
        StepsLineChart lastMonthChart = (StepsLineChart) lastMonthView.findViewById(R.id.last_month_steps_fragment_chart);
        // database get this week
        thisWeekChart.addData(generateTestData(3000,10000,0,7),700);

        // database get last week
        lastWeekChart.addData(generateTestData(3000,10000,7,7),700);

        // database get this month
        lastMonthChart.addData(generateTestData(3000,10000,0,30),700);
    }

    private List<Steps> generateTestData(int minSteps, int maxSteps, int daysOffSetFromToday, int amountOfDays){
        List<Steps> stepsList = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < amountOfDays; i++){
            DateTime time = new DateTime(new Date());
            time = time.plusDays(daysOffSetFromToday+i);
            Steps steps = new Steps(Common.removeTimeFromDate(time.toDate()).getTime());
            steps.setDate(Common.removeTimeFromDate(time.toDate()).getTime());
            steps.setSteps(minSteps+r.nextInt(maxSteps));
            stepsList.add(steps);
        }
        return stepsList;
    }

    private void initView(LayoutInflater inflater) {
        stepsDataList = new ArrayList<>(3);
        thisWeekView = inflater.inflate(R.layout.this_week_chart_fragment_layout,null);
        lastWeekView = inflater.inflate(R.layout.last_week_chart_fragment_layout,null);
        lastMonthView = inflater.inflate(R.layout.last_month_chart_fragment_layout,null);
        stepsDataList.add(thisWeekView);
        stepsDataList.add(lastWeekView);
        stepsDataList.add(lastMonthView);

        AnalysisStepsChartAdapter adapter = new AnalysisStepsChartAdapter(stepsDataList);
        chartViewPager.setAdapter(adapter);
        chartViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        analysisStepsText.setText(R.string.analysis_fragment_this_week_steps);
                        // TODO - don't forget to add the average, total, calories & more
                        break;
                    case 1:
                        analysisStepsText.setText(R.string.analysis_fragment_last_week_steps);
                        // TODO - don't forget to add the average, total, calories & more
                        break;
                    case 2:
                        analysisStepsText.setText(R.string.analysis_fragment_last_month_solar);
                        // TODO - don't forget to add the average, total, calories & more
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
