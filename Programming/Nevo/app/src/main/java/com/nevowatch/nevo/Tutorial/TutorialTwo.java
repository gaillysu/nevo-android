package com.nevowatch.nevo.Tutorial;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.R;

/**
 * Tutorial Two
 */
public class TutorialTwo extends Activity implements View.OnClickListener{

    private Button mT2NextButton;
    private TextView mBLEText;
    private ImageView mBLEStateImg;
    private BluetoothAdapter mBluetoothAdapter;
    private Boolean isFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_2);
        mT2NextButton = (Button) findViewById(R.id.t2_nextButton);
        mT2NextButton.setOnClickListener(this);
        findViewById(R.id.t2_backButton).setOnClickListener(this);
        mBLEText = (TextView) findViewById(R.id.bluetoothOffText);
        mBLEStateImg = (ImageView) findViewById(R.id.bluetoothState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBLEStateImg.setImageResource(R.drawable.bluetoothon_state_off);
                                mBLEText.setVisibility(View.VISIBLE);
                                mT2NextButton.setVisibility(View.INVISIBLE);
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
                startActivity(new Intent(this, TutorialThree.class));
                break;
            case R.id.t2_backButton:
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
