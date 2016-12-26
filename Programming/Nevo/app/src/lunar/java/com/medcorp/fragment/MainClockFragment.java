package com.medcorp.fragment;

import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/12/26.
 */

public class MainClockFragment extends BaseFragment {

    @Bind(R.id.HomeClockHour)
    ImageView hourImage;
    @Bind(R.id.HomeClockMinute)
    ImageView minImage;
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
    @Bind(R.id.lunar_main_clock_home_city_sunrise_icon)
    ImageView sunriseOrSunsetIv;
    @Bind(R.id.lunar_main_clock_home_city_sunrise)
    TextView sunriseTv;

    private Date userSelectDate;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private User user;

    private String homeName;
    private String homeCountryName;
    private String timeZoneId;

    private void refreshClock() {
        final Calendar mCalendar = Calendar.getInstance();
        int mCurHour = mCalendar.get(Calendar.HOUR);
        int mCurMin = mCalendar.get(Calendar.MINUTE);
        minImage.setRotation((float) (mCurMin * 6));
        hourImage.setRotation((float) ((mCurHour + mCurMin / 60.0) * 30));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainClockFragmentView = inflater.inflate(R.layout.lunar_main_fragment_adapter_clock_layout, container, false);
        ButterKnife.bind(this, mainClockFragmentView);
        user = getModel().getNevoUser();
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
        return mainClockFragmentView;
    }

    private void initData(Date date) {
        Steps dailySteps = getModel().getDailySteps(user.getNevoUserID(), date);
        lunarSleepTotal.setText(countSleepTime(date));
        stepsCount.setText(dailySteps.getRunSteps() + dailySteps.getWalkSteps() + "");
        Address positionLocal = getModel().getPositionLocal(getModel().getLocationController().getLocation());
        homeName = Preferences.getPositionCity(MainClockFragment.this.getActivity());
        homeCountryName = Preferences.getPositionCountry(MainClockFragment.this.getActivity());
        timeZoneId = Preferences.getHomeTimezoneId(MainClockFragment.this.getActivity());

        if (homeName != null) {
            homeCityName.setText(homeName);
            countryName.setText(homeCountryName);
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
            String am_pm = calendar.get(Calendar.HOUR_OF_DAY) > 12 ? getString(R.string.time_able_morning) : getString(R.string.time_able_afternoon);
            String minute = calendar.get(Calendar.MINUTE) >= 10 ? calendar.get(Calendar.MINUTE) + "" : "0" + calendar.get(Calendar.MINUTE);
            lunarHomeCityTime.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + minute + am_pm);
            sunriseCityName.setText(homeCountryName);
        } else {
            homeName = positionLocal.getLocality();
            homeCountryName = positionLocal.getCountryName();
            homeCityName.setText(homeName);
            countryName.setText(homeCountryName);
            Calendar calendar = Calendar.getInstance();
            String am_pm = calendar.get(Calendar.HOUR_OF_DAY) > 12 ? getString(R.string.time_able_morning) : getString(R.string.time_able_afternoon);
            String minute = calendar.get(Calendar.MINUTE) >= 10 ? calendar.get(Calendar.MINUTE) + "" : "0" + calendar.get(Calendar.MINUTE);
            lunarHomeCityTime.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + minute + am_pm);
            sunriseCityName.setText(homeCountryName);
        }

        SunriseSunsetCalculator calculator = computeSunriseTime(positionLocal.getLatitude(), positionLocal.getLongitude()
                , Calendar.getInstance().getTimeZone().getID());
        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        int sunriseHour = Integer.parseInt(officialSunrise.split(":")[0]);
        int sunriseMin = Integer.parseInt(officialSunrise.split(":")[1]);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (sunriseHour * 60 + sunriseMin > hour * 60 + minute) {
            sunriseOfsunsetTime.setText(officialSunrise);
            sunriseOrSunsetIv.setImageDrawable(getResources().getDrawable(R.drawable.sunrise_icon));
            sunriseTv.setText(getString(R.string.lunar_main_clock_home_city_sunrise));
        } else {
            sunriseOfsunsetTime.setText(officialSunset);
            sunriseTv.setText(getString(R.string.lunar_main_clock_home_city_sunset));
            sunriseOrSunsetIv.setImageDrawable(getResources().getDrawable(R.drawable.sunset_icon));
        }
        int countSteps = dailySteps.getSteps();
        float value = (float) countSteps / (float) dailySteps.getGoal();
        goalProgress.setProgress(value * 100 >= 100f ? 100 : (int) (value * 100));
        goalPercentage.setText((value * 100 >= 100f ? 100 : (int) (value * 100)) + "%" + getString(R.string.lunar_steps_percentage));
    }

    private SunriseSunsetCalculator computeSunriseTime(double latitude, double longitude, String zone) {
        com.luckycatlabs.sunrisesunset.dto.Location sunriseLocation =
                new com.luckycatlabs.sunrisesunset.dto.Location(latitude + "", longitude + "");
        return new SunriseSunsetCalculator(sunriseLocation, zone);
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
