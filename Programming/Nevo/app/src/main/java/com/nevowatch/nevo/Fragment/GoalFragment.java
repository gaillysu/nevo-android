package com.nevowatch.nevo.Fragment;

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

import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.View.StepPickerView;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.request.NumberOfStepsGoal;

/**
 * GoalFragment aims to set goals including Moderate, Intensive, Sportive and Custom
 */
public class GoalFragment extends Fragment implements View.OnClickListener,StepPickerView.StepPickerFragmentCallbacks,OnSyncControllerListener {


    public static final String GOALFRAGMENT = "GoalFragment";

    private TextView mStepsTextView;
    private ImageView mEditStepsImage;
    private Button mModarateButton;
    private Button mIntensiveButton;
    private Button mSportiveButton;
    private Button[] mButtonArray;
    private static final int MODERATE = 0;
    private static final int INTENSIVE = 1;
    private static final int SPORTIVE = 2;
    private static final int CUSTOM = -1;
    private static final String PREF_KEY_STEP_MODE = "stepMode";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.goal_fragment, container, false);

        mStepsTextView = (TextView) rootView.findViewById(R.id.steps_textView);
        mStepsTextView.setOnClickListener(this);
        mEditStepsImage = (ImageView) rootView.findViewById(R.id.edit_steps_imageView);
        mEditStepsImage.setOnClickListener(this);
        mModarateButton =  (Button)rootView.findViewById(R.id.modarateButton);
        mModarateButton.setOnClickListener(this);
        mIntensiveButton =  (Button)rootView.findViewById(R.id.intensiveButton);
        mIntensiveButton.setOnClickListener(this);
        mSportiveButton =  (Button)rootView.findViewById(R.id.sportiveButton);
        mSportiveButton.setOnClickListener(this);
        mButtonArray = new Button[]{
                mModarateButton,
                mIntensiveButton,
                mSportiveButton
        };

        View [] viewArray = new View []{
                rootView.findViewById(R.id.steps_textView),
                rootView.findViewById(R.id.goal_stepsUnit),
                rootView.findViewById(R.id.modarateButton),
                rootView.findViewById(R.id.intensiveButton),
                rootView.findViewById(R.id.sportiveButton)
        };
        FontManager.changeFonts(viewArray,getActivity());

        return rootView;
    }

    public void lightStepGoal(int mode){
        switch(mode){
            case MODERATE:
                setSelectedButtonProperty(mButtonArray,mModarateButton);
                break;
            case INTENSIVE:
                setSelectedButtonProperty(mButtonArray,mIntensiveButton);
                break;
            case SPORTIVE:
                setSelectedButtonProperty(mButtonArray,mSportiveButton);
                break;
            case CUSTOM:
                setSelectedButtonProperty(mButtonArray, new Button(getActivity()));
                break;
            default:
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mStepsTextView.setText(StepPickerView.getStepTextFromPreference(getActivity()));
        lightStepGoal(GoalFragment.getGoalModeFromPreference(getActivity()));
    }

    @Override
    public void setStepText(String stepGoal) {
        setStep(stepGoal);
    }

    @Override
    public void setStepGoal(int mode) {
        lightStepGoal(mode);
    }


    public void setStep(final String goal){
        mStepsTextView.setText(goal);
        StepPickerView.saveStepTextToPreference(getActivity(), goal);
        SyncController.Singleton.getInstance(getActivity()).setGoal(new NumberOfStepsGoal(Integer.parseInt(goal)));
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.steps_textView:
            case R.id.edit_steps_imageView:
                    mStepsTextView.setClickable(false);
                    mEditStepsImage.setClickable(false);
                    showStepPickerDialog();
                GoalFragment.saveGoalModeToPreference(getActivity(), CUSTOM);
                setSelectedButtonProperty(mButtonArray, new Button(getActivity()));
                break;
            case R.id.modarateButton:
                setSelectedButtonProperty(mButtonArray,mModarateButton);
                GoalFragment.saveGoalModeToPreference(getActivity(), MODERATE);
                setStep(new Integer(7000).toString());
                SyncController.Singleton.getInstance(getActivity()).setGoal(new NumberOfStepsGoal(7000));
                break;
            case R.id.intensiveButton:
                setSelectedButtonProperty(mButtonArray,mIntensiveButton);
                GoalFragment.saveGoalModeToPreference(getActivity(), INTENSIVE);
                setStep(new Integer(10000).toString());
                SyncController.Singleton.getInstance(getActivity()).setGoal(new NumberOfStepsGoal(10000));
                break;
            case R.id.sportiveButton:
                setSelectedButtonProperty(mButtonArray,mSportiveButton);
                GoalFragment.saveGoalModeToPreference(getActivity(), SPORTIVE);
                setStep(new Integer(20000).toString());
                SyncController.Singleton.getInstance(getActivity()).setGoal(new NumberOfStepsGoal(20000));
                break;
            default:
                break;
        }

    }

    /**
     * Show Step Goals in a dialog
     * */
    public void showStepPickerDialog(){
        DialogFragment newFragment = new StepPickerView();
        newFragment.show(getActivity().getSupportFragmentManager(), "stepPicker");
        mStepsTextView.setClickable(true);
        mEditStepsImage.setClickable(true);
    }

    /*Highlight the selected imageView*/
    public void setSelectedButtonProperty(Button[] v,Button l){
        for (int i = 0; i <v.length; i++) {
            v[i].setTextColor(getResources().getColor(R.color.black));
            v[i].setSelected(false);
            if (l.equals(v[i])) {
                v[i].setTextColor(getResources().getColor(R.color.white));
                v[i].setSelected(true);
            }
        }
    }

    public static void saveGoalModeToPreference(Context context, int value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(PREF_KEY_STEP_MODE, value).apply();
    }

    public static int getGoalModeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(PREF_KEY_STEP_MODE, CUSTOM);
    }
    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
       ((MainActivity)getActivity()).replaceFragment(isConnected?1:3, isConnected?GoalFragment.GOALFRAGMENT:ConnectAnimationFragment.CONNECTFRAGMENT);
    }
}