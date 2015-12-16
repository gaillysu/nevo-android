package com.medcorp.nevo.activity.old.tutorial;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.medcorp.nevo.R;

/**
 * Created by Karl on 10/13/15.
 */
public class TutorialSleepTrackingActivity extends FragmentActivity implements View.OnClickListener{

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sleeptracking_tutorial);
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(this);
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
