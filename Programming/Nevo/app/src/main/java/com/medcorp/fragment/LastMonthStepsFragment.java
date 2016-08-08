package com.medcorp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.medcorp.R;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.util.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/21.
 */
public class LastMonthStepsFragment extends BaseFragment {

    @Bind(R.id.last_month_steps_fragment_chart)
    LineChart lastMonthChart;

    private Date changeDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.last_month_chart_fragment_layout,container,false);
        ButterKnife.bind(this,view);
        String selectDate = Preferences.getSelectDate(this.getContext());
        if(selectDate == null){
            changeDate = new Date();
        }else{
            try {
                changeDate = new SimpleDateFormat("yyyy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        initData(changeDate);
        return view;
    }


    private void initData(Date changeDate) {

    }
}
