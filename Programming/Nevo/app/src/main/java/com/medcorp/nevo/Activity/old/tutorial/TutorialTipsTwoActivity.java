package com.medcorp.nevo.activity.old.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.activity.old.OldMainActivity;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.util.Constants;

/**
 * TutorialTipsTwoActivity
 */
public class TutorialTipsTwoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_tips_two_activity);

        findViewById(R.id.t2_tips_backButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialTipsTwoActivity.this, TutorialTipsOneActivity.class));
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                finish();
            }
        });

        findViewById(R.id.t2_tips_finish_Button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(TutorialTipsTwoActivity.this, MainActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                getSharedPreferences(Constants.PREF_NAME, 0).edit().putBoolean(Constants.FIRST_FLAG,false).commit();
                finish();
            }
        });
    }
}
