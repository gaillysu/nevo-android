package com.medcorp.nevo.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.history.database.DatabaseHelper;
import com.medcorp.nevo.history.database.IDailyHistory;
import com.medcorp.nevo.activity.HistoryActivity;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.tutorial.TutorialSleepTrackingActivity;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.packet.DailyTrackerInfoNevoPacket;
import com.medcorp.nevo.ble.model.packet.DailyTrackerNevoPacket;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.request.ReadDailyTrackerInfoNevoRequest;
import com.medcorp.nevo.ble.model.request.ReadDailyTrackerNevoRequest;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.view.SleepDataView;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * WelcomeFragment aims to display current time and steps how many you took.
 */
public class SleepHistoryFragment extends Fragment implements OnSyncControllerListener, View.OnClickListener {


    public static final String SLEEPHISTORYFRAGMENT = "SleepHistoryFragment";
    public static final int SLEEPHISTORYPOSITION = 2;
    private static final String PREF_CUR_STEP = "currentStep";
    private ImageView mHourImage, mMinImage;
    private SleepDataView mRoundProgressBar;
    private TextView mTextView;
    private int mCurHour, mCurMin, mTempMin = -1;
    private long mLastTapTime = 0;
    private ImageView mClockView;
    private static boolean mIsVisible;
    private final String FIRST_TIME_KEY = "sleep_history_first_time_key";;
    private Context mCtx;
    JSONObject sleepAnalysisResult;
    private int TotalHistory;
    private int currentHistory;
    private boolean syncAllFlag = true;
    private long startsleep;
    private long endsleep;

    private Handler  mUiHandler = new Handler(Looper.getMainLooper());
    private Runnable mTimerTask = new Runnable() {
        @Override
        public void run() {
            refreshTime();
            mUiHandler.removeCallbacks(mTimerTask);
            mUiHandler.postDelayed(mTimerTask,10000);
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
        View rootView = inflater.inflate(R.layout.sleephistory_fragment, container, false);
        mCtx = getActivity();
        if (this.firstTimeFragment()){
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mCtx).edit();
            editor.putBoolean(FIRST_TIME_KEY,false);
            editor.commit();
            startSleepTrackingTutorial();
        }
        mIsVisible = true;
        mHourImage = (ImageView) rootView.findViewById(R.id.HomeClockHour);
        mMinImage = (ImageView) rootView.findViewById(R.id.HomeClockMinute);
        mRoundProgressBar = (SleepDataView) rootView.findViewById(R.id.SleepDataViewBar);
        mTextView = (TextView) rootView.findViewById(R.id.textView);
        mClockView = (ImageView)rootView.findViewById(R.id.clock_imageView);
        rootView.findViewById(R.id.sleeptracking_tutorial_button).setOnClickListener(this);
        mClockView.setOnClickListener(this);

        sleepAnalysisResult = DatabaseHelper.getInstance(mCtx).getSleepZone(new Date());
        try {
            startsleep = sleepAnalysisResult.getLong("startDateTime");
            endsleep = sleepAnalysisResult.getLong("endDateTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView  history = (ImageView)rootView.findViewById(R.id.btnhistory);
        history.setOnClickListener(this);
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
            //if no today's sleep data, sync it now.
            if(startsleep == 0 || endsleep ==0 || startsleep==endsleep)
            {
                //blank database, sync all data up to last 7 days
                if (getDailyHistory(new Date()).isEmpty()) {
                    TotalHistory = 0;
                    currentHistory = 0;
                    syncAllFlag = true;
                    SyncController.Singleton.getInstance(getActivity()).getDailyTrackerInfo(syncAllFlag);
                } else //only sync current day
                {
                    TotalHistory = 1;
                    currentHistory = 0;
                    syncAllFlag = false;
                    SyncController.Singleton.getInstance(getActivity()).getDailyTrackerInfo(syncAllFlag);
                }
            }
        }else {
            initLayout(false);
        }
        refreshTime();
        mUiHandler.postDelayed(mTimerTask, 10000);
    }

    private void initLayout(boolean connected){
        if(connected){
            Date startDate = new Date(startsleep);
            Date endDate = new Date(endsleep);
            long total = (endDate.getTime() - startDate.getTime())/1000/60;
            if(total == 0) setText("----");
            else setText(""+ (total>=60?(total/60 + "h "):"") + (total%60) + "min");
            setProgressBar();
            //SyncController.Singleton.getInstance(getActivity()).getStepsAndGoal();
        }else {
            setText("----");
            setProgressBar();
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

    public void setProgressBar(){
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mRoundProgressBar.setSleepAnalysisResult(sleepAnalysisResult);
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
        if((byte) ReadDailyTrackerInfoNevoRequest.HEADER == packet.getHeader())
        {
            DailyTrackerInfoNevoPacket infopacket = packet.newDailyTrackerInfoNevoPacket();
            TotalHistory = infopacket.getDailyTrackerInfo().size();
        }
        else if((byte) ReadDailyTrackerNevoRequest.HEADER == packet.getHeader()) {
            DailyTrackerNevoPacket thispacket = packet.newDailyTrackerNevoPacket();
            currentHistory++;
            if(currentHistory == TotalHistory)
            {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //refresh data after 1s for save local database done.
                        //showGraph();
                        sleepAnalysisResult = DatabaseHelper.getInstance(mCtx).getSleepZone(new Date());
                        initLayout(true);
                        //end refresh
                    }
                },1000);
            }
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
    public void connectionStateChanged(boolean isConnected) {
        if(!isConnected)
            initLayout(false);
        ((MainActivity)getActivity()).replaceFragment(isConnected? SleepHistoryFragment.SLEEPHISTORYPOSITION:ConnectAnimationFragment.CONNECTPOSITION, isConnected? SleepHistoryFragment.SLEEPHISTORYFRAGMENT:ConnectAnimationFragment.CONNECTFRAGMENT);
    }
    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

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

    /**
     * return one day's History
     * @param from
     * @return
     */
    List<IDailyHistory> getDailyHistory(Date from)
    {
        List<Long> days = new ArrayList<Long>();
        //set theDay from 00:00:00
        Calendar calBeginning = new GregorianCalendar();
        calBeginning.setTime(from);
        calBeginning.set(Calendar.HOUR_OF_DAY, 0);
        calBeginning.set(Calendar.MINUTE, 0);
        calBeginning.set(Calendar.SECOND, 0);
        calBeginning.set(Calendar.MILLISECOND, 0);
        Date theday = calBeginning.getTime();
        days.add(theday.getTime());
        try {
            return DatabaseHelper.getInstance(mCtx).getDailyHistoryDao().queryBuilder().orderBy("created", false).where().in("created",days).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<IDailyHistory>();
    }

    private boolean firstTimeFragment(){
        SharedPreferences preferences  = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getBoolean(FIRST_TIME_KEY,true);
    }

    private void startSleepTrackingTutorial(){
        Intent i = new Intent(mCtx, TutorialSleepTrackingActivity.class);
        getActivity().startActivity(i);
        getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sleeptracking_tutorial_button:
                startSleepTrackingTutorial();
                break;
            case R.id.clock_imageView:
                if ((System.currentTimeMillis() - mLastTapTime) > 2000)
                    mLastTapTime = System.currentTimeMillis();
                else {
                    if (SyncController.Singleton.getInstance(getActivity()).isConnected()) {
                        SyncController.Singleton.getInstance(getActivity()).findDevice();
                    }
                }
                break;
            case R.id.btnhistory:
                Intent intent = new Intent(mCtx, HistoryActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

}
