package com.medcorp.activity.tutorial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.R;
import com.medcorp.activity.MainActivity;
import com.medcorp.base.BaseActivity;
import com.medcorp.model.Goal;

import net.medcorp.library.ble.util.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Karl on 1/19/16.
 */
public class TutorialPageSuccessActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_page_success);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);
        SharedPreferences.Editor sharedPreferences = getSharedPreferences(Constants.PREF_NAME, 0).edit();
        sharedPreferences.putBoolean(Constants.FIRST_FLAG, false);
        sharedPreferences.commit();
        if(!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(getString(R.string.key_preset),false))
        {
            getModel().addGoal(new Goal(getString(R.string.startup_goal_light), true, 7000));
            getModel().addGoal(new Goal(getString(R.string.startup_goal_moderate), true, 10000));
            getModel().addGoal(new Goal(getString(R.string.startup_goal_heavy), true, 20000));
            //getModel().addAlarm(new Alarm(8, 0, (byte) 0, getString(R.string.startup_goal_weekly_days),(byte)0,(byte)0));
            //getModel().addAlarm(new Alarm(9, 0, (byte) 0, getString(R.string.startup_goal_weekend),(byte)0,(byte)0));
            sharedPreferences.putBoolean(getString(R.string.key_preset), true);
            sharedPreferences.commit();
        }
    }

    @OnClick(R.id.activity_tutorial_success_next_button)
    public void nextClicked(){
        startActivity(MainActivity.class);
        finish();
    }
}
