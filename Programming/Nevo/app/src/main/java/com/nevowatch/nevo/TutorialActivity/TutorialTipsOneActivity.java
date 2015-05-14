package com.nevowatch.nevo.TutorialActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.R;

/**
 * TutorialTipsOneActivity
 */
public class TutorialTipsOneActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_tips_one_activity);

        findViewById(R.id.t1_tips_backButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialTipsOneActivity.this, TutorialFiveActivity.class));
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                finish();
            }
        });

        findViewById(R.id.t1_tips_nextButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialTipsOneActivity.this, TutorialTipsTwoActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

        View [] viewArray = new View []{
                findViewById(R.id.t1_tips_nextButton),
                findViewById(R.id.t1_tips_backButton),
                findViewById(R.id.t1_tips_message),
                findViewById(R.id.t1_tips_title)
        };
        FontManager.changeFonts(viewArray, this);
    }
}
