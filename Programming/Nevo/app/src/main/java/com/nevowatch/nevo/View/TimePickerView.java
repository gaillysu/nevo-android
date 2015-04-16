package com.nevowatch.nevo.View;

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

import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.MyApplication;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.ble.util.Optional;

/**
 * TimePickerFragment is a dialog fragment which shows the goal of steps from 7000 to 300000 steps.
 */
public class TimePickerView extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerFragmentCallbacks mCallbacks;
    private TimePickerDialog mTimePickerDialog;
    private static final String PREF_KEY_ALARM = "alarm";
    private MainActivity mActivity;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String str = new String(TimePickerView.getAlarmFromPreference(getActivity()));
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
        mCallbacks = (TimePickerFragmentCallbacks) mActivity.getFragment(MyApplication.ALARMFRAGMENT);
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

        mCallbacks.setClockTime(hourStr.get() + ":" + minStr.get());
        TimePickerView.saveAlarmToPreference(getActivity(), hourStr.get() + ":" + minStr.get());
    }

    public static interface  TimePickerFragmentCallbacks {
        void setClockTime(String clockTime);
    }

    public static void saveAlarmToPreference(Context context, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(PREF_KEY_ALARM, value).apply();
    }

    public static String getAlarmFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_KEY_ALARM, "00:00");
    }
}
