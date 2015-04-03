package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.R;

/**
 * Created by imaze on 4/1/15.
 */
public class GoalFragment extends Fragment implements View.OnClickListener{

    private GoalFragmentCallbacks mCallbacks;
    private TextView mStepsTextView;
    private ImageView mEditStepsImage;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.goal_fragment, container, false);
        mCallbacks.onSectionAttached(2);
        mStepsTextView = (TextView) rootView.findViewById(R.id.steps_textView);
        mStepsTextView.setOnClickListener(this);
        mEditStepsImage = (ImageView) rootView.findViewById(R.id.edit_steps_imageView);
        mEditStepsImage.setOnClickListener(this);
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
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.steps_textView:
            case R.id.edit_steps_imageView:
                mCallbacks.showStep();
                break;
            default:
                break;
        }
    }

    public static interface GoalFragmentCallbacks {
        void onSectionAttached(int position);
        void showStep();
    }
}
