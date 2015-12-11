package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.nevo.R;
import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Karl on 12/10/15.
 */
public class StepsHistoryFragment extends Fragment{

    @Bind(R.id.fragment_steps_history_distance)
    TextView distance;

    @Bind(R.id.fragment_steps_history_steps)
    TextView steps;

    @Bind(R.id.fragment_steps_history_consume)
    TextView calories;

    @Bind(R.id.fragment_steps_history_walkingdistance)
    TextView walkingdistance;

    @Bind(R.id.fragment_steps_history_walkingduration)
    TextView walkingduration;

    @Bind(R.id.fragment_steps_history_walkingcalories)
    TextView walkingcalories;

    @Bind(R.id.fragment_steps_history_runningdistance)
    TextView runningdistance;

    @Bind(R.id.fragment_steps_history_runningduration)
    TextView runningduration;

    @Bind(R.id.fragment_steps_history_runningcalories)
    TextView runningcalories;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps_history, container, false);
        ButterKnife.bind(this, view);
        // TODO Gailly, implement Step history fragment. See design.
        return view;
    }
}
