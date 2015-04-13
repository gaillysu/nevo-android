package com.nevowatch.nevo.Tutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.MyApplication;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;

/**
 * TutorialFour
 */
public class TutorialFour extends Activity implements View.OnClickListener, OnSyncControllerListener{

    private Button mConnectButton;
    private ImageView mConnectImg;
    private Button mFinishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_4);
        findViewById(R.id.t4_back_Button).setOnClickListener(this);
        mConnectButton = (Button) findViewById(R.id.t4_connect_Button);
        mConnectButton.setOnClickListener(this);
        mConnectImg = (ImageView) findViewById(R.id.t4_rotate_ImageView);
        mFinishButton = (Button) findViewById(R.id.t4_finish_Button);
        mFinishButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.t4_back_Button:
                finish();
                break;
            case R.id.t4_finish_Button:
                Intent it = new Intent(this, MainActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
                break;
            case R.id.t4_connect_Button:
                final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.roatate);
                mConnectImg.startAnimation(animRotate);
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
                MyApplication.getSyncController().startConnect(true, TutorialFour.this);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mConnectButton.setClickable(true);
            if(MyApplication.getSyncController()!=null && MyApplication.getSyncController().isConnected()){
                mFinishButton.setVisibility(View.VISIBLE);
            }else {
                mFinishButton.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {

    }
}
