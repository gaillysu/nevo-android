package com.nevowatch.nevo.Tutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.nevowatch.nevo.R;

/**
 * Tutorial Three
 */
public class TutorialThree extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_3);

        findViewById(R.id.t3_back_button).setOnClickListener(this);
        findViewById(R.id.t3_next_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.t3_back_button:
                finish();
                break;
            case R.id.t3_next_button:
                startActivity(new Intent(this, TutorialFour.class));
                break;
            default:
                break;
        }
    }
}
