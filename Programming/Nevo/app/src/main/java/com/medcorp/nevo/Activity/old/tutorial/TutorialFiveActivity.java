package com.medcorp.nevo.activity.old.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.activity.observer.ActivityObservable;

/**
 * TutorialFour
 */
public class TutorialFiveActivity extends BaseActivity implements View.OnClickListener, ActivityObservable{

    private Button mConnectButton;
    private ImageView mConnectImg;
    private Button mNextButton;
    private Animation animRotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_5);
        getModel().observableActivity(this);
        findViewById(R.id.t4_back_Button).setOnClickListener(this);
        mConnectButton = (Button) findViewById(R.id.t4_connect_Button);
        mConnectButton.setOnClickListener(this);
        mConnectImg = (ImageView) findViewById(R.id.t4_rotate_ImageView);
        mNextButton = (Button) findViewById(R.id.t4_next_Button);
        mNextButton.setOnClickListener(this);

        if(getModel().isWatchConnected()){
            mConnectButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
            mConnectImg.setImageResource(R.drawable.success);
            mConnectImg.setBackgroundResource(R.color.transparent);
        }

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

    @Override
    public void notifyDatasetChanged() {

    }

    @Override
    public void notifyOnConnected() {
        mConnectImg.clearAnimation();
        mNextButton.setVisibility(View.VISIBLE);
        mConnectButton.setVisibility(View.INVISIBLE);
        mConnectButton.setClickable(false);
        mConnectImg.setImageResource(R.drawable.success);
        mConnectImg.setBackgroundResource(R.color.transparent);
    }

    @Override
    public void notifyOnDisconnected() {

    }
    @Override
    public void batteryInfoReceived(Battery battery) {

    }

    @Override
    public void findWatchSuccess() {

    }

    @Override
    public void onSearching() {

    }

    @Override
    public void onSearchSuccess() {

    }

    @Override
    public void onSearchFailure() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncEnd() {

    }

    public class myAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {
            getModel().startConnectToWatch(true);
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
 }
