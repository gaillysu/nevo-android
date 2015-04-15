package com.nevowatch.nevo.TutorialActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.FontManager;

/**
 * Tutorial Three
 */
public class TutorialThreeActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_3);

        findViewById(R.id.t3_back_button).setOnClickListener(this);
        findViewById(R.id.t3_next_button).setOnClickListener(this);

        View [] viewArray = new View []{
                findViewById(R.id.t3_back_button),
                findViewById(R.id.t3_watchBluetooth),
                findViewById(R.id.t3_longPushLED),
                findViewById(R.id.t3_next_button)
        };
        FontManager.changeFonts(viewArray,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.t3_back_button:
                startActivity(new Intent(this, TutorialTwoActivity.class));
                overridePendingTransition(R.anim.back_enter, R.anim.back_exit);
                finish();
                break;
            case R.id.t3_next_button:
                startActivity(new Intent(this, TutorialFourActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
                break;
            default:
                break;
        }
    }
}
