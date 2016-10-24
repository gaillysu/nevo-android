package com.medcorp.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.medcorp.R;
import com.medcorp.base.BaseActivity;

import org.joda.time.DateTime;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/10/24.
 */

public class SunActivity extends BaseActivity {

    @Bind(R.id.show_day_weekday_tv)
    TextView showLocationDate;
    @Bind(R.id.hour_clock_iv)
    TextView hourClock;
    @Bind(R.id.minutes_clock_iv)
    ImageView minuteClock;
    @Bind(R.id.sunset_activity_location_city_name_tv)
    TextView showLocationCityName;
    @Bind(R.id.show_today_sunrise_tv)
    TextView showSunriseTv;
    @Bind(R.id.show_today_sunset_tv)
    TextView showSunsetTv;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.sunrise_sunset_activity);
        ButterKnife.bind(this);
        refreshClock();
        initView();
    }

    private void initView() {
        DateTime dateTime = new DateTime();
        StringBuffer buffer = new StringBuffer();
        buffer.append(dateTime.dayOfWeek() + ",");
        buffer.append(dateTime.dayOfMonth() + ",");
        buffer.append(dateTime.getMonthOfYear() + ",");
        buffer.append(dateTime.year());
        showLocationDate.setText(buffer.toString());

        android.location.Location location = getLocation(SunActivity.this);
        com.luckycatlabs.sunrisesunset.dto.Location sunriseLocation =
                new com.luckycatlabs.sunrisesunset.dto.Location(location.getLongitude() + "", location.getLatitude() + "");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(sunriseLocation, dateTime.getZone().toTimeZone());
        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        //        Calendar officialSunset = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());

    }

    @SuppressWarnings("static-access")
    private Location getLocation(SunActivity context) {
        //You do not instantiate this class directly;
        //instead, retrieve it through:
        //Context.getSystemService(Context.LOCATION_SERVICE).
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //获取GPS支持
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        android.location.Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            //获取NETWORK支持
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    private void refreshClock() {
        final Calendar mCalendar = Calendar.getInstance();
        int mCurHour = mCalendar.get(Calendar.HOUR);
        int mCurMin = mCalendar.get(Calendar.MINUTE);
        minuteClock.setRotation((float) (mCurMin * 6));
        hourClock.setRotation((float) ((mCurHour + mCurMin / 60.0) * 30));
    }

}
