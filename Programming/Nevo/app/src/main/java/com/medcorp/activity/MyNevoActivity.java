package com.medcorp.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.adapter.MyNevoAdapter;
import com.medcorp.base.BaseActivity;
import com.medcorp.event.bluetooth.BatteryEvent;
import com.medcorp.model.MyNevo;
import com.medcorp.util.Common;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/28.
 */
public class MyNevoActivity extends BaseActivity {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_mynevo_list_view)
    ListView myNevoListView;
    @Bind(R.id.my_device_watch_version_news_layout_root)
    LinearLayout showMyDeviceNewsLayout;

    @Bind(R.id.my_watch_version_tv)
    TextView showFirmwerVersion;
    @Bind(R.id.my_device_battery_tv)
    TextView showWatchBattery;
    @Bind(R.id.my_device_version_text)
    TextView showWatchVersion;
    @Bind(R.id.my_watch_update_tv)
    TextView firmwerUpdateInfomation;

    private MyNevo myNevo;
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        title.setText(R.string.title_my_nevo);
        String app_version = "";
        try {
            app_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        myNevo = new MyNevo(getModel().getWatchFirmware(), getModel().getWatchSoftware(), app_version, battery_level, available_version, null);
        showMyDeviceNewsLayout.setVisibility(View.GONE);
        myNevoListView.setAdapter(new MyNevoAdapter(this, myNevo));

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getModel().isWatchConnected()) {
            getModel().getBatteryLevelOfWatch();
        }
        checkVersion();
    }

    private void checkVersion() {
        List<String> firmwareURLs = new ArrayList<>();
        //check build-in firmwares
        //fill  list by build-in files or download files
        if (null == getModel().getWatchSoftware() || null == getModel().getWatchFirmware()) {
            return;
        }

        int currentSoftwareVersion = Integer.parseInt(getModel().getWatchSoftware());
        int currentFirmwareVersion = Integer.parseInt(getModel().getWatchFirmware());
        firmwareURLs = Common.needOTAFirmwareURLs(this, currentSoftwareVersion, currentFirmwareVersion, getModel().getSyncController().getWatchInfomation().getWatchID());
        //only update nevo watch or nevo solar watch
        if (!firmwareURLs.isEmpty() && (getModel().getSyncController().getWatchInfomation().getWatchID() == 1
                || getModel().getSyncController().getWatchInfomation().getWatchID() == 2)) {
            myNevo.setAvailableVersion(true);
            myNevo.setFirmwareURLs(firmwareURLs);
            myNevoListView.setAdapter(new MyNevoAdapter(this, myNevo));
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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(final BatteryEvent batteryEvent) {
        //fix crash:  Only the original thread that created a view hierarchy can touch its views.
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                myNevo.setBatteryLevel((int) batteryEvent.getBattery().getBatteryLevel());
                myNevoListView.setAdapter(new MyNevoAdapter(MyNevoActivity.this, myNevo));

            }
        });
    }
}