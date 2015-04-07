package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.NumberPicker;

import com.nevowatch.nevo.Function.SaveData;
import com.nevowatch.nevo.R;

/**
 * StepPickerFragment is a dialog fragment which shows the hour and minute of time in the whole day.
 */
public class StepPickerFragment extends DialogFragment{

    private StepPickerFragmentCallbacks mCallbacks;
    private NumberPicker mNumberPicker;
    private final static int NUMBER_OF_VALUES = 30;
    private final static int VALUES_INTERVAL = 1000;
    private String[] mDisplayedValues;

    private void setDisplayedValues(){
        for(int i=0; i<NUMBER_OF_VALUES; i++)
            mDisplayedValues[i] = String.valueOf(VALUES_INTERVAL * (i+1));
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mNumberPicker = new NumberPicker(getActivity());
        mDisplayedValues = new String[NUMBER_OF_VALUES];
        setDisplayedValues();
        mNumberPicker.setMaxValue(NUMBER_OF_VALUES - 1);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setDisplayedValues(mDisplayedValues);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.step_picker_title)
                .setPositiveButton(R.string.ok_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                int temp = (mNumberPicker.getValue() + 1) * VALUES_INTERVAL;
                                mCallbacks.setStepGoal(new Integer(temp).toString());
                            }
                        }
                )
                .setView(mNumberPicker)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        mNumberPicker.setValue(Integer.parseInt(SaveData.getStepGoalFromPreference(getActivity())) / 1000 - 1);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (StepPickerFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement StepPickerFragmentCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public interface StepPickerFragmentCallbacks{
        void setStepGoal(String stepGoal);
    }
}
