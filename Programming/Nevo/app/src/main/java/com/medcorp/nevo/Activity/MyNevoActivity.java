package com.medcorp.nevo.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.adapter.MyNevoAdapter;
import com.medcorp.nevo.event.BatteryEvent;
import com.medcorp.nevo.model.MyNevo;
import com.medcorp.nevo.util.Common;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

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

    private MyNevo myNevo;
    private final int battery_level = 2; //default is 2,  value is [0,1,2], need get later
    private final boolean available_version = false;//need check later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mynevo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getModel().setObservableActivity(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_my_nevo);
        String app_version = "";
        try {
            app_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        myNevo = new MyNevo(getModel().getWatchFirmware(),getModel().getWatchSoftware(),app_version,battery_level,available_version,null);
        myNevoListView.setAdapter(new MyNevoAdapter(this, myNevo));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        getModel().setObservableActivity(this);
        if (getModel().isWatchConnected()){
            getModel().getBatteryLevelOfWatch();
        }
        //we can't make sure that OTA is stable before passed enough testing, here firstly disable it
        checkVersion();
    }

    private void checkVersion()
    {
        List<String> firmwareURLs = new ArrayList<String>();
        //check build-in firmwares
        //fill  list by build-in files or download files
        if(null == getModel().getWatchSoftware() || null == getModel().getWatchFirmware())
        {
            return;
        }

        int  currentSoftwareVersion = Integer.parseInt(getModel().getWatchSoftware());
        int  currentFirmwareVersion = Integer.parseInt(getModel().getWatchFirmware());
        firmwareURLs = Common.needOTAFirmwareURLs(this,currentSoftwareVersion,currentFirmwareVersion);
        if(!firmwareURLs.isEmpty())
        {
            myNevo.setAvailableVersion(true);
            myNevo.setFirmwareURLs(firmwareURLs);
            myNevoListView.setAdapter(new MyNevoAdapter(this, myNevo));
        }
        //check network firmwares

        //end check network firmwares
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
    public void notifyOnConnected() {

    }

    @Override
    public void notifyOnDisconnected() {

    }

    @Subscribe
    public void onEvent(BatteryEvent batteryEvent){
        myNevo.setBatteryLevel((int) batteryEvent.getBattery().getBatteryLevel());
        myNevoListView.setAdapter(new MyNevoAdapter(this, myNevo));
        Log.w("Karl","Pretty coool");
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
}
