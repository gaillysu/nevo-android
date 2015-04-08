package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.nevowatch.nevo.Function.Optional;
import com.nevowatch.nevo.Function.SaveData;

/**
 * TimePickerFragment is a dialog fragment which shows the goal of steps from 7000 to 300000 steps.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerFragmentCallbacks mCallbacks;
    private TimePickerDialog mTimePickerDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String str = new String(SaveData.getAlarmFromPreference(getActivity()));
        String tmp[] = str.split(":");
        int hour = new Integer(tmp[0]).intValue();
        int mintue = new Integer(tmp[1]).intValue();
        mTimePickerDialog = new TimePickerDialog(getActivity(), this, hour, mintue,
                DateFormat.is24HourFormat(getActivity()));
        return mTimePickerDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (TimePickerFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement TimePickerFragmentCallbacks.");
        }
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

        mCallbacks.setClockTime(hourStr.get() + ":" + minStr.get(),SaveData.getClockStateFromPreference(getActivity()));
    }

    public static interface  TimePickerFragmentCallbacks {
        void setClockTime(String clockTime,boolean OnOff);
    }
}
