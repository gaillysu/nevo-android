package com.medcorp.nevo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/23.
 */
public class EditGoalsActivity extends BaseActivity  {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Goals");
        Bundle bundle = getIntent().getExtras();
        Fragment fragment = EditGoalsFragment.instantiate(EditGoalsActivity.this,EditGoalsFragment.class.getName(),bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.activity_goals_fragment_layout, fragment)
                .addToBackStack(EditAlarmFragment.class.getName())
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
