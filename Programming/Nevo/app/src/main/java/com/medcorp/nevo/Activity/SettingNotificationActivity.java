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
import com.medcorp.nevo.ble.datasource.NotificationDataHelper;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorGetter;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorSaver;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationNameVisitor;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationVisitor;
import com.medcorp.nevo.ble.notification.NevoNotificationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        NevoNotificationListener.getNotificationAccessPermission(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        listActiveNotification = new ArrayList<Notification>();
        listInActiveNotification = new ArrayList<Notification>();

        List<Notification> allNotifications = new ArrayList<Notification>();
        NotificationDataHelper dataHelper = new NotificationDataHelper(this);

        Notification applicationNotification = new TelephoneNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new SmsNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new EmailNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new FacebookNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new CalendarNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        applicationNotification = new WeChatNotification();
        allNotifications.add(dataHelper.getState(applicationNotification));
        //applicationNotification = new WhatsappNotification();
        //allNotifications.add(dataHelper.getState(applicationNotification));

        for (Notification notification: allNotifications) {
            if(notification.isOn()){
                listActiveNotification.add(notification);
            }
            else{
                listInActiveNotification.add(notification);
            }
        }
        activeNotificationArrayAdapter = new SettingNotificationArrayAdapter(this,listActiveNotification);
        activeListView.setAdapter(activeNotificationArrayAdapter);
        activeListView.setOnItemClickListener(this);

        inactiveNotificationArrayAdapter = new SettingNotificationArrayAdapter(this,listInActiveNotification);
        inactiveListView.setAdapter(inactiveNotificationArrayAdapter);
        inactiveListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,EditSettingNotificationActivity.class);
        Notification applicationNotification = null;
        if (parent.getId() == activeListView.getId())
        {
             applicationNotification = listActiveNotification.get(position);
        }
        if (parent.getId() == inactiveListView.getId()) {
             applicationNotification = listInActiveNotification.get(position);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("notification", applicationNotification);
        intent.putExtras(bundle);
        startActivity(intent);
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