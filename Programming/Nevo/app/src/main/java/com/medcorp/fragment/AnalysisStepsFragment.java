package com.medcorp.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.adapter.AnalysisStepsChartAdapter;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.model.Goal;
import com.medcorp.model.Steps;
import com.medcorp.util.Preferences;
import com.medcorp.view.graphs.AnalysisStepsLineChart;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @Bind(R.id.steps_fragment_average_calories_tv)
    TextView avgCalories;
    @Bind(R.id.steps_fragment_average_time_tv)
    TextView avgDurationTime;

    @Bind(R.id.steps_fragment_title_tv)
    TextView analysisStepsText;
    @Bind(R.id.analysis_steps_fragment_content_chart_view_pager)
    ViewPager chartViewPager;

    private View thisWeekView;
    private View lastWeekView;
    private View lastMonthView;
    private List<Steps> thisWeekData;
    private List<Steps> lastWeekData;
    private List<Steps> lastMonthData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View stepsView = inflater.inflate(R.layout.analysis_fragment_child_steps_fragment, container, false);
        ButterKnife.bind(this, stepsView);

        String selectDate = Preferences.getSelectDate(this.getContext());
        Date userSelectDate = new Date();
        if (selectDate != null) {
            try {
                userSelectDate = new SimpleDateFormat("yy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        initView(inflater);
        initData(userSelectDate);
        return stepsView;
    }

    private void initData(Date userSelectDate) {
        AnalysisStepsLineChart thisWeekChart = (AnalysisStepsLineChart) thisWeekView.findViewById(R.id.analysis_step_chart);
        AnalysisStepsLineChart lastWeekChart = (AnalysisStepsLineChart) lastWeekView.findViewById(R.id.analysis_step_chart);
        AnalysisStepsLineChart lastMonthChart = (AnalysisStepsLineChart) lastMonthView.findViewById(R.id.analysis_step_chart);

        /*
         * Added max in 'addData', max is the timespam in days, in 'this week' and 'last week' this is 7 because 7 days is equal to a week.
         * In this month this is 30 (or 31) because there are 30 days in a month.
        */
        Goal activeGoal = null;
        for (Goal goal : getModel().getAllGoal()) {
            if (goal.isStatus()) {
                activeGoal = goal;
                break;
            }
        }
        if (activeGoal == null) {
            activeGoal = new Goal("Unknown", true, 1000);
            // 因该 没有这个 SITUATION
            // TODO next version, per day goal line.
        }
        thisWeekData = getModel().getThisWeekSteps(getModel().getNevoUser().getNevoUserID(), userSelectDate);
        lastWeekData = getModel().getLastWeekSteps(getModel().getNevoUser().getNevoUserID(), userSelectDate);
        lastMonthData = getModel().getLastMonthSteps(getModel().getNevoUser().getNevoUserID(), userSelectDate);
        thisWeekChart.addData(thisWeekData, activeGoal, 7);
        lastWeekChart.addData(lastWeekData, activeGoal, 7);
        lastMonthChart.addData(lastMonthData, activeGoal, 7);
    }

    private void initView(LayoutInflater inflater) {
        List<View> stepsDataList = new ArrayList<>(3);
        thisWeekView = inflater.inflate(R.layout.analysis_steps_chart_fragment_layout, null);
        lastWeekView = inflater.inflate(R.layout.analysis_steps_chart_fragment_layout, null);
        lastMonthView = inflater.inflate(R.layout.analysis_steps_chart_fragment_layout, null);
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
                switch (position) {
                    case 0:
                        if (thisWeekData.size() != 0) {
                            setAverageText(getWeekSteps(thisWeekData), getWeekSteps(thisWeekData) / thisWeekData.size()
                                    , getWeekCalories(thisWeekData) / thisWeekData.size()
                                    , getAvgDurationTime(thisWeekData) / thisWeekData.size()
                                    , getResources().getString(R.string.analysis_fragment_this_week_steps));
                        } else {
                            setAverageText(0, 0, 0, 0, getResources().getString(R.string.analysis_fragment_this_week_steps));
                        }
                        break;
                    case 1:
                        if (lastWeekData.size() != 0) {
                            setAverageText(getWeekSteps(lastWeekData), getWeekSteps(lastWeekData) / lastWeekData.size()
                                    , getWeekCalories(lastWeekData) / lastWeekData.size()
                                    , getAvgDurationTime(lastWeekData) / lastWeekData.size()
                                    , getResources().getString(R.string.analysis_fragment_last_week_steps));
                        } else {
                            setAverageText(0, 0, 0, 0, getResources().getString(R.string.analysis_fragment_last_week_steps));
                        }
                        break;
                    case 2:
                        if (lastMonthData.size() != 0) {
                            setAverageText(getWeekSteps(lastMonthData), getWeekSteps(lastMonthData) / lastMonthData.size()
                                    , getWeekCalories(lastMonthData) / lastMonthData.size()
                                    , getAvgDurationTime(lastMonthData) / lastMonthData.size()
                                    , getResources().getString(R.string.analysis_fragment_last_month_solar));
                        } else {
                            setAverageText(0, 0, 0, 0, getResources().getString(R.string.analysis_fragment_last_month_solar));
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setAverageText(int totalSteps, int averageSteps, int averageCalories, int averageDuration, String title) {
        analysisStepsText.setText(title);
        totalStepsText.setText(totalSteps + "");
        averageStepsText.setText(averageSteps + "");
        avgCalories.setText(averageCalories + "");
        avgDurationTime.setText(averageDuration + "");

    }

    private int getAvgDurationTime(List<Steps> thisWeekData) {
        int durationTime = 0;
        for (Steps steps : thisWeekData) {
            durationTime += steps.getRunDuration();
        }
        return durationTime;
    }

    private int getWeekCalories(List<Steps> thisWeekData) {
        int avgCaloriesNum = 0;
        for (Steps steps : thisWeekData) {
            avgCaloriesNum += steps.getCalories();
        }
        return avgCaloriesNum;
    }

    private int getWeekSteps(List<Steps> thisWeekData) {
        int totalSteps = 0;
        for (Steps steps : thisWeekData) {
            totalSteps += steps.getSteps();
        }
        return totalSteps;
    }
}
