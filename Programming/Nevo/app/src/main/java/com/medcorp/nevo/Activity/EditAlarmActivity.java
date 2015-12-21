package com.medcorp.nevo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.FrameLayout;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 21/12/15.
 */
public class EditAlarmActivity extends BaseActivity{

    @Bind(R.id.activity_alarm_fragment_layout)
    FrameLayout frameLayout;

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        Fragment fragment = EditAlarmFragment.instantiate(EditAlarmActivity.this, EditAlarmFragment.class.getName(),bundle);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.activity_alarm_fragment_layout, fragment)
                .addToBackStack(EditAlarmFragment.class.getName())
                .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
