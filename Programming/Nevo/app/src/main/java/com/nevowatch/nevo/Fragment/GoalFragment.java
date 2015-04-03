package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.Function.SaveData;
import com.nevowatch.nevo.R;

/**
 * Created by imaze on 4/1/15.
 */
public class GoalFragment extends Fragment implements View.OnClickListener{

    private GoalFragmentCallbacks mCallbacks;
    private TextView mStepsTextView;
    private ImageView mEditStepsImage;
    private Button mModarateButton;
    private Button mIntensiveButton;
    private Button mSportiveButton;
    private static final int MODERATE = 0;
    private static final int INTENSIVE = 1;
    private static final int SPORTIVE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.goal_fragment, container, false);
        mCallbacks.onSectionAttached(2);

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
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (GoalFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement GoalFragmentCallbacks.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mStepsTextView.setText(SaveData.getStepGoalFromPreference(getActivity()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public static interface GoalFragmentCallbacks {
        void onSectionAttached(int position);
        void showStep();
        void setStepMode(int mode);
    }

    public void setStep(String goal){
        mStepsTextView.setText(goal);
        SaveData.saveStepGoalToPreference(getActivity(), goal);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.steps_textView:
            case R.id.edit_steps_imageView:
                mCallbacks.showStep();
                break;
            case R.id.modarateButton:
                setSelectedButtonProperty(new Button[]{mModarateButton,mIntensiveButton,mSportiveButton},mModarateButton);
                mCallbacks.setStepMode(MODERATE);
                break;
            case R.id.intensiveButton:
                setSelectedButtonProperty(new Button[]{mModarateButton,mIntensiveButton,mSportiveButton},mIntensiveButton);
                mCallbacks.setStepMode(INTENSIVE);
                break;
            case R.id.sportiveButton:
                setSelectedButtonProperty(new Button[]{mModarateButton,mIntensiveButton,mSportiveButton},mSportiveButton);
                mCallbacks.setStepMode(SPORTIVE);
                break;
            default:
                break;
        }

    }

    public void setSelectedButtonProperty(Button[] v,Button l){
        for (int i = 0; i <v.length; i++) {
            v[i].setTextColor(0xff000000);
            v[i].setSelected(false);
            if (l.equals(v[i])) {
                v[i].setTextColor(0xffffffff);
                v[i].setSelected(true);
            }
        }
    }
}



