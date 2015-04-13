package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.RoundProgressBar;
import com.nevowatch.nevo.View.StepPickerView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * WelcomeFragment aims to display current time and steps how many you took.
 */
public class WelcomeFragment extends Fragment{

    private ImageView mHourImage, mMinImage;
    private WelcomeFragmentCallbacks mCallbacks;
    private RoundProgressBar mRoundProgressBar;
    private TextView mTextView;
    private static final String PREF_USER_HOUR_DEGREE = "hour_pointer_degree";
    private static final String PREF_USER_MINUTE_DEGREE = "minute_pointer_degree";
    private int mCurHour, mCurMin, mTempMin = -1;
    private Timer mTimer = new Timer(true);
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            final Calendar mCalendar = Calendar.getInstance();
            mCurHour = mCalendar.get(Calendar.HOUR);
            mCurMin = mCalendar.get(Calendar.MINUTE);
            if(mCurMin != mTempMin) {
                setMin((float) (mCurMin * 6));
                setHour((float) ((mCurHour + mCurMin / 60.0) * 30));
                mTempMin = mCurMin;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.welcome_fragment, container, false);

        mHourImage = (ImageView) rootView.findViewById(R.id.HomeClockHour);
        mMinImage = (ImageView) rootView.findViewById(R.id.HomeClockMinute);
        mRoundProgressBar = (RoundProgressBar) rootView.findViewById(R.id.roundProgressBar);
        mTextView = (TextView) rootView.findViewById(R.id.textView);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (WelcomeFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement WelcomeFragmentCallbacks.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallbacks.onSectionAttached(1);
        setHour(WelcomeFragment.getHourDegreeFromPreference(getActivity()));
        setMin(WelcomeFragment.getMinDegreeFromPreference(getActivity()));
        double tmp = Integer.parseInt(StepPickerView.getStepGoalFromPreference(getActivity())) * 1.0;
        setProgressBar((int)((0/tmp)*100));
        String str =  "- / " + StepPickerView.getStepGoalFromPreference(getActivity());
        setText(str);
        mTimer.schedule(mTimerTask, 0 ,1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimer.cancel();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void setHour(final float degree) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHourImage.setRotation(degree);
            }
        });
    }

    public void setMin(final float degree) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMinImage.setRotation(degree);
            }
        });
    }

    public void setText(final String str){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(str);
            }
        });
    }

    public void setProgressBar(final int progress){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRoundProgressBar.setProgress(progress);
            }
        });
    }

    public static interface WelcomeFragmentCallbacks{
        void onSectionAttached(int position);
    }

    /**
     * Welcome Fragment saves hour and minute pointer degree
     * */
    public static Float getHourDegreeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getFloat(PREF_USER_HOUR_DEGREE, 0);
    }

    public static Float getMinDegreeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getFloat(PREF_USER_MINUTE_DEGREE, 0);
    }
}
