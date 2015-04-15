package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.MyApplication;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.FontManager;
import com.nevowatch.nevo.View.RoundProgressBar;
import com.nevowatch.nevo.View.StepPickerView;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.model.packet.DailyStepsNevoPacket;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.request.GetStepsGoalNevoRequest;

import java.util.Calendar;

/**
 * WelcomeFragment aims to display current time and steps how many you took.
 */
public class WelcomeFragment extends Fragment implements OnSyncControllerListener {

    private ImageView mHourImage, mMinImage;
    private RoundProgressBar mRoundProgressBar;
    private TextView mTextView;
    private static final String PREF_USER_HOUR_DEGREE = "hour_pointer_degree";
    private static final String PREF_USER_MINUTE_DEGREE = "minute_pointer_degree";
    private int mCurHour, mCurMin, mTempMin = -1;
    private static int mCurrentSteps = 0;
    private Handler  mUiHandler = new Handler(Looper.getMainLooper());
    private Runnable mTimerTask = new Runnable() {
        @Override
        public void run() {
            final Calendar mCalendar = Calendar.getInstance();
            mCurHour = mCalendar.get(Calendar.HOUR);
            mCurMin = mCalendar.get(Calendar.MINUTE);
            if (mCurMin != mTempMin) {
                setMin((float) (mCurMin * 6));
                setHour((float) ((mCurHour + mCurMin / 60.0) * 30));
                mTempMin = mCurMin;
            }
            mUiHandler.removeCallbacks(mTimerTask);
            mUiHandler.postDelayed(mTimerTask,60000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.welcome_fragment, container, false);

        mHourImage = (ImageView) rootView.findViewById(R.id.HomeClockHour);
        mMinImage = (ImageView) rootView.findViewById(R.id.HomeClockMinute);
        mRoundProgressBar = (RoundProgressBar) rootView.findViewById(R.id.roundProgressBar);
        mTextView = (TextView) rootView.findViewById(R.id.textView);

        View [] viewArray = new View []{
                rootView.findViewById(R.id.textView)
        };
        FontManager.changeFonts(viewArray,getActivity());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getSyncController().getStepsAndGoal();
        double tmp = Integer.parseInt(StepPickerView.getStepTextFromPreference(getActivity())) * 1.0;
        setProgressBar((int)((0/tmp)*100));
        String str = mCurrentSteps + "/" + StepPickerView.getStepTextFromPreference(getActivity());
        if (!MyApplication.getSyncController().isConnected())
            str = "- /" + StepPickerView.getStepTextFromPreference(getActivity());
        setText(str);
        mUiHandler.post(mTimerTask);
    }

    public void setHour(final float degree) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mHourImage.setRotation(degree);
            }
        });
    }

    public void setMin(final float degree) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mMinImage.setRotation(degree);
            }
        });
    }

    public void setText(final String str){
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(str);
            }
        });
    }

    public void setProgressBar(final int progress){
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mRoundProgressBar.setProgress(progress);
            }
        });
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

    @Override
    public void packetReceived(NevoPacket packet) {
        if((byte) GetStepsGoalNevoRequest.HEADER == packet.getHeader()) {
            DailyStepsNevoPacket steppacket = packet.newDailyStepsNevoPacket();
            int dailySteps = steppacket.getDailySteps();
            int dailyGoal = steppacket.getDailyStepsGoal();
            Log.i("MainActivity", "dailySteps = " + dailySteps + ",dailyGoal = " + dailyGoal);
            mCurrentSteps = dailySteps;
            setText(dailySteps + "/" + dailyGoal);
            setProgressBar((int) (100.0 * dailySteps / dailyGoal));
            StepPickerView.saveStepTextToPreference(getActivity(), "" + dailyGoal);
        }
    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        //DO NOTHING
    }
}
