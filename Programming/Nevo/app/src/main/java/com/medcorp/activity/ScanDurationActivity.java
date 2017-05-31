package com.medcorp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.adapter.ScanDurationAdapter;
import com.medcorp.base.BaseActivity;
import com.medcorp.model.ScanDurationItemModel;
import com.medcorp.util.Preferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2017/5/22.
 */

public class ScanDurationActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.setting_scan_duration_item)
    ListView allDurationList;
    @Bind(R.id.lunar_tool_bar_title)
    TextView title;
    private List<ScanDurationItemModel> list;
    private int[] mTime;
    private ScanDurationAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_duration_activity);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText(R.string.settings_bluetooth_scan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list = new ArrayList<>();
        mTime = getResources().getIntArray(R.array.scan_duration_time);
        initData();
        settingData();
    }

    private void settingData() {
        mAdapter = new ScanDurationAdapter(this, list);
        allDurationList.setAdapter(mAdapter);
        allDurationList.setOnItemClickListener(this);
    }

    private void initData() {
        int scanDuration = Preferences.getScanDuration(this);
        for (int i = 0; i < 9; i++) {
            ScanDurationItemModel model = new ScanDurationItemModel();
            if (scanDuration == -1) {
                if (mTime[i] == 15) {
                    model.setTime(mTime[i]);
                    model.setSelect(true);
                } else {
                    model.setTime(mTime[i]);
                    model.setSelect(false);
                }
            } else {
                if (mTime[i] == scanDuration) {
                    model.setTime(mTime[i]);
                    model.setSelect(true);
                } else {
                    model.setTime(mTime[i]);
                    model.setSelect(false);
                }
            }
            list.add(model);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Preferences.saveScanDuration(this,mTime[position]);
        list.clear();
        initData();
        mAdapter.notifyDataSetChanged();
    }
}
