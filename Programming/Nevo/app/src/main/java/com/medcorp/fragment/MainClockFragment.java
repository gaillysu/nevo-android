package com.medcorp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.event.DateSelectChangedEvent;
import com.medcorp.event.Timer10sEvent;
import com.medcorp.event.bluetooth.LittleSyncEvent;
import com.medcorp.event.bluetooth.OnSyncEvent;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.model.Steps;
import com.medcorp.model.User;
import com.medcorp.util.Common;
import com.medcorp.util.Preferences;
import com.medcorp.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/19.
 */
public class MainClockFragment extends BaseFragment {

    @Bind(R.id.HomeClockHour)
    ImageView hourImage;

    @Bind(R.id.HomeClockMinute)
    ImageView minImage;

    @Bind(R.id.lunar_fragment_show_user_consume_calories)
    TextView showUserCosumeCalories;
    @Bind(R.id.lunar_fragment_show_user_steps_distance_tv)
    TextView showUserStepsDistance;
    @Bind(R.id.lunar_fragment_show_user_activity_time_tv)
    TextView showUserActivityTime;
    @Bind(R.id.lunar_fragment_show_user_steps_tv)
    TextView showUserSteps;

    private Date userSelectDate;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    private void refreshClock() {
        final Calendar mCalendar = Calendar.getInstance();
        int mCurHour = mCalendar.get(Calendar.HOUR);
        int mCurMin = mCalendar.get(Calendar.MINUTE);
        minImage.setRotation((float) (mCurMin * 6));
        hourImage.setRotation((float) ((mCurHour + mCurMin / 60.0) * 30));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View clockFragmentContentView = inflater.inflate(R.layout.lunar_main_fragment_adapter_clock_layout, container, false);
        ButterKnife.bind(this, clockFragmentContentView);

        String selectDate = Preferences.getSelectDate(this.getContext());
        if(selectDate == null){
            userSelectDate = new Date();
        }else{
            try {
                userSelectDate = new SimpleDateFormat("yyyy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        refreshClock();
        initData(userSelectDate);
        return clockFragmentContentView;
    }

    public void initData(Date date) {
        User user = getModel().getNevoUser();
        Steps steps = getModel().getDailySteps(user.getNevoUserID(), date);
        showUserActivityTime.setText(steps.getWalkDuration() != 0 ? TimeUtil.formatTime(steps.getWalkDuration() + steps.getRunDuration()) : 0 + " min");
        showUserSteps.setText(String.valueOf(steps.getSteps()));
        String result = String.format(Locale.ENGLISH,"%.2f km", user.getDistanceTraveled(steps));
        showUserStepsDistance.setText(result);
        showUserCosumeCalories.setText(String.valueOf(user.getConsumedCalories(steps)));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        //NOTICE: if do full big sync, that will consume more battery power and more time (MAX 7 days data),so only big sync today's data
        if(Common.removeTimeFromDate(new Date()).getTime() == Common.removeTimeFromDate(userSelectDate).getTime()) {
            getModel().getSyncController().getDailyTrackerInfo(false);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(LittleSyncEvent event) {
        if (event.isSuccess()) {
            Steps steps = getModel().getDailySteps(getModel().getNevoUser().getNevoUserID(), Common.removeTimeFromDate(userSelectDate));
            if (steps == null) {
                return;
            }
            int dailySteps = steps.getSteps();
            int dailyGoal = steps.getGoal();
            Log.i("MainClockFragment", "dailySteps = " + dailySteps + ",dailyGoal = " + dailyGoal);
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    initData(userSelectDate);
                }
            });
          }
    }

    @Subscribe
    public void onEvent(final DateSelectChangedEvent event) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                userSelectDate = event.getDate();
                initData(userSelectDate);
            }
        });
    }

    @Subscribe
    public void onEvent(final Timer10sEvent event) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                refreshClock();
            }
        });
    }

    @Subscribe
    public void onEvent(final OnSyncEvent event) {
        if(event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    initData(userSelectDate);
                }
            });
        }
    }

}
