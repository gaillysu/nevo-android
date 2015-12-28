package com.medcorp.nevo.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.medcorp.nevo.activity.DfuActivity;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.adapter.MyNevoAdapter;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.model.MyNevo;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/28.
 */
public class MyNevoActivity  extends BaseActivity implements ActivityObservable {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_mynevo_list_view)
    ListView myNevoListView;

    private MyNevo mynevo;
    private final int battery_level = 2; //default is 2,  value is [0,1,2]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mynevo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("My Nevo");
        String app_version = "";
        try {
            app_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mynevo = new MyNevo(getModel().getWatchFirmware(),getModel().getWatchSoftware(),app_version,battery_level,true);
        myNevoListView.setAdapter(new MyNevoAdapter(this,mynevo));
        myNevoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    Intent intent = new Intent(MyNevoActivity.this, DfuActivity.class);
                    MyNevoActivity.this.startActivity(intent);
                    MyNevoActivity.this.finish();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getModel().observableActivity(this);
        if (getModel().isWatchConnected()){
            getModel().getBatteryLevelOfWatch();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void notifyDatasetChanged() {

    }

    @Override
    public void notifyOnConnected() {

    }

    @Override
    public void notifyOnDisconnected() {

    }

    @Override
    public void batteryInfoReceived(Battery battery) {
        mynevo.setBattery_level((int)battery.getBatterylevel());
        myNevoListView.setAdapter(new MyNevoAdapter(this,mynevo));
    }

    @Override
    public void findWatchSuccess() {

    }

    @Override
    public void onSearching() {

    }

    @Override
    public void onSearchSuccess() {

    }

    @Override
    public void onSearchFailure() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncEnd() {

    }
}
