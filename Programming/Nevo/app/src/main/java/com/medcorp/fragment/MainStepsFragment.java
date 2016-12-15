package com.medcorp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.event.DateSelectChangedEvent;
import com.medcorp.event.bluetooth.LittleSyncEvent;
import com.medcorp.event.bluetooth.OnSyncEvent;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.model.Steps;
import com.medcorp.model.User;
import com.medcorp.util.Common;
import com.medcorp.util.Preferences;
import com.medcorp.util.TimeUtil;
import com.medcorp.view.graphs.MainStepsBarChart;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/7/19.
 */
public class MainStepsFragment extends BaseFragment {

    @Bind(R.id.lunar_fragment_show_user_consume_calories)
    TextView showUserConsumeCalories;
    @Bind(R.id.lunar_fragment_show_user_steps_distance_tv)
    TextView showUserStepsDistance;
    @Bind(R.id.lunar_fragment_show_user_activity_time_tv)
    TextView showUserActivityTime;
    @Bind(R.id.lunar_fragment_show_user_steps_tv)
    TextView showUserSteps;
    @Bind(R.id.lunar_main_fragment_steps_chart)
    MainStepsBarChart hourlyBarChart;

    private Steps steps;
    private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View lunarMainFragmentAdapterChart = inflater.inflate(R.layout.chart_fragment_lunar_main_fragment_adapter_layout, container, false);
        ButterKnife.bind(this, lunarMainFragmentAdapterChart);

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
        hourlyBarChart.animateY(3000);
        initData(userSelectDate);
        return lunarMainFragmentAdapterChart;
    }

    private void initData(Date date) {


        User user = getModel().getNevoUser();
        steps = getModel().getDailySteps(user.getNevoUserID(), date);

        showUserActivityTime.setText(TimeUtil.formatTime(steps.getWalkDuration() + steps.getRunDuration()));
        showUserSteps.setText(String.valueOf(steps.getSteps()));
        String result = null;
        String calories = user.getConsumedCalories(steps) + getString(R.string.unit_cal);
        DecimalFormat df = new DecimalFormat("######0.00");
        if (Preferences.getUnitSelect(MainStepsFragment.this.getActivity())) {
            result = df.format(user.getDistanceTraveled(steps) * 0.6213712f) + getString(R.string.unit_length);
        } else {
            result = String.format(Locale.ENGLISH, "%.2f km", user.getDistanceTraveled(steps));
        }

        showUserStepsDistance.setText(String.valueOf(result));
        showUserConsumeCalories.setText(calories);

        if (steps.getSteps() != 0 && steps.getHourlySteps() != null) {
            JSONArray array = null;
            try {
                array = new JSONArray(steps.getHourlySteps());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int[] stepsArray = new int[24];
            for (int i = 0; i < 24; i++) {
                stepsArray[i] = array.optInt(i, 0);
            }
            hourlyBarChart.setDataInChart(stepsArray);
        } else {
            hourlyBarChart.setDataInChart(new int[]{0});
        }
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
            }
        });
    }

    @Subscribe
    public void onEvent(final OnSyncEvent event) {
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED || event.getStatus() == OnSyncEvent.SYNC_EVENT.TODAY_SYNC_STOPPED) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    initData(userSelectDate);
                }
            });
        }
    }

    @Subscribe
    public void onEvent(LittleSyncEvent event) {
        if (event.isSuccess()) {
            Steps steps = getModel().getDailySteps(getModel().getNevoUser().getNevoUserID(), Common.removeTimeFromDate(userSelectDate));
            if (steps == null) {
                return;
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    initData(userSelectDate);
                }
            });
        }
    }
}
