package com.medcorp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.ApplicationFlag;
import com.medcorp.R;
import com.medcorp.event.DateSelectChangedEvent;
import com.medcorp.event.Timer10sEvent;
import com.medcorp.event.bluetooth.LittleSyncEvent;
import com.medcorp.event.bluetooth.OnSyncEvent;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.model.Sleep;
import com.medcorp.model.SleepData;
import com.medcorp.model.Steps;
import com.medcorp.model.User;
import com.medcorp.util.Common;
import com.medcorp.util.Preferences;
import com.medcorp.util.SleepDataHandler;
import com.medcorp.util.SleepDataUtils;
import com.medcorp.util.TimeUtil;
import com.medcorp.view.RoundProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    @Bind(R.id.main_steps_progress_bar)
    RoundProgressBar roundProgressBar;
    @Bind(R.id.lunar_fragment_show_user_consume_calories)
    TextView showUserCosumeCalories;
    @Bind(R.id.lunar_fragment_show_user_steps_distance_tv)
    TextView showUserStepsDistance;
    @Bind(R.id.lunar_fragment_show_user_activity_time_tv)
    TextView showUserActivityTime;
    @Bind(R.id.lunar_fragment_show_user_steps_tv)
    TextView showUserSteps;
    @Bind(R.id.main_clock_content_nevo_include)
    View nevoContentView;
    @Bind(R.id.main_clock_content_lunar_include)
    View lunarContentView;

    //    lunar main clock item

    @Bind(R.id.lunar_main_clock_sleep_time_count)
    TextView lunarSleepTotal;
    @Bind(R.id.lunar_main_clock_home_city_time)
    TextView lunarHomeCityTime;
    @Bind(R.id.lunar_main_clock_home_city_name)
    TextView homeCityName;
    @Bind(R.id.lunar_main_clock_home_country)
    TextView countryName;
    @Bind(R.id.lunar_main_clock_home_city_sunrise_time_tv)
    TextView sunriseOfsunsetTime;
    @Bind(R.id.lunar_main_clock_home_city_name_tv)
    TextView sunriseCityName;
    @Bind(R.id.lunar_main_clock_steps_goal_analysis)
    RoundProgressBar goalProgress;
    @Bind(R.id.lunar_main_clock_steps_count)
    TextView stepsCount;
    @Bind(R.id.steps_of_goal_percentage)
    TextView goalPercentage;

    private Date userSelectDate;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private User user = getModel().getNevoUser();

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
        if (selectDate == null) {
            userSelectDate = new Date();
        } else {
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
        if (ApplicationFlag.FLAG == ApplicationFlag.Flag.NEVO) {
            roundProgressBar.setVisibility(View.VISIBLE);
            nevoContentView.setVisibility(View.VISIBLE);
            lunarContentView.setVisibility(View.GONE);
        } else if (ApplicationFlag.FLAG == ApplicationFlag.Flag.LUNAR) {
            nevoContentView.setVisibility(View.GONE);
            lunarContentView.setVisibility(View.VISIBLE);
            roundProgressBar.setVisibility(View.GONE);
            initLunarData(date);
        }

        User user = getModel().getNevoUser();
        Steps steps = getModel().getDailySteps(user.getNevoUserID(), date);
        showUserActivityTime.setText(TimeUtil.formatTime(steps.getWalkDuration() + steps.getRunDuration()));
        showUserSteps.setText(String.valueOf(steps.getSteps()));

        String result = null;
        DecimalFormat df = new DecimalFormat("######0.00");
        if (Preferences.getUnitSelect(MainClockFragment.this.getActivity())) {
            result = df.format(user.getDistanceTraveled(steps) * 0.6213712f) + getString(R.string.unit_length);
        } else {
            result = String.format(Locale.ENGLISH, "%.2f km", user.getDistanceTraveled(steps));
        }
        String calories = user.getConsumedCalories(steps) + getString(R.string.unit_cal);

        showUserStepsDistance.setText(String.valueOf(result));
        showUserCosumeCalories.setText(calories);

        int countSteps = steps.getSteps();
//        int goal = steps.getGoal();
        float value = (float) countSteps / (float) steps.getGoal();
        roundProgressBar.setProgress(value * 100 >= 100f ? 100 : (int) (value * 100));
        goalProgress.setProgress(value * 100 >= 100f ? 100 : (int) (value * 100));
        goalPercentage.setText((value * 100 >= 100f ? 100 : (int) (value * 100))+"%"+getString(R.string.lunar_steps_percentage));
    }

    private void initLunarData(Date date) {
        Steps dailySteps = getModel().getDailySteps(user.getNevoUserID(), date);
        lunarSleepTotal.setText(countSleepTime(date));
        stepsCount.setText(dailySteps.getRunSteps() + dailySteps.getWalkSteps() + "");

    }

    private String countSleepTime(Date date) {
        Sleep[] sleepArray = getModel().getDailySleep(user.getNevoUserID(), date);
        SleepDataHandler handler = new SleepDataHandler(Arrays.asList(sleepArray));
        List<SleepData> sleepDataList = handler.getSleepData(date);
        String totalSleepTime;
        if (!sleepDataList.isEmpty()) {
            SleepData sleepData;
            if (sleepDataList.size() == 2) {
                sleepData = SleepDataUtils.mergeYesterdayToday(sleepDataList.get(1), sleepDataList.get(0));
                totalSleepTime = TimeUtil.formatTime(sleepData.getTotalSleep());
            } else {
                sleepData = sleepDataList.get(0);
                totalSleepTime = TimeUtil.formatTime(sleepData.getTotalSleep());
            }
            return totalSleepTime;
        }
        return new String("00:00");
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
    public void onEvent(LittleSyncEvent event) {
        if (event.isSuccess()) {
            Steps steps = getModel().getDailySteps(getModel().getNevoUser().getNevoUserID(), Common.removeTimeFromDate(userSelectDate));
            if (steps == null) {
                return;
            }
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
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED || event.getStatus() == OnSyncEvent.SYNC_EVENT.TODAY_SYNC_STOPPED) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    initData(userSelectDate);
                }
            });
        }
    }

}
