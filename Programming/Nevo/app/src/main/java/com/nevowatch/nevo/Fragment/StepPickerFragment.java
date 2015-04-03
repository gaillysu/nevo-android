package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.nevowatch.nevo.R;

/**
 * Created by imaze on 15/4/2.
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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_picker_fragment, container, false);
        mNumberPicker = (NumberPicker) rootView.findViewById(R.id.step_number_picker);
        mDisplayedValues = new String[NUMBER_OF_VALUES];
        setDisplayedValues();
        mNumberPicker.setMaxValue(NUMBER_OF_VALUES - 1);
        mNumberPicker.setMinValue(0);
        mNumberPicker.setDisplayedValues(mDisplayedValues);
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
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

    }
}
