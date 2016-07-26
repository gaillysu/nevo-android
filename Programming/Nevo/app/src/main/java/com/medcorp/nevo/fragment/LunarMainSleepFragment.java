package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.base.BaseFragment;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View sleepView = inflater.inflate(R.layout.lunar_main_sleep_fragment_layout, container, false);
        ButterKnife.bind(this, sleepView);
        return sleepView;
    }
}
