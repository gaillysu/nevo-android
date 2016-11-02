package com.medcorp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.medcorp.R;
import com.medcorp.activity.EditWorldClockActivity;
import com.medcorp.fragment.base.BaseObservableFragment;
import com.medcorp.util.Preferences;

import net.medcorp.library.worldclock.City;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
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

    //    other city info
    @Bind(R.id.world_clock_fragment_show_other_city_tv)
    TextView showOtherCityNameAndTime;
    @Bind(R.id.show_other_city_sunrise_tv)
    TextView showOtherSunrise;
    @Bind(R.id.show_other_city_sunset_tv)
    TextView showOtherCitySunset;
    @Bind(R.id.show_other_city_info_ground)
    LinearLayout showOtherCityGround;

    private Realm realm = Realm.getDefaultInstance();
    private RealmResults<City> cities;
    private City city;
    public static String FORMAT_LONG_TIME = "yyyy-MM-dd HH:mm:ss";
    private String otherCityName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sunrise_sunset_activity, container, false);
        ButterKnife.bind(this, view);
        cities = realm.where(City.class).findAll();
        otherCityName = Preferences.getSaveOtherCityName(WorldClockFragment.this.getContext());
        setHasOptionsMenu(true);
        refreshClock();
        initView();
        return view;
    }

    private void initView() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = format.format(date);
        String[] currentTimeArray = currentTime.split("-");
        showLocationDate.setText(currentTimeArray[2] + "," + new SimpleDateFormat("MMM").format(date) + "," + currentTimeArray[0]);

        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();
        String locationCityName = timeZone.getID().split("/")[1].replace("_", " ");
        format = new SimpleDateFormat(FORMAT_LONG_TIME);
        String[] localTimeStr = format.format(calendar.getTime()).split(" ");

        if (new Integer(localTimeStr[1].split(":")[0]).intValue() <= 12) {
            showLocationCityInfo.setText(locationCityName + ": " + localTimeStr[1].split(":")[0] + ":" + localTimeStr[1].split(":")[1] + " AM");
        } else {
            showLocationCityInfo.setText(locationCityName + ": " + localTimeStr[1].split(":")[0] + ":" + localTimeStr[1].split(":")[1] + " PM");
        }
        for (City city : cities) {
            if (city.getName().equals(locationCityName)) {
                this.city = city;
            }
        }

        //set location sunrise sunset
        setSunriseAndSunset(showSunriseTv, showSunsetTv, city, timeZone.getID());

        // set other city sunrise sunset
        if (otherCityName != null) {
            for (City city : cities) {
                if ((city.getName() + ", " + city.getCountry()).equals(otherCityName)) {
                    showOtherCityNameAndTime.setText(city.getName());
                    net.medcorp.library.worldclock.TimeZone zone = city.getTimezoneRef();
                    setSunriseAndSunset(showOtherSunrise, showOtherCitySunset, city, zone.getName());
                }
            }
        } else {
            showOtherCityGround.setVisibility(View.GONE);
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

    //set sunrise and sunset value
    public void setSunriseAndSunset(TextView sunrise, TextView sunset, City city, String zone) {

        com.luckycatlabs.sunrisesunset.dto.Location sunriseLocation =
                new com.luckycatlabs.sunrisesunset.dto.Location(city.getLat() + "", city.getLng() + "");

        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(sunriseLocation, zone);
        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        sunrise.setText(officialSunrise);
        sunset.setText(officialSunset);
    }

    private void refreshClock() {
        final Calendar mCalendar = Calendar.getInstance();
        int mCurHour = mCalendar.get(Calendar.HOUR);
        int mCurMin = mCalendar.get(Calendar.MINUTE);
        minuteClock.setRotation((float) (mCurMin * 6));
        hourClock.setRotation((float) ((mCurHour + mCurMin / 60.0) * 30));
    }

}
