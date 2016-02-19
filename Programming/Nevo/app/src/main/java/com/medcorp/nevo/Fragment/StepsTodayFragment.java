package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.base.BaseFragment;
import com.medcorp.nevo.fragment.listener.OnStepsListener;
import com.medcorp.nevo.model.Goal;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.util.Common;
import com.medcorp.nevo.util.Preferences;
import com.medcorp.nevo.view.RoundProgressBar;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Karl on 12/10/15.
 */
public class StepsTodayFragment extends BaseFragment implements OnStepsListener {

    @Bind(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;

    @Bind(R.id.fragment_steps_today_goal)
    TextView goal;


    @Bind(R.id.fragment_steps_today_progress)
    TextView goal_progress;


    @Bind(R.id.fragment_steps_today_daily_steps)
    TextView dailysteps;

    @Bind(R.id.HomeClockHour)
    ImageView hourImage;

    @Bind(R.id.HomeClockMinute)
    ImageView minImage;

    private final int REFRESHINTERVAL = 10000;
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
    private void setProgressBar(final int progress){
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                roundProgressBar.setProgress(progress);
            }
        });
    }

    private void setDashboard(final Dashboard dashboard)
    {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                goal.setText(dashboard.formatSteps(dashboard.goal));
                goal_progress.setText(dashboard.progress + "%");
                dailysteps.setText(dashboard.formatSteps(dashboard.allsteps));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps_today, container, false);
        ButterKnife.bind(this, view);
        int dailySteps = 0;
        int dailyGoal =  7000;
        // default goal which comes from nevo, when run app firstly
        Steps steps =  getModel().getDailySteps(0, Common.removeTimeFromDate(new Date()));
        if(steps != null) {
             dailySteps = steps.getSteps();
             dailyGoal =  steps.getGoal();
        } else {
            Goal goal = getModel().getGoalById(Preferences.getPresetId(getContext()));
            if(goal !=null) {
                dailyGoal = goal.getSteps();
            }
        }
        setProgressBar((int) (100.0 * dailySteps / dailyGoal));
        setDashboard(new Dashboard(dailySteps, dailyGoal, (int) (100.0 * dailySteps / dailyGoal), steps.getDistance(), dailySteps, steps.getCalories()));
        return view;
    }

    @Override
    public void OnStepsChanged() {

        Steps steps =  getModel().getDailySteps(0, Common.removeTimeFromDate(new Date()));
        if(steps == null) {
            return;
        }
        int dailySteps = steps.getSteps();
        int dailyGoal =  steps.getGoal();
        Log.i("StepsTodayFragment", "dailySteps = " + dailySteps + ",dailyGoal = " + dailyGoal);
        setProgressBar((int) (100.0 * dailySteps / dailyGoal));
        setDashboard(new Dashboard(dailySteps, dailyGoal, (int) (100.0 * dailySteps / dailyGoal), steps.getDistance(), dailySteps, steps.getCalories()));
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiHandler.removeCallbacks(refreshTimerTask);
    }

    private class Dashboard{
        int steps;
        int goal;
        int progress;
        int distance;
        int allsteps;
        int calories;

        Dashboard(int steps,int goal,int progress,int distance,int allsteps,int calories)
        {
            this.steps = steps;
            this.goal = goal;
            this.progress = progress;
            this.distance = distance;
            this.allsteps = allsteps;
            this.calories = calories;
        }
        String formatSteps(int steps)
        {
            return steps + "";
        }
        String formatDistance(int distanceMeter)
        {
            return distanceMeter/1000 + "KM";
        }
        String formatConsume(int calories)
        {
            return calories/1000 + "KCal";
        }
    }
}
