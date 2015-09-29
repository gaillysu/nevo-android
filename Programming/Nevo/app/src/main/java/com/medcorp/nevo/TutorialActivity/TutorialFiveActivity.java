package com.medcorp.nevo.TutorialActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.medcorp.nevo.FontManager;
import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.controller.OnSyncControllerListener;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.util.Constants;

/**
 * TutorialFour
 */
public class TutorialFiveActivity extends Activity
        implements View.OnClickListener, OnSyncControllerListener{

    private Button mConnectButton;
    private ImageView mConnectImg;
    private Button mNextButton;
    private Animation animRotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_5);
        findViewById(R.id.t4_back_Button).setOnClickListener(this);
        mConnectButton = (Button) findViewById(R.id.t4_connect_Button);
        mConnectButton.setOnClickListener(this);
        mConnectImg = (ImageView) findViewById(R.id.t4_rotate_ImageView);
        mNextButton = (Button) findViewById(R.id.t4_next_Button);
        mNextButton.setOnClickListener(this);

        if(SyncController.Singleton.getInstance(this)!=null && SyncController.Singleton.getInstance(this).isConnected()){
            mConnectButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
            mConnectImg.setImageResource(R.drawable.success);
            mConnectImg.setBackgroundResource(R.color.transparent);
        }

        if(SyncController.Singleton.getInstance(this)!=null)
            SyncController.Singleton.getInstance(this).setSyncControllerListenser(this);

        View [] viewArray = new View []{
                findViewById(R.id.t4_back_Button),
                findViewById(R.id.t4_connectButton),
                findViewById(R.id.t4_connect_Button),
                findViewById(R.id.t4_placeConnect),
                findViewById(R.id.t4_next_Button)
        };
        FontManager.changeFonts(viewArray,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.t4_back_Button:
                startActivity(new Intent(this, TutorialFourActivity.class));
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                finish();
                break;
            case R.id.t4_next_Button:
                startActivity(new Intent(TutorialFiveActivity.this, TutorialTipsOneActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
                break;
            case R.id.t4_connect_Button:
                animRotate = AnimationUtils.loadAnimation(this, R.anim.roatate);
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
            if(SyncController.Singleton.getInstance(TutorialFiveActivity.this)!=null && !SyncController.Singleton.getInstance(TutorialFiveActivity.this).isConnected()){
                SyncController.Singleton.getInstance(TutorialFiveActivity.this).startConnect(true, TutorialFiveActivity.this);
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mConnectButton.setClickable(true);
            mConnectButton.setTextColor(getResources().getColor(R.color.customBlack));
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
        if(isConnected){
            mConnectImg.clearAnimation();
            mNextButton.setVisibility(View.VISIBLE);
            mConnectButton.setVisibility(View.INVISIBLE);
            mConnectButton.setClickable(false);
            mConnectImg.setImageResource(R.drawable.success);
            mConnectImg.setBackgroundResource(R.color.transparent);
        }
    }
    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

    }
}
