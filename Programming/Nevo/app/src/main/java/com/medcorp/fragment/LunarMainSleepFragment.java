package com.medcorp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import org.joda.time.DateTime;

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
    @Bind(R.id.lunar_hjbkarl)
    TextView wakeTimeTextView;

    @Bind(R.id.fragment_sleep_history_linechart)
    SleepTodayChart lineChartSleep;

    private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View sleepView = inflater.inflate(R.layout.lunar_main_sleep_fragment_layout, container, false);
        ButterKnife.bind(this, sleepView);
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
        if (sleepArray.length == 0) {
            DateTime dateTime = getToday();
            SleepData sleepData = new SleepData(0, 0, 0, date.getTime());
            sleepData.setSleepStart(dateTime.getMillis());
            sleepData.setSleepEnd(dateTime.withHourOfDay(8).getMillis());
            lineChartSleep.setDataInChart(sleepData);
            durationTextView.setText("0");
            qualityTextView.setText("0");
            sleepTimeTextView.setText("0");
            wakeTimeTextView.setText("0");
        } else {
            List<Sleep> sleepList = new ArrayList<>();

            for (int i = 0; i < sleepArray.length; i++) {
                sleepList.add(sleepArray[i]);
            }

            SleepDataHandler handler = new SleepDataHandler(sleepList, false);
            List<SleepData> sleepDataList = handler.getSleepData();

            if (!sleepDataList.isEmpty()) {
                SleepData sleepData;
                if (sleepDataList.size() == 2) {
                    sleepData = SleepDataUtils.mergeYesterdayToday(sleepDataList.get(1), sleepDataList.get(0));
                    DateTime sleepStart = new DateTime(sleepData.getSleepStart());
                    Log.w("Karl", "Yo yo : " + sleepData.getTotalSleep());

                    sleepTimeTextView.setText(sleepStart.toString("HH:mm", Locale.ENGLISH));
                    durationTextView.setText(TimeUtil.formatTime(sleepData.getTotalSleep()));
                } else {
                    sleepData = sleepDataList.get(0);
                    DateTime sleepStart = new DateTime(sleepData.getSleepStart());
                    sleepTimeTextView.setText(sleepStart.toString("HH:mm", Locale.ENGLISH));
                    durationTextView.setText(TimeUtil.formatTime(sleepData.getTotalSleep()));
                }
                qualityTextView.setText("100%");
                lineChartSleep.setDataInChart(sleepData);
                DateTime sleepEnd = new DateTime(sleepData.getSleepEnd());
                wakeTimeTextView.setText(sleepEnd.toString("HH:mm", Locale.ENGLISH));
            }
        }
    }

    private DateTime getToday() {
        return new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
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