package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;

import com.nevowatch.nevo.R;


/**
 * Created by imaze on 4/1/15.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener{

    private AlarmFragmentCallbacks mCallbacks;
    private TextView mClockTextView;
    private String mClockSr;
    private ImageButton mImageButton;
    private Button onButton;
    private Button offButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.alarm_fragment, container, false);
        mCallbacks.onSectionAttached(3);

        mClockTextView = (TextView) rootView.findViewById(R.id.clock_textView);
        mClockTextView.setOnClickListener(this);
        mImageButton = (ImageButton) rootView.findViewById(R.id.edit_clock_imageButton);
        mImageButton.setOnClickListener(this);
        onButton =  (Button)rootView.findViewById(R.id.on_mode_button);
        onButton.setOnClickListener(this);
        offButton =  (Button)rootView.findViewById(R.id.off_mode_button);
        offButton.setOnClickListener(this);

    /*    if(savedInstanceState != null){
            mCallbacks.setClockTime(mClockSr);
        }*/

        return rootView;
    }

/*
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mClockSr = mClockTextView.getText().toString();
    }
*/

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_clock_imageButton:
            case R.id.clock_textView:
                mCallbacks.showTime();
                break;
            case R.id.on_mode_button:
                Log.d("onButton","on");
                offButton.setTextColor(0xff000000);
                onButton.setTextColor(0xffffffff);
                offButton.setSelected(false);
                onButton.setSelected(true);
                break;
            case R.id.off_mode_button:
                Log.d("offButton","off");
                offButton.setTextColor(0xffffffff);
                onButton.setTextColor(0xff000000);
                offButton.setSelected(true);
                onButton.setSelected(false);
                break;
            default:
                break;
        }

    }

    public void setClock(String time){
        mClockTextView.setText(time);
    }

    public static interface  AlarmFragmentCallbacks {
        void onSectionAttached(int position);
        void showTime();
        void setClockTime(String clockTime);
    }
}
