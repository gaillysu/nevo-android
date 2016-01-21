package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.medcorp.nevo.R;

import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.database.entry.SleepDatabaseHelper;
import com.medcorp.nevo.model.Sleep;
import com.medcorp.nevo.model.SleepData;
import com.medcorp.nevo.util.SleepDataHandler;
import com.medcorp.nevo.view.SleepDataView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.medcorp.nevo.fragment.base.BaseFragment;


/**
 * Created by Karl on 12/10/15.
 */
public class SleepTodayFragment extends BaseFragment {

    @Bind(R.id.sleepDataViewBar)
    SleepDataView  sleepDataViewBar;

    @Bind(R.id.fragment_sleep_today_sleepduration)
    TextView sleepDuration;

    @Bind(R.id.fragment_sleep_today_deep)
    TextView sleepDeepDuration;

    @Bind(R.id.fragment_sleep_today_light)
    TextView sleepLightDuration;

    @Bind(R.id.fragment_sleep_today_start)
    TextView sleepStart;

    @Bind(R.id.fragment_sleep_today_wake)
    TextView sleepEnd;

    @Bind(R.id.fragment_sleep_today_wakeduration)
    TextView sleepWakeDuration;

    @Bind(R.id.HomeClockHour)
    ImageView hourImage;

    @Bind(R.id.HomeClockMinute)
    ImageView minImage;

    private final int REFRESH_INTERVAL = 10000;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private Runnable refreshTimerTask = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };

    private void refresh(){
        final Calendar mCalendar = Calendar.getInstance();
        int mCurHour = mCalendar.get(Calendar.HOUR);
        int mCurMin = mCalendar.get(Calendar.MINUTE);
        setMin((float) (mCurMin * 6));
        setHour((float) ((mCurHour + mCurMin / 60.0) * 30));
        mUiHandler.postDelayed(refreshTimerTask, REFRESH_INTERVAL);
    }
    public void setHour(final float degree) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                hourImage.setRotation(degree);
            }
        });
    }

    public void setMin(final float degree) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                minImage.setRotation(degree);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_today, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
        JSONObject sleepAnalysisResult = new JSONObject();
        SleepDatabaseHelper helper = new SleepDatabaseHelper(getContext());
        List<Sleep> sleepList = new ArrayList<Sleep>();
        Date today = getModel().getDateFromDate(new Date());
        Date yesterday = new Date(today.getTime()-24*60*60*1000);
        Optional<Sleep> todaySleep = helper.get(0,today);
        Optional<Sleep> yesterdaySleep = helper.get(0,yesterday);
        if(yesterdaySleep.notEmpty())
        {
            sleepList.add(yesterdaySleep.get());
        }
        if(todaySleep.notEmpty())
        {
            sleepList.add(todaySleep.get());
        }
        SleepDataHandler handler = new SleepDataHandler(sleepList);
        List<SleepData> sleepDataList = handler.getSleepData();
        //have today sleep, use it.
        if(!sleepDataList.isEmpty() && todaySleep.notEmpty())
        {
            SleepData todaySleepData = sleepDataList.get(sleepDataList.size()-1);
            sleepAnalysisResult = todaySleepData.toJSONObject();
        }
        setProgressBar(sleepAnalysisResult);
        try {
            //TODO put into keys.xml
            setDashboard(new Dashboard(sleepAnalysisResult.getInt("sleepDuration")
                    ,sleepAnalysisResult.getInt("sleepDeepDuration")
                    ,sleepAnalysisResult.getInt("sleepLightDuration")
                    ,sleepAnalysisResult.getLong("startDateTime")
                    ,sleepAnalysisResult.getLong("endDateTime")
                    ,sleepAnalysisResult.getInt("sleepWakeDuration")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiHandler.removeCallbacks(refreshTimerTask);
    }
    public void setProgressBar(final JSONObject sleepAnalysisResult){
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                sleepDataViewBar.setSleepAnalysisResult(sleepAnalysisResult);
            }
        });
    }

    public void setDashboard(final Dashboard dashboard)
    {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                sleepDuration.setText(dashboard.formatDuration(dashboard.sleepDuration));
                sleepDeepDuration.setText(dashboard.formatDuration(dashboard.sleepDeepDuration));
                sleepLightDuration.setText(dashboard.formatDuration(dashboard.sleepLightDuration));
                sleepStart.setText(dashboard.formatTimeStamp(dashboard.sleepStart));
                sleepEnd.setText(dashboard.formatTimeStamp(dashboard.sleepEnd));
                sleepWakeDuration.setText(dashboard.formatDuration(dashboard.sleepWakeDuration));
            }
        });
    }
    class Dashboard{
        int sleepDuration;
        int sleepDeepDuration;
        int sleepLightDuration;
        long sleepStart;
        long sleepEnd;
        int sleepWakeDuration;

        Dashboard(int sleepDuration,int sleepDeepDuration,int sleepLightDuration,long sleepStart,long sleepEnd,int sleepWakeDuration)
        {
            this.sleepDuration = sleepDuration;
            this.sleepDeepDuration = sleepDeepDuration;
            this.sleepLightDuration = sleepLightDuration;
            this.sleepStart = sleepStart;
            this.sleepEnd = sleepEnd;
            this.sleepWakeDuration = sleepWakeDuration;
        }
        String formatDuration(int durationMinute)
        {
            return durationMinute/60 + "h" + durationMinute%60 + "m";
        }
        String formatTimeStamp(long timeStamp)
        {
            return new SimpleDateFormat("HH:mm").format(new Date(timeStamp));
        }
    }
}
