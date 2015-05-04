package com.nevowatch.nevo.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.View.RoundProgressBar;
import com.nevowatch.nevo.View.StepPickerView;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.DailyStepsNevoPacket;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.request.GetStepsGoalNevoRequest;

import java.util.Calendar;

/**
 * WelcomeFragment aims to display current time and steps how many you took.
 */
public class WelcomeFragment extends Fragment implements OnSyncControllerListener {


    public static final String WELCOMEFRAGMENT = "WelcomeFragment";
    private static final String PREF_CUR_STEP = "currentStep";

    private ImageView mHourImage, mMinImage;
    private RoundProgressBar mRoundProgressBar;
    private TextView mTextView;
    private int mCurHour, mCurMin, mTempMin = -1;
    private Handler  mUiHandler = new Handler(Looper.getMainLooper());
    private Runnable mTimerTask = new Runnable() {
        @Override
        public void run() {
            refreshTime();
            mUiHandler.removeCallbacks(mTimerTask);
            mUiHandler.postDelayed(mTimerTask,10000);
            if (SyncController.Singleton.getInstance(getActivity()).isConnected())
                SyncController.Singleton.getInstance(getActivity()).getStepsAndGoal();
        }
    };

    private void refreshTime(){
        final Calendar mCalendar = Calendar.getInstance();
        mCurHour = mCalendar.get(Calendar.HOUR);
        mCurMin = mCalendar.get(Calendar.MINUTE);
        if (mCurMin != mTempMin) {
            setMin((float) (mCurMin * 6));
            setHour((float) ((mCurHour + mCurMin / 60.0) * 30));
            mTempMin = mCurMin;
        }
    }
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
        //only connected nevo ,can send this cmd, due to send cmd add a timeout feature
        //when app start,syncController is connecting, send this cmd, will lead to  timeout
        // and kill service, auto reconnect nevo after 10s, user can't accept waiting 10s
        if (SyncController.Singleton.getInstance(getActivity()).isConnected()){
            initLayout(true);
        }else {
            initLayout(false);
        }
        refreshTime();
        mUiHandler.post(mTimerTask);
    }

    private void initLayout(boolean connected){
        if(connected){
            setText(getCurStepFromPreference(getActivity()) + "/" + StepPickerView.getStepTextFromPreference(getActivity()));
            setProgressBar((int)(100.0*getCurStepFromPreference(getActivity()) / Integer.parseInt(StepPickerView.getStepTextFromPreference(getActivity()))));
            SyncController.Singleton.getInstance(getActivity()).getStepsAndGoal();
        }else {
            setText("-/" + StepPickerView.getStepTextFromPreference(getActivity()));
            setProgressBar(0);
        }
    }

    public void setHour(final float degree) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mHourImage.setRotation(degree);
                if (mHourImage.getVisibility()==View.GONE){
                    mHourImage.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    public void setMin(final float degree) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mMinImage.setRotation(degree);
                if (mMinImage.getVisibility()==View.GONE){
                    mMinImage.setVisibility(View.VISIBLE);
                }
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

    public static void saveCurStepToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(PREF_CUR_STEP, value).apply();
    }

    public static int getCurStepFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(PREF_CUR_STEP, 0);
    }

    @Override
    public void packetReceived(NevoPacket packet) {
        if((byte) GetStepsGoalNevoRequest.HEADER == packet.getHeader()) {
            DailyStepsNevoPacket steppacket = packet.newDailyStepsNevoPacket();
            int dailySteps = steppacket.getDailySteps();
            int dailyGoal = steppacket.getDailyStepsGoal();
            Log.i("MainActivity", "dailySteps = " + dailySteps + ",dailyGoal = " + dailyGoal);
            saveCurStepToPreference(getActivity(), dailySteps);
            setText(dailySteps + "/" + dailyGoal);
            setProgressBar((int) (100.0 * dailySteps / dailyGoal));
            StepPickerView.saveStepTextToPreference(getActivity(), "" + dailyGoal);
        }
    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        if(!isConnected)
            initLayout(false);
        ((MainActivity)getActivity()).replaceFragment(isConnected?0:10, isConnected?WelcomeFragment.WELCOMEFRAGMENT:ConnectAnimationFragment.CONNECTFRAGMENT);
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiHandler.removeCallbacks(mTimerTask);
    }
}
