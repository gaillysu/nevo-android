package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.adapter.AnalysisStepsChartAdapter;
import com.medcorp.nevo.fragment.base.BaseFragment;

import java.util.ArrayList;

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

    private ArrayList<Fragment> stepsDataList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View stepsView = inflater.inflate(R.layout.analysis_fragment_child_steps_fragment, container, false);
        ButterKnife.bind(this, stepsView);
        initView();
        return stepsView;
    }

    private void initView() {
        stepsDataList = new ArrayList<>(3);
        ThisWeekStepsFragment thisWeek = (ThisWeekStepsFragment) ThisWeekStepsFragment.instantiate(this.getContext(), ThisWeekStepsFragment.class.getName());
        LastWeekStepsFragment lastWeek = (LastWeekStepsFragment) LastWeekStepsFragment.instantiate(this.getContext(), LastWeekStepsFragment.class.getName());
        LastMonthStepsFragment lastMonth = (LastMonthStepsFragment) LastMonthStepsFragment.instantiate(this.getContext(), LastMonthStepsFragment.class.getName());
        stepsDataList.add(thisWeek);
        stepsDataList.add(lastWeek);
        stepsDataList.add(lastMonth);
        AnalysisStepsChartAdapter adapter = new AnalysisStepsChartAdapter(getChildFragmentManager(), stepsDataList);
        chartViewPager.setAdapter(adapter);
        chartViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position){
                    case 1:
                        analysisStepsText.setText(R.string.analysis_fragment_last_week_steps);
                        break;
                    case 2:
                        analysisStepsText.setText(R.string.analysis_fragment_last_month_solar);
                        break;
                    case 0:
                        analysisStepsText.setText(R.string.analysis_fragment_this_week_steps);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
