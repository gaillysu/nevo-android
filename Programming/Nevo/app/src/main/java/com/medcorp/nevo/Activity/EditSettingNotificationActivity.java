package com.medcorp.nevo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.adapter.SettingNotificationArrayAdapter;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/31.
 */
public class EditSettingNotificationActivity extends BaseActivity{
    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    private boolean isON;
    private int color;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        color = getIntent().getIntExtra("color",0);
        isON = getIntent().getBooleanExtra("isOn",false);
        setTitle(getIntent().getStringExtra("name"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
