package com.medcorp.nevo.activity.old.tutorial;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.R;

/**
 * Tutorial Two
 */
public class TutorialThreeActivity extends BaseActivity implements View.OnClickListener{

    private Button mT2NextButton;
    private TextView mBLEText;
    private TextView mBLEStatus;
    private ImageView mBLEStateImg;
    private BluetoothAdapter mBluetoothAdapter;
    private Boolean isFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_3);
        mT2NextButton = (Button) findViewById(R.id.t2_nextButton);
        mT2NextButton.setOnClickListener(this);
        findViewById(R.id.t2_backButton).setOnClickListener(this);
        mBLEText = (TextView) findViewById(R.id.bluetoothOffText);
        mBLEStateImg = (ImageView) findViewById(R.id.bluetoothState);
        mBLEStatus = (TextView) findViewById(R.id.t2_bluetoothEnabled);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        BLEState();
    }

    private  void BLEState(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinish){
                    if(mBluetoothAdapter.isEnabled()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBLEStateImg.setImageResource(R.drawable.bluetoothon_state_on);
                                mBLEText.setVisibility(View.INVISIBLE);
                                mT2NextButton.setVisibility(View.VISIBLE);
                                mBLEStatus.setText(R.string.bluetoothEnabled);
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBLEStateImg.setImageResource(R.drawable.bluetoothon_state_off);
                                mBLEText.setVisibility(View.VISIBLE);
                                mT2NextButton.setVisibility(View.INVISIBLE);
                                mBLEStatus.setText(R.string.enableBluetoothPhone);
                            }
                        });
                    }
                }
            }
        }).start();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.t2_nextButton:
                startActivity(new Intent(this, TutorialFourActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
                break;
            case R.id.t2_backButton:
                startActivity(new Intent(this, TutorialTwoActivity.class));
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFinish = true;
    }
}