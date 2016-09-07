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
import com.medcorp.model.SleepData;
import com.medcorp.util.Preferences;
import com.medcorp.view.graphs.AnalysisSleepLineChart;

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
public class AnalysisSleepFragment extends BaseFragment {

    @Bind(R.id.steps_fragment_average_steps_tv)
    TextView averageStepsText;
    @Bind(R.id.steps_fragment_total_steps_tv)
    TextView totalStepsText;
    @Bind(R.id.steps_fragment_average_calories_tv)
    TextView averageWake;
    @Bind(R.id.steps_fragment_average_time_tv)
    TextView sleepQualityTv;

    @Bind(R.id.analysis_sleep_fragment_view_page)
    ViewPager sleepViewPage;
    @Bind(R.id.analysis_sleep_fragment_title_tv)
    TextView sleepTextView;

    @Bind(R.id.analysis_fragment_des_total_one)
    TextView totalSleepDes;
    @Bind(R.id.analysis_fragment_des_total_two)
    TextView avgSleepDes;
    @Bind(R.id.analysis_fragment_des_total_three)
    TextView avgWakeDes;
    @Bind(R.id.analysis_fragment_des_total_four)
    TextView sleepQuality;

    private List<View> sleepList;
    private Date userSelectDate;
    private View thisWeekView;
    private View lastWeekView;
    private View lastMonthView;
    private List<SleepData> thisWeekSleepData;
    private List<SleepData> lastWeekSleepData;
    private List<SleepData> lastMonthSleepData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sleepView = inflater.inflate(R.layout.analysis_fragment_child_sleep_fragment, container, false);
        ButterKnife.bind(this, sleepView);
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
        return sleepView;
    }

    private void initView(LayoutInflater inflater) {
        totalSleepDes.setText(getString(R.string.analysis_fragment_des_total_sleep));
        avgSleepDes.setText(getString(R.string.analysis_fragment_des_avg_sleep));
        avgWakeDes.setText(getString(R.string.analysis_fragment_des_avg_wake));
        sleepQuality.setText(getString(R.string.analysis_fragment_des_sleep_quality));

        sleepList = new ArrayList<>(3);

        thisWeekView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout, null);
        lastWeekView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout, null);
        lastMonthView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout, null);

        sleepList.add(thisWeekView);
        sleepList.add(lastWeekView);
        sleepList.add(lastMonthView);

        initData(userSelectDate);
    }

    private void initData(Date userSelectDate) {
        thisWeekSleepData = getModel().getThisWeekSleep(getModel().getNevoUser().getNevoUserID(), userSelectDate);
        lastWeekSleepData = getModel().getLastWeekSleep(getModel().getNevoUser().getNevoUserID(), userSelectDate);
        lastMonthSleepData = getModel().getLastMonthSleep(getModel().getNevoUser().getNevoUserID(), userSelectDate);
        setDesText(0);
        AnalysisSleepLineChart thisWeekChart = (AnalysisSleepLineChart) thisWeekView.findViewById(R.id.analysis_sleep_chart);
        AnalysisSleepLineChart lastWeekChart = (AnalysisSleepLineChart) lastWeekView.findViewById(R.id.analysis_sleep_chart);
        AnalysisSleepLineChart lastMonthChart = (AnalysisSleepLineChart) lastMonthView.findViewById(R.id.analysis_sleep_chart);
        /*
            'Sleep' is not the right way to put it into the chart because one evening and one night is spread through 2 'Sleep' Objects.
            Therefor we have a solution which is 'SleepData' We have therefor 'SleepDataHandler' to parse a List<Sleep> to List<SleepData>. Although, this needs to be tested.
            getDummyData is just for the 'dummy data'
         */
        thisWeekChart.addData(thisWeekSleepData, 7);
        lastWeekChart.addData(lastWeekSleepData, 7);
        lastMonthChart.addData(lastMonthSleepData, 30);

        AnalysisStepsChartAdapter adapter = new AnalysisStepsChartAdapter(sleepList);
        sleepViewPage.setAdapter(adapter);
        sleepViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setDesText(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setAverageText(int totalSleep, int averageSleep, int averageWakeValue, int averageQuality, String title) {
        StringBuffer buffer = new StringBuffer();
        if (totalSleep / 60 > 0) {
            buffer.append(totalSleep / 60 + "h");
        }
        if (totalSleep % 60 > 0) {
            buffer.append(totalSleep % 60 + "min");
        }
        if (buffer.length() <= 0) {
            buffer.append("0");
        }
        totalStepsText.setText(buffer.toString());
        averageStepsText.setText(averageSleep + "");
        averageWake.setText(averageWakeValue + "");
        sleepQualityTv.setText(averageQuality + "");
        sleepTextView.setText(title);
    }

    public int getTotalSleep(List<SleepData> list) {
        int sumSleep = 0;
        for (SleepData sleepData : list) {
            sumSleep += sleepData.getTotalSleep();
        }
        return sumSleep;
    }

    public int getAverageWake(List<SleepData> list) {
        int averageWake = 0;
        for (SleepData sleepData : list) {
            averageWake += sleepData.getAwake();
        }
        return averageWake / list.size();
    }

    public void setDesText(int position) {
        switch (position) {
            case 0:
                String title = getString(R.string.analysis_fragment_this_week_steps);
                if (thisWeekSleepData.size() != 0) {
                    setAverageText(getTotalSleep(thisWeekSleepData), getTotalSleep(thisWeekSleepData) / thisWeekSleepData.size()
                            , getAverageWake(thisWeekSleepData) / thisWeekSleepData.size(), 0, title);
                } else {
                    setAverageText(0, 0, 0, 0, title);
                }
                break;
            case 1:
                String lastTitle = getString(R.string.analysis_fragment_last_week_steps);
                if (lastWeekSleepData.size() != 0) {
                    setAverageText(getTotalSleep(lastWeekSleepData), getTotalSleep(lastWeekSleepData) / lastWeekSleepData.size()
                            , getAverageWake(lastWeekSleepData) / lastWeekSleepData.size(), 0, lastTitle);
                } else {
                    setAverageText(0, 0, 0, 0, lastTitle);
                }
                break;
            case 2:
                String lastMonthTitle = getString(R.string.analysis_fragment_last_month_solar);
                if (lastMonthSleepData.size() != 0) {
                    setAverageText(getTotalSleep(lastMonthSleepData), getTotalSleep(lastMonthSleepData) / lastMonthSleepData.size()
                            , getAverageWake(lastMonthSleepData) / lastMonthSleepData.size(), 0, lastMonthTitle);
                } else {
                    setAverageText(0, 0, 0, 0, lastMonthTitle);
                }
                break;
        }
    }
}
