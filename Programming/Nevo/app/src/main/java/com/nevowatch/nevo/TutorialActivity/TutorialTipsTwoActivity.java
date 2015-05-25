package com.nevowatch.nevo.TutorialActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.ble.util.Constants;

/**
 * TutorialTipsTwoActivity
 */
public class TutorialTipsTwoActivity extends Activity {

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

        View [] viewArray = new View []{
                findViewById(R.id.t2_tips_backButton),
                findViewById(R.id.t2_tips_finish_Button),
                findViewById(R.id.t2_tips_message),
                findViewById(R.id.t2_tips_title)
        };
        FontManager.changeFonts(viewArray, this);
    }
}