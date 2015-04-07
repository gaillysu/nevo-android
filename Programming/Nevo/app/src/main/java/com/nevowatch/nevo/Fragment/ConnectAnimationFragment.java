package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.nevowatch.nevo.Function.SaveData;
import com.nevowatch.nevo.R;

/**
 * A Round Pointer Animation
 */
public class ConnectAnimationFragment extends Fragment implements View.OnClickListener{

    private ImageView mConnectImage;
    private ConnectAnimationFragmentCallbacks mCallbacks;
    private int mPostion;
    private String mTag;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.connect_fragment, container, false);
        mPostion = getArguments().getInt("position");
        mTag = getArguments().getString("tag");

        mConnectImage = (ImageView) rootView.findViewById(R.id.connect_imageView);
        mConnectImage.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallbacks.onSectionAttached(mPostion+1);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (ConnectAnimationFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ConnectAnimationFragment.");
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
            case R.id.connect_imageView:
                final Animation animRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.roatate);
                mConnectImage.startAnimation(animRotate);
                animRotate.setAnimationListener(new myAnimationListener());
                break;
            default:
                break;
        }
    }

    public class myAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(SaveData.getBleConnectFromPreference(getActivity())){
                mCallbacks.replaceFragment(mPostion, mTag);
            }else{
                mCallbacks.showWarning();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public interface ConnectAnimationFragmentCallbacks{
        void onSectionAttached(int i);
        void replaceFragment(final int position, final String tag);
        void showWarning();
    }
}
