package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.nevowatch.nevo.MyApplication;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.AlertDialogView;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;

/**
 * A Round Pointer Animation
 */
public class ConnectAnimationFragment extends Fragment implements View.OnClickListener, OnSyncControllerListener{

    private ImageView mConnectImage;
    private Button mConnectButton;
    private ConnectAnimationFragmentCallbacks mCallbacks;
    private int mPostion;
    private String mTag;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.connect_fragment, container, false);
        mPostion = getArguments().getInt("position");
        mTag = getArguments().getString("tag");

        mConnectImage = (ImageView) rootView.findViewById(R.id.connect_imageView);
        mConnectButton = (Button)rootView.findViewById(R.id.connect_imageButton);
        mConnectButton.setOnClickListener(this);
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
            throw new ClassCastException("Activity must implement ConnectAnimationFragmentCallbacks.");
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
            case R.id.connect_imageButton:
                final Animation animRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.roatate);
                mConnectImage.startAnimation(animRotate);
                animRotate.setAnimationListener(new myAnimationListener());
                mConnectButton.setTextColor(getResources().getColor(R.color.customGray));
                mConnectButton.setClickable(false);
                break;
            default:
                break;
        }
    }

    public class myAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {
            if(MyApplication.getSyncController()!=null && !MyApplication.getSyncController().isConnected()){
                MyApplication.getSyncController().startConnect(true, ConnectAnimationFragment.this);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(MyApplication.getSyncController()!=null && MyApplication.getSyncController().isConnected()){
                mCallbacks.replaceFragment(mPostion, mTag);
            }else {
                //showAlertDialog();
                mConnectButton.setClickable(true);
                mConnectButton.setTextColor(getResources().getColor(R.color.customBlack));
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public interface ConnectAnimationFragmentCallbacks{
        void onSectionAttached(int i);
        void replaceFragment(final int position, final String tag);
    }

    /**
     * Pop-up window showing waring messages which means "Nevo Watch Not Found"
     * */
    public void showAlertDialog(){
        DialogFragment newFragment = new AlertDialogView();
        newFragment.show(getActivity().getSupportFragmentManager(), "warning");
    }

    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {

    }
}
