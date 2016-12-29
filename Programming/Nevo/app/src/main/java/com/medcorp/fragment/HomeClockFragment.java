package com.medcorp.fragment;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.medcorp.R;
import com.medcorp.activity.EditWorldClockActivity;
import com.medcorp.event.LocationChangedEvent;
import com.medcorp.event.bluetooth.HomeTimeEvent;
import com.medcorp.event.bluetooth.SunRiseAndSunSetWithZoneOffsetChangedEvent;
import com.medcorp.fragment.base.BaseObservableFragment;
import com.medcorp.util.Preferences;

import net.medcorp.library.worldclock.City;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jason on 2016/10/24.
 *
 */

public class HomeClockFragment extends BaseObservableFragment {

    @Bind(R.id.show_day_weekday_tv)
    TextView showLocationDate;
    @Bind(R.id.hour_clock_iv)
    ImageView hourClock;
    @Bind(R.id.minutes_clock_iv)
    ImageView minuteClock;
    @Bind(R.id.home_time_day_tv)
    TextView homeDay;
    @Bind(R.id.world_clock_fragment_show_location_city_tv)
    TextView showLocationCityInfo;

    private Realm realm = Realm.getDefaultInstance();
    private RealmResults<City> cities;
    private City locationCity;
    private String localCityName;
    private TimeZone timeZone;
    private String homeCityName;
    private String homeCountryName;
    private City mHomeCity;
    private Calendar mHomeCalendar;
    private Location location;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sunrise_sunset_activity, container, false);
        ButterKnife.bind(this, view);
        cities = realm.where(City.class).findAll();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        refreshClock();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView() {
        homeCityName = Preferences.getPositionCity(HomeClockFragment.this.getContext());
        homeCountryName = Preferences.getPositionCountry(HomeClockFragment.this.getContext());

        Calendar calendar = Calendar.getInstance();
        timeZone = calendar.getTimeZone();

        Address positionLocal = getModel().getPositionLocal(location);
        if (positionLocal != null) {
            localCityName = positionLocal.getLocality() + ", " + positionLocal.getCountryName();
        } else {
            localCityName = timeZone.getID().split("/")[1].replace("_", " ");
            for (City city : cities) {
                if (city.getName().equals(localCityName)) {
                    this.locationCity = city;
                    setSunriseAndSunset(locationCity, timeZone.getID());
                }
            }
        }

        if (homeCityName != null) {
            showLocationCityInfo.setText(homeCityName + "," + homeCountryName);
            mHomeCalendar = Calendar.getInstance(TimeZone.getTimeZone(Preferences.
                    getHomeTimezoneId(HomeClockFragment.this.getActivity())));
        } else {
            mHomeCalendar = calendar;
            showLocationCityInfo.setText(localCityName);
            mHomeCity = locationCity;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_goal_menu:
                startActivity(EditWorldClockActivity.class);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    //set sunrise and sunset value
    public void setSunriseAndSunset(City city, String zone) {
        com.luckycatlabs.sunrisesunset.dto.Location sunriseLocation =
                new com.luckycatlabs.sunrisesunset.dto.Location(city.getLat() + "", city.getLng() + "");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(sunriseLocation, zone);
        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());

        byte sunriseHour = (byte) Integer.parseInt(officialSunrise.split(":")[0]);
        byte sunriseMin = (byte) Integer.parseInt(officialSunrise.split(":")[1]);
        byte sunsetHour = (byte) Integer.parseInt(officialSunset.split(":")[0]);
        byte sunsetMin = (byte) Integer.parseInt(officialSunset.split(":")[1]);
        byte timeZoneOffset = (byte) (Calendar.getInstance().getTimeZone().getRawOffset() / 3600 / 1000);
        EventBus.getDefault().post(new SunRiseAndSunSetWithZoneOffsetChangedEvent(timeZoneOffset, sunriseHour, sunriseMin, sunsetHour, sunsetMin));

    }

    private void refreshClock() {
        setHomeDay(mHomeCalendar);
        int mCurHour = mHomeCalendar.get(Calendar.HOUR);
        int mCurMin = mHomeCalendar.get(Calendar.MINUTE);
        minuteClock.setRotation((float) (mCurMin * 6));
        hourClock.setRotation((float) ((mCurHour + mCurMin / 60.0) * 30));

        showLocationDate.setText(mHomeCalendar.get(Calendar.DAY_OF_MONTH) + " "
                + new SimpleDateFormat("MMM").format(mHomeCalendar.getTime()) + " ,"
                + mHomeCalendar.get(Calendar.YEAR));

        syncWatch(mHomeCalendar);
    }


    private void syncWatch(Calendar mCalendar) {

        byte sunriseHour = (byte) mCalendar.get(Calendar.HOUR);
        byte sunriseMin = (byte) mCalendar.get(Calendar.MINUTE);
        EventBus.getDefault().post(new HomeTimeEvent(sunriseHour, sunriseMin));
    }

    @Subscribe
    public void onEvent(LocationChangedEvent locationChangedEvent) {
        this.location = locationChangedEvent.getLocation();
    }

    public void setHomeDay(Calendar mCalendar) {
        Calendar localCalendar = Calendar.getInstance();
        int localDayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH);
        int homeDayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        if (localDayOfMonth == homeDayOfMonth) {
            homeDay.setText(R.string.sunset_activity_title_tv);
        } else {
            int timeDifference = homeDayOfMonth - localDayOfMonth;
            if (timeDifference == 1) {
                homeDay.setText(R.string.sunset_activity_title_tomorrow);
            } else if (timeDifference == -1) {
                homeDay.setText(R.string.sunset_activity_title_yesterday);
            } else if (timeDifference > 1) {
                homeDay.setText(R.string.sunset_activity_title_yesterday);
            } else if (timeDifference < -1) {
                homeDay.setText(R.string.sunset_activity_title_tomorrow);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
