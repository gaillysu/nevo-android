package com.medcorp.nevo.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.adapter.MyNevoAdapter;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.model.MyNevo;
import com.medcorp.nevo.util.Common;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/28.
 */
public class OpenSleepTrackActivity extends BaseActivity{

    @Bind(R.id.nextButton)
    Button  nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page_2);
        ButterKnife.bind(this);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
