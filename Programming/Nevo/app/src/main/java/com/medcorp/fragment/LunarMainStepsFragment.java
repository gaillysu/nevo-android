package com.medcorp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.R;
import com.medcorp.model.Steps;
import com.medcorp.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/7/19.
 */
public class LunarMainStepsFragment extends BaseFragment {

    @Bind(R.id.lunar_fragment_show_user_consume_calories)
    TextView showUserCosumeCalories;
    @Bind(R.id.lunar_fragment_show_user_steps_distance_tv)
    TextView showUserStepsDistance;
    @Bind(R.id.lunar_fragment_show_user_activity_time_tv)
    TextView showUserActivityTime;
    @Bind(R.id.lunar_fragment_show_user_steps_tv)
    TextView showUserSteps;
    @Bind(R.id.lunar_main_fragment_steps_chart)
    BarChart hourlyBarChart;
    private Steps steps;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String date = intent.getStringExtra("date");
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                Date changeDate = simple.parse(date);
                initData(changeDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View lunarMainFragmentAdapterChart = inflater.inflate(R.layout.chart_fragment_lunar_main_fragment_adapter_layout, container, false);
        ButterKnife.bind(this, lunarMainFragmentAdapterChart);
        initData(new Date());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("changeSelectDate");
        (this.getActivity()).registerReceiver(mBroadcastReceiver, intentFilter);

        modifyChart(hourlyBarChart);
        return lunarMainFragmentAdapterChart;
    }

    private void modifyChart(BarChart hourlyBarChart) {


    }


    public void initData(Date date) {
        User user = getModel().getNevoUser();
        steps = getModel().getDailySteps(user.getNevoUserID(), date);
        showUserActivityTime.setText(steps.getWalkDuration() != 0 ? formatTime(steps.getWalkDuration()) : 0 + "");
        showUserStepsDistance.setText(steps.getWalkDistance() != 0 ? steps.getWalkDistance() + "km" : 0 + "");
        showUserSteps.setText(steps.getSteps() + "");
        showUserCosumeCalories.setText(steps.getCalories() + "");
    }

    private String formatTime(int walkDuration) {
        StringBuffer activityTime = new StringBuffer();
        if (walkDuration >= 60) {
            if (walkDuration % 60 > 0) {
                activityTime.append(walkDuration % 60 + "h");
                activityTime.append(walkDuration - (walkDuration % 60 * 60) + "m");
            }
        } else {
            activityTime.append(walkDuration + "m");
        }

        return activityTime.toString();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.getActivity().unregisterReceiver(mBroadcastReceiver);
    }

}
