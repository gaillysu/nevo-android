package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nevowatch.nevo.R;

/**
 * TutorialFragment, namely introduction pages.
 */
public class TutorialFragment extends Fragment {

    private ImageView mImageView, mStartImage;
    private TutorialFragmentCallbcaks mCallbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tutorial_fragment, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.tutorial_imageView);
        mStartImage = (ImageView) rootView.findViewById(R.id.tutoiral_startImage);

        mImageView.setImageResource(getArguments().getInt("drawableID"));
        if(getArguments().getInt("position") == 2){
            mStartImage.setVisibility(View.VISIBLE);
            mStartImage.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    mCallbacks.startMainActivity();
                }
            });
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (TutorialFragmentCallbcaks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement TutorialFragmentCallbcaks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public interface TutorialFragmentCallbcaks{
        void startMainActivity();
    }
}
