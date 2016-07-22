package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/21.
 */
public class ThisWeekStepsFragment extends BaseFragment {

    @Bind(R.id.this_week_steps_fragment_chart)
    LineChart thisWeekChart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.this_week_chart_fragment_layout, container, false);
        ButterKnife.bind(this,view);
        modifyChart(thisWeekChart);
        return view;
    }

    private void modifyChart(LineChart lineChart) {
//
//        lineChart.setContentDescription("");
//        lineChart.setDescription("");
//        lineChart.setNoDataTextDescription("");
//        lineChart.setNoDataText("");
//        lineChart.setDragEnabled(false);
//        lineChart.setScaleEnabled(false);
//        lineChart.setPinchZoom(false);
//        lineChart.getLegend().setEnabled(false);
//        TipsView tipsView = new TipsView(this.getContext(), R.layout.custom_marker_view);
//        lineChart.setMarkerView(tipsView);
    }

}
