package com.medcorp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/21.
 */
public class AnalysisSleepFragment extends BaseFragment {

    @Bind(R.id.steps_fragment_average_steps_tv)
    TextView averageStepsText;
    @Bind(R.id.steps_fragment_total_steps_tv)
    TextView totalStepsText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View sleepView = inflater.inflate(R.layout.analysis_fragment_child_sleep_fragment,container,false);
        ButterKnife.bind(this,sleepView);

        return sleepView;
    }
}
