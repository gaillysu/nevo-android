package com.medcorp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.base.BaseActivity;
import com.medcorp.util.Preferences;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jason on 2016/12/14.
 */

public class MoreSettingActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.more_select_unit_imperial)
    TextView imperialUnit;
    @Bind(R.id.more_select_unit_metrics)
    TextView metricsUnit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_setting_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.settings_more));
        setTitle(R.string.edit_notification_item_name);

        if (!Preferences.getUnitSelect(this)) {
            selectMetrics();
        } else {
            selectImperial();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.more_select_unit_metrics)
    public void selectUnitMetrics() {
        Preferences.saveUnitSelect(this, false);
        selectMetrics();
    }

    @OnClick(R.id.more_select_unit_imperial)
    public void selectUnitImperial() {
        Preferences.saveUnitSelect(this, true);
        selectImperial();
    }

    public void selectMetrics() {
        metricsUnit.setTextColor(getResources().getColor(R.color.more_setting_text_color));
        imperialUnit.setTextColor(getResources().getColor(R.color.colorPrimary));
        metricsUnit.setBackground(getResources().getDrawable(R.drawable.more_setting_unit_select_shape));
        imperialUnit.setBackground(getResources().getDrawable(R.drawable.user_select_unit_imperial_def_shape));
    }

    public void selectImperial() {
        metricsUnit.setTextColor(getResources().getColor(R.color.colorPrimary));
        imperialUnit.setTextColor(getResources().getColor(R.color.more_setting_text_color));
        metricsUnit.setBackground(getResources().getDrawable(R.drawable.more_setting_unit_select_default_shape));
        imperialUnit.setBackground(getResources().getDrawable(R.drawable.user_select_unit_imperial_shape));
    }

    @OnClick(R.id.more_setting_go_setting_goal)
    public void openSettingGoal() {
        startActivity(GoalsActivity.class);
    }
}
