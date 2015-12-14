package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.view.RoundProgressBar;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by Karl on 12/10/15.
 */
public class StepsTodayFragment  extends Fragment{

    @Bind(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;

    @Bind(R.id.fragment_steps_today_goal)
    TextView goal;

    @Bind(R.id.fragment_steps_today_reach)
    TextView goal_reach;

    @Bind(R.id.fragment_steps_today_progress)
    TextView goal_progress;

    @Bind(R.id.fragment_steps_today_distance)
    TextView distance;

    @Bind(R.id.fragment_steps_today_dailysteps)
    TextView dailysteps;

    @Bind(R.id.fragment_steps_today_calories)
    TextView calories;

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
        ((ApplicationModel)getActivity().getApplication()).getSyncController().getStepsAndGoal();
        mUiHandler.postDelayed(refreshTimerTask,REFRESHINTERVAL);
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
        View view = inflater.inflate(R.layout.fragment_steps_today, container, false);
        ButterKnife.bind(this, view);
        refresh();
        return view;
    }

}
