package com.medcorp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.R;

/**
 * Created by Administrator on 2016/7/21.
 */
public class LastWeekStepsFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.last_week_chart_fragment_layout,container,false);
        return view;
    }
}
