package com.medcorp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.util.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

   private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_solar_fragment_layout ,container ,false);
        String selectDate = Preferences.getSelectDate(this.getContext());
        if(selectDate == null){
            userSelectDate = new java.util.Date();
        }else{
            try {
                userSelectDate = new SimpleDateFormat("yyyy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ButterKnife.bind(this,view);
        initData(userSelectDate);
        return view;
    }

    private void initData(Date userSelectDate) {

    }
}
