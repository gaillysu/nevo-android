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
import com.nevowatch.nevo.R;

/**
 * TutorialFour
 */
public class TutorialFour extends Activity implements View.OnClickListener{

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
                //startActivity(new Intent(this, TutorialThree.class));
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

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mConnectButton.setClickable(true);
           /* if(MainActivity.getmSyncController().isConnected()){*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFinishButton.setVisibility(View.VISIBLE);
                    }
                });
/*            }else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFinishButton.setVisibility(View.INVISIBLE);
                    }
                });
            }*/
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
