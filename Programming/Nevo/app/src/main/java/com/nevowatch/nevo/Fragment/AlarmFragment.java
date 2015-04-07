package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nevowatch.nevo.Function.SaveData;
import com.nevowatch.nevo.R;


/**
 * Created by imaze on 4/1/15.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener{

    private AlarmFragmentCallbacks mCallbacks;
    private TextView mClockTextView;
    private String mClockStr;
    private ImageButton mImageButton;
    private Button mOnButton;
    private Button mOffButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        if(savedInstanceState != null){
            mClockStr = savedInstanceState.getString("ClockStr");
            mCallbacks.setClockTime(mClockStr);
            Log.d("AlarmFragment", "create");
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.alarm_fragment, container, false);
       // mCallbacks.onSectionAttached(3);

        mClockTextView = (TextView) rootView.findViewById(R.id.clock_textView);
        mClockTextView.setOnClickListener(this);
        mImageButton = (ImageButton) rootView.findViewById(R.id.edit_clock_imageButton);
        mImageButton.setOnClickListener(this);
        mOnButton =  (Button)rootView.findViewById(R.id.on_mode_button);
        mOnButton.setOnClickListener(this);
        mOffButton =  (Button)rootView.findViewById(R.id.off_mode_button);
        mOffButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mClockStr = mClockTextView.getText().toString();
        outState.putString("ClockStr", mClockStr);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (AlarmFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AlarmFragmentCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallbacks.onSectionAttached(3);
        mClockTextView.setText(SaveData.getAlarmFromPreference(getActivity()));
        if(SaveData.getClockStateFromPreference(getActivity())){
            onClick(mOnButton);
        }else{
            onClick(mOffButton);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_clock_imageButton:
            case R.id.clock_textView:
                mCallbacks.showTime();
                break;
            case R.id.on_mode_button:
                mOffButton.setTextColor(0xff000000);
                mOnButton.setTextColor(0xffffffff);
                mOffButton.setSelected(false);
                mOnButton.setSelected(true);
                SaveData.saveClockStateToPreference(getActivity(), true);
                break;
            case R.id.off_mode_button:
                mOffButton.setTextColor(0xffffffff);
                mOnButton.setTextColor(0xff000000);
                mOffButton.setSelected(true);
                mOnButton.setSelected(false);
                SaveData.saveClockStateToPreference(getActivity(), false);
                break;
            default:
                break;
        }

    }

    public void setClock(String time){
        mClockTextView.setText(time);
        SaveData.saveAlarmToPreference(getActivity(), time);
    }

    public static interface  AlarmFragmentCallbacks {
        void onSectionAttached(int position);
        void showTime();
        void setClockTime(String clockTime);
    }
}
