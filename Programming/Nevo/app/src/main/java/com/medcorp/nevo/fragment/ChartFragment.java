package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.base.BaseFragment;

/**
 * Created by Administrator on 2016/7/19.
 */
public class ChartFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,  Bundle savedInstanceState) {
        View lunarMainFragmentAdapterChart = inflater.inflate(R.layout.chart_fragment_lunar_main_fragment_adapter_layout,container,false);
        return lunarMainFragmentAdapterChart;
    }
}
