package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.base.BaseFragment;

import java.util.Calendar;

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

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View clockFragmentContentView = inflater.inflate(R.layout.lunar_main_fragment_adapter_clock_layout,container,false);
        ButterKnife.bind(this, clockFragmentContentView);

        return clockFragmentContentView;
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

}
