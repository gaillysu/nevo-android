package com.medcorp.nevo.activity.tutorial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.Preset;

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
        if(!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean("preset",false))
        {
            getModel().addPreset(new Preset("light", true, 7000));
            getModel().addPreset(new Preset("moderate", false, 10000));
            getModel().addPreset(new Preset("highly", false, 20000));
            getModel().addAlarm(new Alarm(8, 0, false, "wake up"));
            getModel().addAlarm(new Alarm(21, 0, false, "start sleep"));
            sharedPreferences.putBoolean("preset", true);
            sharedPreferences.commit();
        }
    }

    @OnClick(R.id.next_button)
    public void nextClicked(){
        startActivity(MainActivity.class);
        finish();
    }
}
