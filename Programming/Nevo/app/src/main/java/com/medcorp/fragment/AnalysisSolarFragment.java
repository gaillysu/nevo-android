package com.medcorp.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.medcorp.R;
import com.medcorp.adapter.AnalysisStepsChartAdapter;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.util.Preferences;

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
public class AnalysisSolarFragment extends BaseFragment {

    @Bind(R.id.analysis_solar_fragment_view_pager)
    ViewPager solarViewPager;
    @Bind(R.id.steps_fragment_title_tv)
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
        Log.w("Karl","Test push");
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
        thisWeekView = inflater.inflate(R.layout.this_week_chart_fragment_layout,null);
        lastWeekView = inflater.inflate(R.layout.last_week_chart_fragment_layout,null);
        lastMonthView = inflater.inflate(R.layout.last_month_chart_fragment_layout,null);
        solarList.add(thisWeekView);
        solarList.add(lastWeekView);
        solarList.add(lastMonthView);
        initData(userSelectDate);
    }

    private void initData(Date userSelectDate) {

        LineChart thisWeekChart = (LineChart) thisWeekView.findViewById(R.id.this_week_steps_fragment_chart);
        LineChart lastWeekChart = (LineChart) lastWeekView.findViewById(R.id.last_week_steps_fragment_chart);
        LineChart lastMonthChart = (LineChart) lastMonthView.findViewById(R.id.last_month_steps_fragment_chart);


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
}
