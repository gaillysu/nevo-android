package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.MyApplication;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.TimePickerView;


/**
 * AlarmFragment, it works for setting alarm and turning alarm on or off.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener{

    private AlarmFragmentCallbacks mCallbacks;
    private TextView mClockTextView;
    private ImageView mEditClockImage;
    private Button mOnButton;
    private Button mOffButton;
    private static final String PREF_KEY_CLOCK_STATE = "clockState";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.alarm_fragment, container, false);

        mClockTextView = (TextView) rootView.findViewById(R.id.clock_textView);
        mClockTextView.setOnClickListener(this);
        mEditClockImage = (ImageView) rootView.findViewById(R.id.edit_clock_imageButton);
        mEditClockImage.setOnClickListener(this);
        mOnButton =  (Button)rootView.findViewById(R.id.on_mode_button);
        mOnButton.setOnClickListener(this);
        mOffButton =  (Button)rootView.findViewById(R.id.off_mode_button);
        mOffButton.setOnClickListener(this);

        return rootView;
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
        mClockTextView.setText(TimePickerView.getAlarmFromPreference(getActivity()));
        if(AlarmFragment.getClockStateFromPreference(getActivity())){
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
                    mClockTextView.setClickable(false);
                    mEditClockImage.setClickable(false);
                    showTimePickerDialog();
                break;
            case R.id.on_mode_button:
                mOffButton.setTextColor(getResources().getColor(R.color.black));
                mOnButton.setTextColor(getResources().getColor(R.color.white));
                mOffButton.setSelected(false);
                mOnButton.setSelected(true);
                AlarmFragment.saveClockStateToPreference(getActivity(), true);
                String[] strAlarm = TimePickerView.getAlarmFromPreference(getActivity()).split(":");
                MyApplication.getSyncController().setAlarm(Integer.parseInt(strAlarm[0]),
                        Integer.parseInt(strAlarm[1]),
                        true);
                break;
            case R.id.off_mode_button:
                mOffButton.setTextColor(getResources().getColor(R.color.white));
                mOnButton.setTextColor(getResources().getColor(R.color.black));
                mOffButton.setSelected(true);
                mOnButton.setSelected(false);
                AlarmFragment.saveClockStateToPreference(getActivity(), false);
                String[] strAlarmOff = TimePickerView.getAlarmFromPreference(getActivity()).split(":");
                MyApplication.getSyncController().setAlarm(Integer.parseInt(strAlarmOff[0]),
                        Integer.parseInt(strAlarmOff[1]),
                        false);
                break;
            default:
                break;
        }

    }

    public void setClock(final String time){
        mClockTextView.setText(time);
    }

    /**
     * Show Time in a dialog
     * */
    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerView();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        mClockTextView.setClickable(true);
        mEditClockImage.setClickable(true);
    }

    public static interface AlarmFragmentCallbacks {
        void onSectionAttached(int position);
    }

    public static void saveClockStateToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(PREF_KEY_CLOCK_STATE, value).apply();
    }

    public static Boolean getClockStateFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_KEY_CLOCK_STATE, false);
    }
}
