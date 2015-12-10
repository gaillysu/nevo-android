package com.medcorp.nevo.activity.old.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.R;

/**
 * Tutorial Three
 */
public class TutorialFourActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_4);
        findViewById(R.id.t3_back_button).setOnClickListener(this);
        findViewById(R.id.t3_next_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.t3_back_button:
                startActivity(new Intent(this, TutorialThreeActivity.class));
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                finish();
                break;
            case R.id.t3_next_button:
                startActivity(new Intent(this, TutorialFiveActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
                break;
            default:
                break;
        }
    }

}
