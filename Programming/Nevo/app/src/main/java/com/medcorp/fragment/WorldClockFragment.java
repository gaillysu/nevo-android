package com.medcorp.fragment;

import android.os.Bundle;
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
import com.medcorp.event.bluetooth.SunRiseAndSunSetWithZoneOffsetChangedEvent;
import com.medcorp.fragment.base.BaseObservableFragment;
import com.medcorp.util.Preferences;

import net.medcorp.library.worldclock.City;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Jason on 2016/10/24.
 */

public class WorldClockFragment extends BaseObservableFragment {

    @Bind(R.id.show_day_weekday_tv)
    TextView showLocationDate;
    @Bind(R.id.hour_clock_iv)
    ImageView hourClock;
    @Bind(R.id.minutes_clock_iv)
    ImageView minuteClock;
    @Bind(R.id.show_today_sunrise_tv)
    TextView showSunriseTv;
    @Bind(R.id.show_today_sunset_tv)
    TextView showSunsetTv;
    @Bind(R.id.world_clock_fragment_show_location_city_tv)
    TextView showLocationCityInfo;

    private Realm realm = Realm.getDefaultInstance();
    private RealmResults<City> cities;
    private City locationCity;
    private String otherCityName;
    private TimeZone timeZone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sunrise_sunset_activity, container, false);
        ButterKnife.bind(this, view);
        cities = realm.where(City.class).findAll();
        setHasOptionsMenu(true);
        refreshClock();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView() {
        otherCityName = Preferences.getSaveOtherCityName(WorldClockFragment.this.getContext());
        Calendar calendar = Calendar.getInstance();
        timeZone = calendar.getTimeZone();
        int month = calendar.get(Calendar.MONTH) + 1;
        showLocationDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + " "
                + new SimpleDateFormat("MMM").format(calendar.getTime()) + " ,"
                + calendar.get(Calendar.YEAR));

        if (otherCityName == null) {
            otherCityName = timeZone.getID().split("/")[1].replace("_", " ");
            showLocationCityInfo.setText(otherCityName);
            for (City city : cities) {
                if (city.getName().equals(otherCityName)) {
                    this.locationCity = city;
                }
            }
            setSunriseAndSunset(showSunriseTv, showSunsetTv, locationCity, timeZone.getID());
        } else {
            for (City city : cities) {
                if ((city.getName() + ", " + city.getCountry()).equals(otherCityName)) {
                    showLocationCityInfo.setText(city.getName());
                    setSunriseAndSunset(showSunriseTv, showSunsetTv, city, city.getTimezoneRef().getName());
                }
            }
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.choose_other_city)
    public void openEditWorldClock() {
        startActivity(EditWorldClockActivity.class);
    }

    //set sunrise and sunset value
    public void setSunriseAndSunset(TextView sunrise, TextView sunset, City city, String zone) {

        com.luckycatlabs.sunrisesunset.dto.Location sunriseLocation =
                new com.luckycatlabs.sunrisesunset.dto.Location(city.getLat() + "", city.getLng() + "");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(sunriseLocation, zone);
        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        sunrise.setText(officialSunrise + " " + getString(R.string.time_able_morning));
        sunset.setText(officialSunset + " " + getString(R.string.time_able_afternoon));

        byte sunriseHour = (byte) Integer.parseInt(officialSunrise.split(":")[0]);
        byte sunriseMin = (byte) Integer.parseInt(officialSunrise.split(":")[1]);
        byte sunsetHour = (byte) Integer.parseInt(officialSunset.split(":")[0]);
        byte sunsetMin = (byte) Integer.parseInt(officialSunset.split(":")[1]);
        byte timeZoneOffset = (byte) (Calendar.getInstance().getTimeZone().getRawOffset() / 3600 / 1000);
        EventBus.getDefault().post(new SunRiseAndSunSetWithZoneOffsetChangedEvent(timeZoneOffset, sunriseHour, sunriseMin, sunsetHour, sunsetMin));
    }

    private void refreshClock() {
        final Calendar mCalendar = Calendar.getInstance();
        int mCurHour = mCalendar.get(Calendar.HOUR);
        int mCurMin = mCalendar.get(Calendar.MINUTE);
        minuteClock.setRotation((float) (mCurMin * 6));
        hourClock.setRotation((float) ((mCurHour + mCurMin / 60.0) * 30));
    }

}
