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
import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.MyApplication;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.AlertDialogView;
import com.nevowatch.nevo.View.FontManager;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;

/**
 * A Round Pointer Animation
 */
public class ConnectAnimationFragment extends Fragment implements View.OnClickListener, OnSyncControllerListener{

    private ImageView mConnectImage;
    private Button mConnectButton;
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

        View [] viewArray = new View []{
                rootView.findViewById(R.id.nevoConnectedText),
                rootView.findViewById(R.id.connect_imageButton),
                rootView.findViewById(R.id.pushConnectedText)
        };
        FontManager.changeFonts(viewArray, getActivity());

        return rootView;
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
                MyApplication.getSyncController().startConnect(true, (OnSyncControllerListener)getActivity());
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(MyApplication.getSyncController()!=null && MyApplication.getSyncController().isConnected()){
            //DO NOTHING, @see function connectionStateChanged
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
        if (isConnected) ((MainActivity)getActivity()).replaceFragment(mPostion, mTag);
    }
}
