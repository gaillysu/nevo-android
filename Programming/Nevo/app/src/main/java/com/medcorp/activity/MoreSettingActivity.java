package com.medcorp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.medcorp.R;
import com.medcorp.base.BaseActivity;
import com.medcorp.util.Preferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jason on 2016/12/14.
 */

public class MoreSettingActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.more_setting_select_unit_spinner)
    Spinner selectUnitSpinner;
    @Bind(R.id.more_setting_select_sync_time_spinner)
    Spinner selectPlaceSpinner;

    private List<String> unitList;
    private ArrayAdapter<String> unitAdapter;
    private List<String> placeList;
    private ArrayAdapter<String> placeAdapter;


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
        initData();
    }

    private void initData() {
        unitList = new ArrayList<>();
        placeList = new ArrayList<>();

        placeList.add(getString(R.string.more_setting_place_local));
        placeList.add(getString(R.string.more_setting_place_home));
        unitList.add(getString(R.string.user_select_metrics));
        unitList.add(getString(R.string.user_select_imperial));

        unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitList);
        placeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,placeList);
        placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectPlaceSpinner.setAdapter(placeAdapter);
        selectUnitSpinner.setAdapter(unitAdapter);
        selectUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Preferences.saveUnitSelect(MoreSettingActivity.this, false);
                } else {
                    Preferences.saveUnitSelect(MoreSettingActivity.this, true);

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectPlaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Preferences.savePlaceSelect(MoreSettingActivity.this, false);
                }else{
                    Preferences.savePlaceSelect(MoreSettingActivity.this, true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.more_setting_go_setting_goal)
    public void openSettingGoal() {
        startActivity(GoalsActivity.class);
    }
}
