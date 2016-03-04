package com.medcorp.nevo.activity;

import android.os.Bundle;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaillysu on 15/12/28.
 */
public class OpenSleepTrackActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page_2);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.activity_tutorial_sleep_close_button)
    public void closeButtonClicked(){
        finish();
    }
}
