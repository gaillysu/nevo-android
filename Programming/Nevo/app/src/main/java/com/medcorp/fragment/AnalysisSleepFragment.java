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
public class AnalysisSleepFragment extends BaseFragment {

    @Bind(R.id.steps_fragment_average_steps_tv)
    TextView averageStepsText;
    @Bind(R.id.steps_fragment_total_steps_tv)
    TextView totalStepsText;
    @Bind(R.id.analysis_sleep_fragment_view_page)
    ViewPager sleepViewPage;
    @Bind(R.id.steps_fragment_title_tv)
    TextView sleepTextView;

    private List<View> sleepList;
    private Date userSelectDate;
    private View thisWeekView;
    private View lastWeekView;
    private View lastMonthView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sleepView = inflater.inflate(R.layout.analysis_fragment_child_sleep_fragment,container,false);
        ButterKnife.bind(this,sleepView);
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
        sleepList = new ArrayList<>(3);
        thisWeekView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout,null);
        lastWeekView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout,null);
        lastMonthView = inflater.inflate(R.layout.analysis_sleep_chart_fragment_layout,null);
        sleepList.add(thisWeekView);
        sleepList.add(lastWeekView);
        sleepList.add(lastMonthView);
        initData(userSelectDate);
    }

    private void initData(Date userSelectDate) {
        AnalysisSleepLineChart thisWeekChart = (AnalysisSleepLineChart) thisWeekView.findViewById(R.id.analysis_sleep_chart);
        AnalysisSleepLineChart lastWeekChart = (AnalysisSleepLineChart) lastWeekView.findViewById(R.id.analysis_sleep_chart);
        AnalysisSleepLineChart lastMonthChart = (AnalysisSleepLineChart) lastMonthView.findViewById(R.id.analysis_sleep_chart);

        //TODO Replace this data with sleep data from the database,
        /*
            'Sleep' is not the right way to put it into the chart because one evening and one night is spread through 2 'Sleep' Objects.
            Therefor we have a solution which is 'SleepData' We have therefor 'SleepDataHandler' to parse a List<Sleep> to List<SleepData>. Although, this needs to be tested.
            getDummyData is just for the 'dummy data'
         */
        thisWeekChart.addData(getDummyData(7),7);
        lastWeekChart.addData(getDummyData(7),7);
        lastMonthChart.addData(getDummyData(30),30);

        AnalysisStepsChartAdapter adapter = new AnalysisStepsChartAdapter(sleepList);
        sleepViewPage.setAdapter(adapter);
        sleepViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        sleepTextView.setText(R.string.analysis_fragment_this_week_steps);
                        break;
                    case 1:
                        sleepTextView.setText(R.string.analysis_fragment_last_week_steps);
                        break;
                    case 2:
                        sleepTextView.setText(R.string.analysis_fragment_last_month_solar);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public List<SleepData> getDummyData(int days){
        List<SleepData> sleepList = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < days; i++){
            DateTime dateTime = new DateTime();
            DateTime dateSleep = dateTime.plusDays(i);
            sleepList.add(new SleepData(40+r.nextInt(100),240+ r.nextInt(100),10+r.nextInt(20), dateSleep.getMillis()));
        }
        return sleepList;
    }
}
