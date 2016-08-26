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
import com.medcorp.model.Solar;
import com.medcorp.util.Preferences;
import com.medcorp.view.graphs.AnalysisSolarLineChart;

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
public class AnalysisSolarFragment extends BaseFragment {

    @Bind(R.id.analysis_solar_fragment_view_pager)
    ViewPager solarViewPager;
    @Bind(R.id.analysis_solar_fragment_title_tv)
    TextView solarTitleTextView;

    private View thisWeekView;
    private View lastWeekView;
    private View lastMonthView;
    private List<View> solarList;
    private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View solarView = inflater.inflate(R.layout.analysis_fragment_child_solar_fragment,container,false);
        ButterKnife.bind(this,solarView);

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
        return solarView;
    }


    private void initView(LayoutInflater inflater) {
        solarList = new ArrayList<>(3);
        thisWeekView = inflater.inflate(R.layout.analysis_solar_chart_fragment_layout,null);
        lastWeekView = inflater.inflate(R.layout.analysis_solar_chart_fragment_layout,null);
        lastMonthView = inflater.inflate(R.layout.analysis_solar_chart_fragment_layout,null);
        solarList.add(thisWeekView);
        solarList.add(lastWeekView);
        solarList.add(lastMonthView);
        initData(userSelectDate);
    }

    private void initData(Date userSelectDate) {

        AnalysisSolarLineChart thisWeekChart = (AnalysisSolarLineChart) thisWeekView.findViewById(R.id.analysis_solar_chart);
        AnalysisSolarLineChart lastWeekChart = (AnalysisSolarLineChart) lastWeekView.findViewById(R.id.analysis_solar_chart);
        AnalysisSolarLineChart lastMonthChart = (AnalysisSolarLineChart) lastMonthView.findViewById(R.id.analysis_solar_chart);

        // TODO Add real data from the database, also create database for solar.
        // TODO add other following textfields: Average Time in Sun, Most Time in Sun , Least time in sun, Total Time in Sun
        // 不明白用 GOOGLE TRANSLATE :D
        // remove dummy data after migrating to db
        thisWeekChart.addData(generateDummyData(7,0),7);
        lastWeekChart.addData(generateDummyData(7,7),7);
        lastMonthChart.addData(generateDummyData(30,0),30);


        AnalysisStepsChartAdapter adapter = new AnalysisStepsChartAdapter(solarList);
        solarViewPager.setAdapter(adapter);
        solarViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 0:
                        solarTitleTextView.setText(R.string.analysis_fragment_this_week_steps);
                        break;
                    case 1:
                        solarTitleTextView.setText(R.string.analysis_fragment_last_week_steps);
                        break;
                    case 2:
                        solarTitleTextView.setText(R.string.analysis_fragment_last_month_solar);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private List<Solar> generateDummyData(int maxDays, int offset){
        List<Solar> solarList = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < maxDays; i++){
            DateTime time = new DateTime();
            Solar solar = new Solar(time.toDate());
            solar.setDate(time.plusDays(i + offset).toDate());
            solar.setTotalHarvestingTime(r.nextInt(300));
            solarList.add(solar);
        }
        return solarList;
    }
}
