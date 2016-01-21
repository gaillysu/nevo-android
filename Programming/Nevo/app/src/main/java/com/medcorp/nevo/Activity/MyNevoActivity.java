package com.medcorp.nevo.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.adapter.MyNevoAdapter;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.model.MyNevo;

import java.io.IOException;
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

    private MyNevo mynevo;
    private final int battery_level = 2; //default is 2,  value is [0,1,2], need get later
    private final boolean available_version = false;//need check later

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mynevo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //TODO put in Strings.xml
        setTitle("My Nevo");
        String app_version = "";
        try {
            app_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mynevo = new MyNevo(getModel().getWatchFirmware(),getModel().getWatchSoftware(),app_version,battery_level,available_version,null);
        myNevoListView.setAdapter(new MyNevoAdapter(this, mynevo));
    }

    @Override
    public void onResume() {
        super.onResume();
        getModel().observableActivity(this);
        if (getModel().isWatchConnected()){
            getModel().getBatteryLevelOfWatch();
        }
        checkVersion();
    }

    //TODO This method should not be in here.
    private void checkVersion()
    {
        List<String> firmwareURLs = new ArrayList<String>();

        //check build-in firmwares
        //fill  list by build-in files or download files
        if(null == getModel().getWatchSoftware() || null == getModel().getWatchFirmware())
        {
            return;
        }

        String[]files;
        int  currentSoftwareVersion = Integer.parseInt(getModel().getWatchSoftware());
        int  currentFirmwareVersion = Integer.parseInt(getModel().getWatchFirmware());
        int buildinSoftwareVersion = 0;
        int buildinFirmwareVersion = 0;

        try {
            files = getAssets().list("firmware");
            for(String file:files)
            {
                if(file.contains(".hex"))
                {
                    int start  = file.toLowerCase().indexOf("_v");
                    int end = file.toLowerCase().indexOf(".hex");
                    String vString = file.substring(start+2,end);
                    if(vString != null) buildinFirmwareVersion = Integer.parseInt(vString);
                    if(currentFirmwareVersion < buildinFirmwareVersion )
                    {
                        firmwareURLs.add("firmware/" + file);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            files = getAssets().list("firmware");
            for(String file:files)
            {
                if(file.contains(".bin"))
                {
                    int start  = file.toLowerCase().indexOf("_v");
                    int end = file.toLowerCase().indexOf(".bin");
                    String vString = file.substring(start+2,end);
                    if(vString != null) buildinSoftwareVersion = Integer.parseInt(vString);
                    if(currentSoftwareVersion < buildinSoftwareVersion )
                    {
                        //if MCU got broken, firstly update MCU
                        if(currentSoftwareVersion == 0)
                            firmwareURLs.add(0,"firmware/" + file);
                        else
                            firmwareURLs.add("firmware/" + file);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!firmwareURLs.isEmpty())
        {
            mynevo.setAvailable_version(true);
            mynevo.setFirmwareURLs(firmwareURLs);
            myNevoListView.setAdapter(new MyNevoAdapter(this, mynevo));
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

    @Override
    public void onInitializeStart() {

    }

    @Override
    public void onInitializeEnd() {

    }

    @Override
    public void onRequestResponse(boolean success) {

    }

}
