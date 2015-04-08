package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.Function.SaveData;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.RoundProgressBar;

/**
 * WelcomeFragment aims to display current time and steps how many you took.
 */
public class WelcomeFragment extends Fragment{

    private ImageView mHourImage, mMinImage;
    private WelcomeFragmentCallbacks mCallbacks;
    private RoundProgressBar mRoundProgressBar;
    private TextView mTextView;

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
        setHour(SaveData.getHourDegreeFromPreference(getActivity()));
        setMin(SaveData.getMinDegreeFromPreference(getActivity()));
        double tmp = Integer.parseInt(SaveData.getStepGoalFromPreference(getActivity())) * 1.0;
        setProgressBar((int)((0/tmp)*100));
        String str =  "- / " + SaveData.getStepGoalFromPreference(getActivity());
        setText(str);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void setHour(float degree) {
        mHourImage.setRotation(degree);
    }

    public void setMin(float degree) {
        mMinImage.setRotation(degree);
    }

    public void setText(String str){
        mTextView.setText(str);
    }

    public void setProgressBar(int progress){
        mRoundProgressBar.setProgress(progress);
    }

    public static interface WelcomeFragmentCallbacks{
        void onSectionAttached(int position);
    }
}
