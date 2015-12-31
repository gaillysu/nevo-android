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
public class SettingNotificationActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_setting_notification_active_list_view)
    ListView activeListView;

    @Bind(R.id.activity_setting_notification_inactive_list_view)
    ListView inactiveListView;

    private SettingNotificationArrayAdapter activeNotificationArrayAdapter;
    private SettingNotificationArrayAdapter inactiveNotificationArrayAdapter;

    List<Notification> listActiveNotification;
    List<Notification> listInActiveNotification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Notifications");

        listActiveNotification = new ArrayList<Notification>();
        listActiveNotification.add(new TelephoneNotification());
        listActiveNotification.add(new SmsNotification());
        listActiveNotification.add(new EmailNotification());
        activeNotificationArrayAdapter = new SettingNotificationArrayAdapter(this,listActiveNotification);
        activeListView.setAdapter(activeNotificationArrayAdapter);
        activeListView.setOnItemClickListener(this);

        listInActiveNotification = new ArrayList<Notification>();
        listInActiveNotification.add(new FacebookNotification());
        listInActiveNotification.add(new CalendarNotification());
        listInActiveNotification.add(new WhatsappNotification());
        inactiveNotificationArrayAdapter = new SettingNotificationArrayAdapter(this,listInActiveNotification);
        inactiveListView.setAdapter(inactiveNotificationArrayAdapter);
        inactiveListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,EditSettingNotificationActivity.class);
        if (view.getId() == activeListView.getId())
        {
            //intent.put
        }
        if (view.getId() == inactiveListView.getId())
        {

        }
        startActivityForResult(intent,0);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
