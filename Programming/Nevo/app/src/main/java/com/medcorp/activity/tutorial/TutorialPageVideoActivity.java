package com.medcorp.activity.tutorial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.medcorp.ApplicationFlage;
import com.medcorp.activity.MainActivity;
import com.medcorp.base.BaseActivity;
import com.medcorp.R;

import net.medcorp.library.ble.util.Constants;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPageVideoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG,true)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome_page);
        ButterKnife.bind(this);

        if(ApplicationFlage.FLAGE == ApplicationFlage.Flage.LUNAR){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                  startActivity(TutorialPage1Activity.class);
                    finish();
                }
            },1000);
        }
    }

    @OnClick(R.id.activity_welcome_next_button)
    public void nextButtonClicked(){
        startActivity(TutorialPage1Activity.class);
        finish();
    }

    @OnClick(R.id.activity_welcome_take_tour_button)
    public void takeTourClicked(){
        Uri uri = Uri.parse(getString(R.string.video_url));
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.setDataAndType(uri , "video/*");
        startActivity(intent);
    }
}
