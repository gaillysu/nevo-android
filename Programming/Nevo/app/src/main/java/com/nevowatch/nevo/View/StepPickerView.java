package com.nevowatch.nevo.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.NumberPicker;

import com.nevowatch.nevo.Fragment.GoalFragment;
import com.nevowatch.nevo.R;

/**
 * StepPickerFragment is a dialog fragment which shows the hour and minute of time in the whole day.
 */
public class StepPickerView extends DialogFragment{

    private StepPickerFragmentCallbacks mCallbacks;
    private NumberPicker mNumberPicker;
    private final static int NUMBER_OF_VALUES = 30;
    private final static int VALUES_INTERVAL = 1000;
    private String[] mDisplayedValues;
    private static final String PREF_KEY_STEP_TEXT = "stepText";
    private static final String PREF_KEY_STEP_Goal = "stepGoal";
    private static final int MODERATE = 0;
    private static final int INTENSIVE = 1;
    private static final int SPORTIVE = 2;
    private static final int CUSTOM = -1;

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
                                mCallbacks.setStepText(new Integer(temp).toString());
                                if(temp == 7000){
                                    mCallbacks.setStepGoal(MODERATE);
                                    GoalFragment.saveGoalModeToPreference(getActivity(), MODERATE);
                                }else if(temp == 10000){
                                    mCallbacks.setStepGoal(INTENSIVE);
                                    GoalFragment.saveGoalModeToPreference(getActivity(), INTENSIVE);
                                }else if(temp == 20000){
                                    mCallbacks.setStepGoal(SPORTIVE);
                                    GoalFragment.saveGoalModeToPreference(getActivity(), SPORTIVE);
                                }else {
                                    mCallbacks.setStepGoal(CUSTOM);
                                    GoalFragment.saveGoalModeToPreference(getActivity(), CUSTOM);
                                }
                                StepPickerView.saveStepTextToPreference(getActivity(), new Integer(temp).toString());
                            }
                        }
                )
                .setView(mNumberPicker)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        mNumberPicker.setValue(Integer.parseInt(StepPickerView.getStepTextFromPreference(getActivity())) / 1000 - 1);
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
        void setStepText(String stepGoal);
        void setStepGoal(int mode);
    }

    public static void saveStepTextToPreference(Context context, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(PREF_KEY_STEP_TEXT, value).apply();
    }

    public static String getStepTextFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_KEY_STEP_TEXT, "5000");
    }
}