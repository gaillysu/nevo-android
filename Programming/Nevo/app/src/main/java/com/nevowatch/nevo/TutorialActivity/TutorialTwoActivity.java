package com.nevowatch.nevo.TutorialActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.R;

/**
 * TutorialTwoActivity
 */
public class TutorialTwoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity_2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.t1_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialTwoActivity.this, TutorialOneActivity.class));
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                finish();
            }
        });

        findViewById(R.id.t1_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialTwoActivity.this, TutorialThreeActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
            }
        });

        View [] viewArray = new View []{
                findViewById(R.id.t1_next_button),
                findViewById(R.id.t1_title),
                findViewById(R.id.t1_message),
                findViewById(R.id.t1_back_button)
        };
        FontManager.changeFonts(viewArray, this);
    }
}
