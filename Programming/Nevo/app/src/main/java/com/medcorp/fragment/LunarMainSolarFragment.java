package com.medcorp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.medcorp.R;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.util.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/8/12.
 */
public class LunarMainSolarFragment extends BaseFragment {

    @Bind(R.id.today_solar_battery_time_tv)
    TextView batteryTimeTv;
    @Bind(R.id.today_solar_solar_time_tv)
    TextView solarTimeTv;
    @Bind(R.id.main_fragment_solar_pie_chart)
    PieChart solarPieChart;

    private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_solar_fragment_layout, container, false);
        String selectDate = Preferences.getSelectDate(this.getContext());
        if (selectDate == null) {
            userSelectDate = new java.util.Date();
        } else {
            try {
                userSelectDate = new SimpleDateFormat("yyyy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ButterKnife.bind(this, view);
        initData(userSelectDate);
        return view;
    }

    private void initData(Date userSelectDate) {
        float[] solarPieChartDate = {40f, 60f};
        String[] destory = {"solar","battery"};
        ArrayList<String> des = new ArrayList<>();
        solarPieChart.setUsePercentValues(true);
        solarPieChart.setDescription("");
        solarPieChart.setDrawHoleEnabled(false);
        solarPieChart.setDrawCenterText(false);

        ArrayList<Entry> yValue = new ArrayList<>();
        for (int i = 0; i < solarPieChartDate.length; i++) {
            des.add(i,destory[i]);
            yValue.add(new Entry(solarPieChartDate[i],i));
        }

        PieDataSet pieDataSet = new PieDataSet(yValue,"");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(R.color.steps_identification_tint_color);
        pieDataSet.setColors(colors);

        PieData pieData = new PieData( des, pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextColor(R.color.text_color);//设置描述字体颜色

        pieData.setDataSet(pieDataSet);
        solarPieChart.setData(pieData);
        solarPieChart.invalidate();//更新
    }
}
