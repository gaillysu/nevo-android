package com.medcorp.nevo.View;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.medcorp.nevo.Fragment.AlarmFragment;
import com.medcorp.nevo.Activity.MainActivity;
import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.util.Optional;

/**
 * TimePickerFragment is a dialog fragment which shows the goal of steps from 7000 to 300000 steps.
 */
public class TimePickerView extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerFragmentCallbacks mCallbacks;
    private TimePickerDialog mTimePickerDialog;
    private static final String PREF_KEY_ALARM = "alarm";
    private static final String PREF_KEY_ALARM2 = "alarm2";
    private static final String PREF_KEY_ALARM3 = "alarm3";
    private MainActivity mActivity;
    private int mAlarmIndex;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mAlarmIndex = getArguments().getInt("AlarmIndex",0);
        String str = new String(TimePickerView.getAlarmFromPreference(mAlarmIndex,getActivity()));
        String tmp[] = str.split(":");
        int hour = new Integer(tmp[0]).intValue();
        int mintue = new Integer(tmp[1]).intValue();
        mTimePickerDialog = new TimePickerDialog(getActivity(), this, hour, mintue,
                DateFormat.is24HourFormat(getActivity()));
        mTimePickerDialog.setTitle(R.string.time_picker);
        mTimePickerDialog.setCanceledOnTouchOutside(false);
        return mTimePickerDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity)getActivity();
        mCallbacks = (TimePickerFragmentCallbacks) mActivity.getFragment(AlarmFragment.ALARMFRAGMENT);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /*Time Format */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Optional<String> hourStr = new Optional<String>(null), minStr = new Optional<String>();
        if(hourOfDay <= 9){
            hourStr.set("0" + hourOfDay);
        }else {
            hourStr.set(new Integer(hourOfDay).toString());
        }
        if(minute <= 9){
            minStr.set("0" + minute);
        }else {
            minStr.set(new Integer(minute).toString());
        }
        TimePickerView.saveAlarmToPreference(mAlarmIndex,getActivity(), hourStr.get() + ":" + minStr.get());
        mCallbacks.setClockTime(mAlarmIndex,hourStr.get() + ":" + minStr.get());
    }

    public static interface  TimePickerFragmentCallbacks {
        void setClockTime(int index,String clockTime);
    }

    public static void saveAlarmToPreference(int index,Context context, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(index == 0) pref.edit().putString(PREF_KEY_ALARM, value).apply();
        if(index == 1) pref.edit().putString(PREF_KEY_ALARM2, value).apply();
        if(index == 2) pref.edit().putString(PREF_KEY_ALARM3, value).apply();
    }

    public static String getAlarmFromPreference(int index,Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(index == 0)  return pref.getString(PREF_KEY_ALARM, "00:00");
        if(index == 1)  return pref.getString(PREF_KEY_ALARM2, "00:00");
        if(index == 2)  return pref.getString(PREF_KEY_ALARM3, "00:00");
        else return "00:00";
    }
}
