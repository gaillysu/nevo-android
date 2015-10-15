package com.medcorp.nevo;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.medcorp.nevo.Activity.BaseActivities.BaseFragmentActivity;

/**
 * Created by Karl on 10/13/15.
 */
public class SleepTrackingTutorial extends BaseFragmentActivity implements View.OnClickListener{

    private Button backButton;
    private TextView instructionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getModel();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sleeptracking_tutorial);
        instructionTextView = (TextView) findViewById(R.id.sleeptracking_textview);
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(this);
        FontManager.changeFonts(new View[]{
                backButton,
                instructionTextView
        },this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_button:
                finish();
                break;

        }
    }
}
