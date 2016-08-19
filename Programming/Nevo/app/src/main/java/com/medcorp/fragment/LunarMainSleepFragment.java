package com.medcorp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.medcorp.R;
import com.medcorp.event.DateSelectChangedEvent;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.model.Steps;
import com.medcorp.model.User;
import com.medcorp.util.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/26.
 */
public class LunarMainSleepFragment extends BaseFragment {


    @Bind(R.id.lunar_fragment_show_user_consume_calories)
    TextView showUserCosumeCalories;
    @Bind(R.id.lunar_fragment_show_user_steps_distance_tv)
    TextView showUserStepsDistance;
    @Bind(R.id.lunar_fragment_show_user_activity_time_tv)
    TextView showUserActivityTime;
    @Bind(R.id.lunar_fragment_show_user_steps_tv)
    TextView showUserSteps;
    @Bind(R.id.main_fragment_describe_text_three)
    TextView fragmentDescribeThree;
    @Bind(R.id.main_fragment_describe_text_one)
    TextView fragmentDescribeOne;
    @Bind(R.id.main_fragment_describe_text_two)
    TextView fragmentDescribeTwo;
    @Bind(R.id.main_fragment_describe_text_four)
    TextView fragmentDescribeFour;

    @Bind(R.id.fragment_sleep_history_linechart)
    LineChart lineChartSleep;

    private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View sleepView = inflater.inflate(R.layout.lunar_main_sleep_fragment_layout, container, false);
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
        fragmentDescribeOne.setText(getString(R.string.lunar_main_sleep_fragment_time_text));
        fragmentDescribeTwo.setText(getString(R.string.lunar_main_sleep_fragment_wake_time_text));
        fragmentDescribeThree.setText(getString(R.string.lunar_main_sleep_fragment_sleep_dec));
        fragmentDescribeFour.setText(getString(R.string.lunar_main_sleep_fragment_duration));


        initData(userSelectDate);
        modifyChart(lineChartSleep);
        return sleepView;
    }

    private void modifyChart(LineChart lineChart) {


    }
    public void initData(Date date) {
        User user = getModel().getNevoUser();
        Steps steps = getModel().getDailySteps(user.getNevoUserID(), date);
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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(final DateSelectChangedEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                userSelectDate = event.getDate();
                initData(userSelectDate);
                modifyChart(lineChartSleep);
            }
        });
    }
}
