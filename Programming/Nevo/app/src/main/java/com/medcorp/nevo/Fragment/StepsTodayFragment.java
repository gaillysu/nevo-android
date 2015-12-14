package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.view.RoundProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Karl on 12/10/15.
 */
public class StepsTodayFragment  extends Fragment{

    @Bind(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;

    @Bind(R.id.fragment_steps_today_goal)
    TextView goal;

    @Bind(R.id.fragment_steps_today_reach)
    TextView goal_reach;

    @Bind(R.id.fragment_steps_today_progress)
    TextView goal_progress;

    @Bind(R.id.fragment_steps_today_distance)
    TextView distance;

    @Bind(R.id.fragment_steps_today_dailysteps)
    TextView dailysteps;

    @Bind(R.id.fragment_steps_today_calories)
    TextView calories;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps_today, container, false);
        ButterKnife.bind(this, view);
        //TODO start sync nevo, when sync done, refresh screen
        return view;
    }

}
