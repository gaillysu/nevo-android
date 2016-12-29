package com.medcorp.fragment;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.medcorp.R;
import com.medcorp.event.DateSelectChangedEvent;
import com.medcorp.event.LocationChangedEvent;
import com.medcorp.event.Timer10sEvent;
import com.medcorp.event.bluetooth.LittleSyncEvent;
import com.medcorp.event.bluetooth.OnSyncEvent;
import com.medcorp.event.bluetooth.SolarConvertEvent;
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

import net.medcorp.library.worldclock.City;

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
import io.realm.Realm;
import io.realm.RealmResults;

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
    TextView sunriseOfSunsetTime;
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
    @Bind(R.id.lunar_main_clock_battery_status)
    TextView solar_harvest_status;
    private Date userSelectDate;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private User user;

    private String homeName;
    private String homeCountryName;
    private String timeZoneId;
    private Address mPositionLocal;
    private SunriseSunsetCalculator calculator;
    private Realm realm = Realm.getDefaultInstance();
    private City LocalCity;
    private Calendar mCalendar;
    private Location location;

    private void refreshClock() {
        final Calendar mCalendar = Calendar.getInstance();
        int mCurHour = mCalendar.get(Calendar.HOUR);
        int mCurMin = mCalendar.get(Calendar.MINUTE);
        minImage.setRotation((float) (mCurMin * 6));
        hourImage.setRotation((float) ((mCurHour + mCurMin / 60.0) * 30));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPositionLocal = getModel().getPositionLocal(location);
        user = getModel().getNevoUser();
        EventBus.getDefault().register(this);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainClockFragmentView = inflater.inflate(R.layout.lunar_main_fragment_adapter_clock_layout, container, false);
        ButterKnife.bind(this, mainClockFragmentView);
        refreshClock();
        initData(userSelectDate);
        return mainClockFragmentView;
    }

    private void initData(Date date) {
        lunarSleepTotal.setText(countSleepTime(date));
        Steps dailySteps = getModel().getDailySteps(user.getNevoUserID(), date);
        stepsCount.setText(dailySteps.getRunSteps() + dailySteps.getWalkSteps() + "");
        if (mPositionLocal != null) {
            calculator = computeSunriseTime(mPositionLocal.getLatitude(), mPositionLocal.getLongitude()
                    , Calendar.getInstance().getTimeZone().getID());
            sunriseCityName.setText(mPositionLocal.getCountryName());
        } else {
            RealmResults<City> cities = realm.where(City.class).findAll();
            TimeZone timeZone = Calendar.getInstance().getTimeZone();
            String localCityName = timeZone.getID().split("/")[1].replace("_", " ");
            for (City city : cities) {
                if (city.getName().equals(localCityName)) {
                    calculator = computeSunriseTime(city.getLat(), city.getLng()
                            , Calendar.getInstance().getTimeZone().getID());
                    this.LocalCity = city;
                    sunriseCityName.setText(LocalCity.getCountry());
                    break;
                }
            }
        }
        setData();
        setSunsetOrSunrise();

        int countCalories = dailySteps.getRunSteps() + dailySteps.getWalkSteps();
        float valueCalories = (float) countCalories / (float) dailySteps.getGoal();
        goalProgress.setProgress(valueCalories * 100 >= 100f ? 100 : (int) (valueCalories * 100));
        goalPercentage.setText((valueCalories * 100 >= 100f ? 100 : (int) (valueCalories * 100)) + "%" + getString(R.string.lunar_steps_percentage));
    }

    private void setSunsetOrSunrise() {

        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());

        int sunriseHour = Integer.parseInt(officialSunrise.split(":")[0]);
        int sunriseMin = Integer.parseInt(officialSunrise.split(":")[1]);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (sunriseHour * 60 + sunriseMin > hour * 60 + minute) {
            sunriseOfSunsetTime.setText(officialSunrise);
            sunriseOrSunsetIv.setImageDrawable(getResources().getDrawable(R.drawable.sunrise_icon));
            sunriseTv.setText(getString(R.string.lunar_main_clock_home_city_sunrise));
        } else {
            sunriseOfSunsetTime.setText(officialSunset);
            sunriseTv.setText(getString(R.string.lunar_main_clock_home_city_sunset));
            sunriseOrSunsetIv.setImageDrawable(getResources().getDrawable(R.drawable.sunset_icon));
        }

    }

    private void setData() {
        homeName = Preferences.getPositionCity(MainClockFragment.this.getActivity());
        homeCountryName = Preferences.getPositionCountry(MainClockFragment.this.getActivity());
        timeZoneId = Preferences.getHomeTimezoneId(MainClockFragment.this.getActivity());
        if (homeName != null) {
            homeCityName.setText(homeName);
            countryName.setText(homeCountryName);
            mCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId));
            setHomeCityTime(mCalendar);
        } else {
            mCalendar = Calendar.getInstance();
            if (mPositionLocal == null) {
                homeName = LocalCity.getName();
                homeCountryName = LocalCity.getCountry();
            } else {
                homeName = mPositionLocal.getLocality();
                homeCountryName = mPositionLocal.getCountryName();
            }
            homeCityName.setText(homeName);
            countryName.setText(homeCountryName);
        }
    }

    public void setHomeCityTime(Calendar homeCityTime) {
        String am_pm = homeCityTime.get(Calendar.HOUR_OF_DAY) > 12 ? getString(R.string.time_able_morning) : getString(R.string.time_able_afternoon);
        String minute = homeCityTime.get(Calendar.MINUTE) >= 10 ? homeCityTime.get(Calendar.MINUTE) + "" : "0" + homeCityTime.get(Calendar.MINUTE);
        lunarHomeCityTime.setText(homeCityTime.get(Calendar.HOUR_OF_DAY) + ":" + minute + am_pm);
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

    @Subscribe
    public void onEvent(LocationChangedEvent locationChangedEvent) {
        this.location = locationChangedEvent.getLocation();
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
                setHomeCityTime(mCalendar);
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

    @Subscribe
    public void onEvent(final SolarConvertEvent event) {
        solar_harvest_status.post(new Runnable() {
            @Override
            public void run() {
                //                NOTICE: nevo solar adc threshold is 200，but lunar is 170
                if (event.getPv_adc() >= 170) {
                    solar_harvest_status.setText(R.string.lunar_home_clock_solar_harvest_charge);
                } else {
                    solar_harvest_status.setText(R.string.lunar_home_clock_solar_harvest_idle);
                }
            }
        });
    }


}
