package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.base.BaseFragment;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.model.User;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/19.
 */
public class ChartFragment extends BaseFragment {

    @Bind(R.id.lunar_fragment_show_user_consume_calories)
    TextView showUserCosumeCalories;
    @Bind(R.id.lunar_fragment_show_user_steps_distance_tv)
    TextView showUserStepsDistance;
    @Bind(R.id.lunar_fragment_show_user_activity_time_tv)
    TextView showUserActivityTime;
    @Bind(R.id.lunar_fragment_show_user_steps_tv)
    TextView showUserSteps;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,  Bundle savedInstanceState) {
        View lunarMainFragmentAdapterChart = inflater.inflate(R.layout.chart_fragment_lunar_main_fragment_adapter_layout,container,false);
        ButterKnife.bind(this,lunarMainFragmentAdapterChart);
        initData(new Date());
        return lunarMainFragmentAdapterChart;
    }

    private void initData(Date date) {
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

}
