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
import com.medcorp.event.bluetooth.LittleSyncEvent;
import com.medcorp.fragment.base.BaseFragment;
import com.medcorp.model.Steps;
import com.medcorp.model.User;
import com.medcorp.util.Common;
import com.medcorp.util.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/19.
 */
public class ClockFragment extends BaseFragment {

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
    private final int REFRESHINTERVAL = 10000;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private Runnable refreshTimerTask = new Runnable() {
        @Override
        public void run() {
            refresh();
        }
    };

    private void refresh() {
        final Calendar mCalendar = Calendar.getInstance();
        int mCurHour = mCalendar.get(Calendar.HOUR);
        int mCurMin = mCalendar.get(Calendar.MINUTE);
        setMin((float) (mCurMin * 6));
        setHour((float) ((mCurHour + mCurMin / 60.0) * 30));
        //realtime sync for current steps and goal
        getModel().getSyncController().getStepsAndGoal();
        mUiHandler.postDelayed(refreshTimerTask, REFRESHINTERVAL);
    }

    private void setHour(final float degree) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                hourImage.setRotation(degree);
            }
        });
    }

    private void setMin(final float degree) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                minImage.setRotation(degree);
            }
        });
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
        initData(userSelectDate);
        return clockFragmentContentView;
    }

    public void initData(Date date) {
        User user = getModel().getNevoUser();
        Steps steps = getModel().getDailySteps(user.getNevoUserID(), date);
        showUserActivityTime.setText(steps.getWalkDuration() != 0 ? formatTime(steps.getWalkDuration()) : 0 + "");
        showUserStepsDistance.setText(steps.getWalkDistance() != 0 ? steps.getWalkDistance() + "km" : 0 + "");
        showUserSteps.setText(steps.getSteps() + "");
        showUserCosumeCalories.setText(steps.getCalories() + "");
    }

    private String formatTime(int walkDuration) {
        StringBuffer activityTime = new StringBuffer();
        if (walkDuration >= 60) {
            if (walkDuration % 60 > 0) {
                activityTime.append(walkDuration % 60 + "h");
                activityTime.append(walkDuration - (walkDuration % 60 * 60) + "m");
            }
        } else {
            activityTime.append(walkDuration + "m");
        }

        return activityTime.toString();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiHandler.removeCallbacks(refreshTimerTask);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
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
            int dailySteps = steps.getSteps();
            int dailyGoal = steps.getGoal();
            Log.i("ClockFragment", "dailySteps = " + dailySteps + ",dailyGoal = " + dailyGoal);
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
}