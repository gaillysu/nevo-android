package com.medcorp.activity.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.medcorp.activity.MainActivity;
import com.medcorp.activity.login.LoginActivity;
import com.medcorp.base.BaseActivity;
import com.medcorp.R;
import com.medcorp.util.Preferences;

import net.medcorp.library.ble.util.Constants;

import butterknife.ButterKnife;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPageVideoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG, true)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_page);
        ButterKnife.bind(this);

        if (!Preferences.getIsFirstLogin(this)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(TutorialPage1Activity.class);
                    finish();
                }
            }, 1500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(TutorialPageVideoActivity.this,LoginActivity.class);
                    intent.putExtra("isTutorialPage",true);
                    startActivity(intent);
                    finish();
                }
            }, 1500);
        }
    }
}
