package com.medcorp.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.adapter.EditNotificationAdapter;
import com.medcorp.base.BaseActivity;
import com.medcorp.ble.datasource.NotificationDataHelper;
import com.medcorp.ble.model.color.BlueLed;
import com.medcorp.ble.model.color.GreenLed;
import com.medcorp.ble.model.color.LightGreenLed;
import com.medcorp.ble.model.color.NevoLed;
import com.medcorp.ble.model.color.OrangeLed;
import com.medcorp.ble.model.color.RedLed;
import com.medcorp.ble.model.color.YellowLed;
import com.medcorp.ble.model.notification.Notification;
import com.medcorp.ble.model.notification.OtherAppNotification;
import com.medcorp.model.NotificationListItemBean;
import com.medcorp.util.Preferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * Created by gaillysu on 15/12/31.
 */
public class EditSettingNotificationActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.main_toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_setting_notification_edit_onoff)
    SwitchCompat onOffSwitch;
    @Bind(R.id.notification_lamp_list)
    ListView notificationLampList;
    @Bind(R.id.notification_watch_icon)
    ImageView watchView;
    @Bind(R.id.notification_activity_layout)
    CoordinatorLayout coordinatorLayout;
    private int defaultColor = 0;
    private EditNotificationAdapter adapter;

    private final List<NevoLed> ledList = new ArrayList<>();
    private NotificationDataHelper helper;
    private Notification notification;
    private NevoLed selectedLed;
    private List<NotificationListItemBean> dataList;
    private String[] notificationTimeTextArray;

    private int[] notificationIcon = {R.drawable.red_dot, R.drawable.blue_dot, R.drawable.light_green_dot,
            R.drawable.yellow_dot, R.drawable.orange_dot, R.drawable.green_dot};

    private int[] watchIcon = {R.drawable.two_clock_notification, R.drawable.four_clock_notification,
            R.drawable.six_clock_notification, R.drawable.eight_clock_notificatio,
            R.drawable.ten_clock_notification, R.drawable.twele_clock_notification
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notification = (Notification) getIntent().getExtras().getSerializable(getString(R.string.key_notification));

        helper = new NotificationDataHelper(this);
        ledList.add(new RedLed());
        ledList.add(new BlueLed());
        ledList.add(new LightGreenLed());
        ledList.add(new YellowLed());
        ledList.add(new OrangeLed());
        ledList.add(new GreenLed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        if (notification instanceof OtherAppNotification) {
            title.setText(((OtherAppNotification) notification).getAppName(this));
        } else {
            title.setText(notification.getStringResource());
        }
        notificationTimeTextArray = getResources().getStringArray(R.array.notification_array);
        onOffSwitch.setChecked(notification.isOn());
        initView();
    }

    private void setDefaultLampColor() {
        switch (selectedLed.getStringResource()) {
            case R.string.notification_led_red:
                defaultColor = 0;
                break;
            case R.string.notification_led_blue:
                defaultColor = 1;
                break;
            case R.string.notification_led_light_green:
                defaultColor = 2;
                break;
            case R.string.notification_led_yellow:
                defaultColor = 3;
                break;
            case R.string.notification_led_orange:
                defaultColor = 4;
                break;
            case R.string.notification_led_green:
                defaultColor = 5;
                break;
        }
    }

    private void initView() {
        selectedLed = Preferences.getNotificationColor(this, notification, getModel());

        setDefaultLampColor();
        dataList = new ArrayList<>();
        for (int i = 0; i < notificationIcon.length; i++) {
            NotificationListItemBean bean = new NotificationListItemBean();
            bean.setLampId(notificationIcon[i]);
            bean.setNotificationTimeText(notificationTimeTextArray[i]);
            if (i == defaultColor) {
                bean.setChecked(true);
            } else {
                bean.setChecked(false);
            }
            dataList.add(bean);
        }
        watchView.setImageResource(watchIcon[defaultColor]);
        adapter = new EditNotificationAdapter(this, dataList);
        notificationLampList.setAdapter(adapter);
        notificationLampList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setChecked(false);
            if (position == i) {
                dataList.get(i).setChecked(true);
            }
        }
        userSelectChangeLamp(position);
        adapter.notifyDataSetChanged();
    }

    @OnCheckedChanged(R.id.activity_setting_notification_edit_onoff)
    public void notificationEditTriggered(CompoundButton buttonView, boolean isChecked) {
        notification.setState(isChecked);
        helper.saveState(notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.done_menu:
                Preferences.saveNotificationColor(this, notification, selectedLed.getHexColor());
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void userSelectChangeLamp(int position) {
        watchView.setImageResource(watchIcon[position]);
        selectedLed = ledList.get(position);
        Preferences.saveNotificationColor(this, notification, selectedLed.getHexColor());
    }
}
