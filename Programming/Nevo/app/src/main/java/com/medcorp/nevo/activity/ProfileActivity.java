package com.medcorp.nevo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.fragment.ProfileFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by med on 16/4/6.
 */
public class ProfileActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.profile_title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = ProfileFragment.instantiate(this,ProfileFragment.class.getName());
        fragmentManager.beginTransaction().add(R.id.activity_profile_fragment_layout,
                fragment).addToBackStack(ProfileFragment.class.getName()).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
