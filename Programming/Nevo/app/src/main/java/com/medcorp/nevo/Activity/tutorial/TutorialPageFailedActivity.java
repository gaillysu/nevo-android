package com.medcorp.nevo.activity.tutorial;

import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;

import butterknife.OnClick;

/**
 * Created by Karl on 1/19/16.
 */
public class TutorialPageFailedActivity extends BaseActivity{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_failed);

    }


    @OnClick(R.id.retry_button)
    public void retryClicked(){
        finish();
    }
}
