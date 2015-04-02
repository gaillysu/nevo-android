package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by imaze on 15/4/2.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TimePickerFragmentCallbacks mCallbacks;
    private TimePickerDialog mTimePickerDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int mintue = c.get(Calendar.MINUTE);
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

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String hourStr = null , minStr = null;
        if(hourOfDay < 9){
            hourStr = "0" + hourOfDay;
        }else {
            hourStr = new Integer(hourOfDay).toString();
        }
        if(minute < 9){
            minStr = "0" + minute;
        }else {
            minStr = new Integer(minute).toString();
        }

        mCallbacks.setClockTime(hourStr + ":" + minStr);
    }

    public static interface  TimePickerFragmentCallbacks {
        void setClockTime(String clockTime);
    }
}
