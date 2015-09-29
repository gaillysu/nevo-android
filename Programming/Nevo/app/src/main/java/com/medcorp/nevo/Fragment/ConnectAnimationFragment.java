package com.medcorp.nevo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.nevo.Activity.MainActivity;
import com.medcorp.nevo.R;
import com.medcorp.nevo.View.AlertDialogView;
import com.medcorp.nevo.ble.controller.OnSyncControllerListener;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.util.Constants;

/**
 * A Round Pointer Animation
 */
public class ConnectAnimationFragment extends Fragment implements View.OnClickListener, OnSyncControllerListener{


    public static final String CONNECTFRAGMENT = "ConnectAnimationFragment";
    public static final int CONNECTPOSITION = 10;
    private ImageView mConnectImage;
    private Button mConnectButton;
    private int mPostion;
    private String mTag;
    private Button mForgetButton;
    private TextView mHyperLink;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.connect_fragment, container, false);
        mPostion = getArguments().getInt("position");
        mTag = getArguments().getString("tag");

        mConnectImage = (ImageView) rootView.findViewById(R.id.connect_imageView);
        mConnectButton = (Button)rootView.findViewById(R.id.connect_imageButton);
        mConnectButton.setOnClickListener(this);

        mForgetButton = (Button) rootView.findViewById(R.id.forget_device_button);
        mForgetButton.setOnClickListener(this);
        mForgetButton.setVisibility(View.VISIBLE);

        mHyperLink = (TextView) rootView.findViewById(R.id.link_textView);
        mHyperLink.setText( Html.fromHtml("<a href=\"http://nevowatch.com/blehelp\">Nevo Support</a>"));
        mHyperLink.setMovementMethod(LinkMovementMethod.getInstance());
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connect_imageButton:
                mConnectButton.setTextColor(getResources().getColor(R.color.customGray));
                mConnectButton.setClickable(false);
                final Animation animRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.roatate);
                mConnectImage.startAnimation(animRotate);
                animRotate.setAnimationListener(new myAnimationListener());
                break;
            case R.id.forget_device_button:
                SyncController.Singleton.getInstance(getActivity()).forgetDevice();
                Log.d("ConnectAnimationFragemt", "Forget Device Address");
                break;
            default:
                break;
        }
    }

    public class myAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {
            if(SyncController.Singleton.getInstance(getActivity())!=null && !SyncController.Singleton.getInstance(getActivity()).isConnected()){
                SyncController.Singleton.getInstance(getActivity()).startConnect(false, (OnSyncControllerListener)getActivity());
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
           //     showAlertDialog();
                mConnectButton.setClickable(true);
                mConnectButton.setTextColor(getResources().getColor(R.color.customBlack));
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
        if (isConnected)((MainActivity)getActivity()).replaceFragment(mPostion, mTag);
    }
    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

    }
}
