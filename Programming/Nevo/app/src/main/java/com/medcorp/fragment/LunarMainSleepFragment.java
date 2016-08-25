package com.medcorp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;
import com.medcorp.R;
import com.medcorp.event.DateSelectChangedEvent;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.model.Sleep;
import com.medcorp.model.SleepData;
import com.medcorp.model.User;
import com.medcorp.util.Preferences;
import com.medcorp.util.SleepDataHandler;
import com.medcorp.util.SleepDataUtils;
import com.medcorp.util.TimeUtil;
import com.medcorp.view.graphs.SleepTodayChart;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/26.
 */
public class LunarMainSleepFragment extends BaseFragment {


    @Bind(R.id.lunar_sleep_fragment_duration)
    TextView durationTextView;

    @Bind(R.id.lunar_sleep_fragment_quality)
    TextView qualityTextView;

    @Bind(R.id.lunar_sleep_fragment_sleep_time)
    TextView sleepTimeTextView;

    TextView wakeTimeTextView;

    @Bind(R.id.fragment_sleep_history_linechart)
    SleepTodayChart lineChartSleep;

    private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View sleepView = inflater.inflate(R.layout.lunar_main_sleep_fragment_layout, container, false);
        ButterKnife.bind(this, sleepView);
        wakeTimeTextView = (TextView) sleepView.findViewById(R.id.lunar_hjbkarl);
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
        return sleepView;
    }

    public void initData(Date date) {
        User user = getModel().getNevoUser();
        Sleep[] sleepArray = getModel().getDailySleep(user.getNevoUserID(), date);
        // TEST DATA

        Sleep yesterday = new Sleep(1448467200000l);
        yesterday.setDate(1448553600000L);
        yesterday.setStart(1448638260000L);
        yesterday.setEnd(1448553600000L);
        yesterday.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0]");
        yesterday.setHourlyLight("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 45, 60, 60]");
        yesterday.setHourlyDeep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;
        yesterday.setHourlySleep("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50, 60, 60]") ;


        Sleep today = new Sleep(1448640000000l);;
        today.setDate(1448640000000l);
        today.setStart(1448640000000l);
        today.setEnd(1471820700000l);
        today.setHourlyWake("[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        today.setHourlyLight("[23, 50, 27, 23, 50, 27, 23, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
        today.setHourlyDeep("[37, 10, 33, 37, 10, 33, 37, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]") ;
        today.setHourlySleep("[60, 60, 60, 60, 60, 60, 60, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");

        List<Sleep> sleepList = new ArrayList<>();
        sleepList.add(yesterday);
        sleepList.add(today);
        SleepDataHandler handler = new SleepDataHandler(sleepList,false);
        List<SleepData> sleepDataList = handler.getSleepData();
        // TEST DATA
        if (!sleepDataList.isEmpty()){
            SleepData sleepData;
            if (sleepDataList.size() == 2){
                sleepData = SleepDataUtils.mergeYesterdayToday(sleepDataList.get(1),sleepDataList.get(0));
                DateTime sleepStart = new DateTime(sleepData.getSleepStart());
                Log.w("Karl","Yo yo : " + sleepData.getTotalSleep());

                sleepTimeTextView.setText(sleepStart.toString("HH:mm", Locale.ENGLISH));
                durationTextView.setText(TimeUtil.formatTime(sleepData.getTotalSleep()));
            }else{
                sleepData = sleepDataList.get(0);
                DateTime sleepStart = new DateTime(sleepData.getSleepStart());
                sleepTimeTextView.setText(sleepStart.toString("HH:mm", Locale.ENGLISH));
                durationTextView.setText(TimeUtil.formatTime(sleepData.getTotalSleep()));
            }
            lineChartSleep.setDataInChart(sleepData);
            qualityTextView.setText("100%");
            DateTime sleepEnd = new DateTime(sleepData.getSleepEnd());
            wakeTimeTextView.setText(sleepEnd.toString("HH:mm", Locale.ENGLISH));
        }else{
            lineChartSleep.setEmptyChart();
            durationTextView.setText("0");
            qualityTextView.setText("0");
            sleepTimeTextView.setText("0");
            wakeTimeTextView.setText("0");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(final DateSelectChangedEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                userSelectDate = event.getDate();
                initData(userSelectDate);
            }
        });
    }
}
