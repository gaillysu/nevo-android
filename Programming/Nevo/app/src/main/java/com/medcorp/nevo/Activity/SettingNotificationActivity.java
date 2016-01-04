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
import com.medcorp.nevo.ble.model.notification.visitor.NotificationNameVisitor;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationVisitor;

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

        listActiveNotification = new ArrayList<Notification>();
        listInActiveNotification = new ArrayList<Notification>();

        Map<Notification, Integer> applicationNotificationColorMap = new HashMap<Notification, Integer>();
        NotificationColorGetter getter = new NotificationColorGetter(this);
        NotificationDataHelper dataHelper = new NotificationDataHelper(this);
        Notification applicationNotification = new TelephoneNotification();
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
        applicationNotification = new SmsNotification();
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
        applicationNotification = new EmailNotification();
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
        applicationNotification = new FacebookNotification();
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
        applicationNotification = new CalendarNotification();
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
        applicationNotification = new WeChatNotification();
        applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());
        //applicationNotification = new WhatsappNotification();
        //applicationNotificationColorMap.put(dataHelper.getState(applicationNotification), applicationNotification.accept(getter).getColor());

        for (Notification notification: applicationNotificationColorMap.keySet()) {
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

        NotificationColorGetter getter = new NotificationColorGetter(this);
        NotificationNameVisitor nameGetter = new NotificationNameVisitor(this);

        if (parent.getId() == activeListView.getId())
        {
            Notification applicationNotification = listActiveNotification.get(position);
            intent.putExtra("isOn",applicationNotification.isOn());
            intent.putExtra("nameNotification",applicationNotification.accept(nameGetter));
            intent.putExtra("colorNotification",applicationNotification.accept(getter).getColor());
        }
        if (parent.getId() == inactiveListView.getId())
        {
            Notification applicationNotification = listInActiveNotification.get(position);
            intent.putExtra("isOn",applicationNotification.isOn());
            intent.putExtra("name",applicationNotification.accept(nameGetter));
            intent.putExtra("color",applicationNotification.accept(getter).getColor());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
