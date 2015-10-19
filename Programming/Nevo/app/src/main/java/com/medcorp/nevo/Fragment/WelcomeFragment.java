package com.medcorp.nevo.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.packet.DailyStepsNevoPacket;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.request.GetStepsGoalNevoRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.view.RoundProgressBar;
import com.medcorp.nevo.view.StepPickerView;

import java.util.Calendar;

/**
 * WelcomeFragment aims to display current time and steps how many you took.
 */
public class WelcomeFragment extends BaseFragment {


    public static final String WELCOMEFRAGMENT = "WelcomeFragment";
    public static final int WELPOSITION = 0;
    private static final String PREF_CUR_STEP = "currentStep";
    private ImageView mHourImage, mMinImage;
    private RoundProgressBar mRoundProgressBar;
    private TextView mTextView;
    private int mCurHour, mCurMin, mTempMin = -1;
    private long mLastTapTime = 0;
    private ImageView mClockView;
    private static boolean mIsVisible;
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
        mIsVisible = true;
        mHourImage = (ImageView) rootView.findViewById(R.id.HomeClockHour);
        mMinImage = (ImageView) rootView.findViewById(R.id.HomeClockMinute);
        mRoundProgressBar = (RoundProgressBar) rootView.findViewById(R.id.roundProgressBar);
        mTextView = (TextView) rootView.findViewById(R.id.textView);
        mClockView = (ImageView)rootView.findViewById(R.id.clock_imageView);
        mClockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //double click clock within 2s, light on nevo all color LED
                if ((System.currentTimeMillis() - mLastTapTime) > 2000)
                    mLastTapTime = System.currentTimeMillis();
                else {
                    if (SyncController.Singleton.getInstance(getActivity()).isConnected()) {
                        SyncController.Singleton.getInstance(getActivity()).findDevice();
                    }
                }
            }
        });
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
        mUiHandler.postDelayed(mTimerTask,10000);
    }

    private void initLayout(boolean connected){
        if(connected){
            setText(getCurStepFromPreference(getActivity()) + "/" + StepPickerView.getStepTextFromPreference(getActivity()));
            setProgressBar((int)(100.0*getCurStepFromPreference(getActivity()) / Integer.parseInt(StepPickerView.getStepTextFromPreference(getActivity()))));
            //SyncController.Singleton.getInstance(getActivity()).getStepsAndGoal();
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
        //double click get response within 2s, blink clock image once
        //use 2s, get rid of notification's response
        else if(((byte)0xF0 == packet.getHeader() && (System.currentTimeMillis() - mLastTapTime) < 2000)
                || ((byte)0xF1 == packet.getHeader() && (byte)0x02 == packet.getPackets().get(0).getRawData()[2]))
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mIsVisible) mClockView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.clockview600_color));
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //perhaps 2s later, the fragment got destory!!!!
                            if (mIsVisible && getActivity()!=null) mClockView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.clockview600));
                        }
                    }, 3000);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mUiHandler.removeCallbacks(mTimerTask);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsVisible = false;
    }

    @Override
    public void notifyDatasetChanged() {
        //TODO Delete packet received method and get it from the database.
    }

    @Override
    public void notifyOnConnected() {
        ((MainActivity)getActivity()).replaceFragment(WelcomeFragment.WELPOSITION, WelcomeFragment.WELCOMEFRAGMENT);
    }

    @Override
    public void notifyOnDisconnected() {
            initLayout(false);
        ((MainActivity)getActivity()).replaceFragment(ConnectAnimationFragment.CONNECTPOSITION, ConnectAnimationFragment.CONNECTFRAGMENT);
    }
}
