package com.medcorp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.medcorp.R;
import com.medcorp.fragment.base.BaseFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/21.
 */
public class LastWeekStepsFragment extends BaseFragment {

    @Bind(R.id.last_week_steps_fragment_chart)
    LineChart lastWeekChart;

    private Date userSelectDate;

    private BroadcastReceiver changeDateBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                 String selectDateString = intent.getStringExtra("date");
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
            try {
                userSelectDate =  simple.parse(selectDateString);
                initData(userSelectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.last_week_chart_fragment_layout,container,false);
        ButterKnife.bind(this,view);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("changeSelectDate");
        LastWeekStepsFragment.this.getActivity().registerReceiver(changeDateBroadcast,intentFilter);

        initData(userSelectDate);
        return view;
    }

    private void initData(Date date) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.getActivity().unregisterReceiver(changeDateBroadcast);
    }
}
