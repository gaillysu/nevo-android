package com.medcorp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.medcorp.R;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.util.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/21.
 */
public class ThisWeekStepsFragment extends BaseFragment {

    @Bind(R.id.this_week_steps_fragment_chart)
    LineChart thisWeekChart;

    @Bind(R.id.test_date_text)
    TextView tv;

    private Date userSelectDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.this_week_chart_fragment_layout, container, false);
        ButterKnife.bind(this, view);
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
        initData(userSelectDate);
        return view;
    }


    private void initData(Date userSelectDate) {
        tv.setText(new SimpleDateFormat("yyyy-MM-dd").format(userSelectDate));
    }
}
