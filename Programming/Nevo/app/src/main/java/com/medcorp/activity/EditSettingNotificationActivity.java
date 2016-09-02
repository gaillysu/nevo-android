package com.medcorp.activity;

import android.os.Bundle;
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
import com.medcorp.model.NotificationListItemBean;
import com.medcorp.util.Preferences;
import com.medcorp.view.ToastHelper;

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

    private EditNotificationAdapter adapter;

    private final List<NevoLed> ledList = new ArrayList<>();
    private NotificationDataHelper helper;
    private Notification notification;
    private NevoLed selectedLed;
    private List<NotificationListItemBean> dataList;
    private int[] notificationIcon = {R.drawable.red_dot, R.drawable.blue_dot, R.drawable.green_dot,
            R.drawable.yellow_dot, R.drawable.orange_dot, R.drawable.light_green_dot};
    private String[] notificationTimeTextArray;
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

        helper = new NotificationDataHelper(this);
        ledList.add(new GreenLed());
        ledList.add(new RedLed());
        ledList.add(new BlueLed());
        ledList.add(new LightGreenLed());
        ledList.add(new YellowLed());
        ledList.add(new OrangeLed());
        notification = (Notification) getIntent().getSerializableExtra(getString(R.string.key_notification));
        selectedLed = Preferences.getNotificationColor(this, notification);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) toolbar.findViewById(R.id.lunar_tool_bar_title);
        title.setText(notification.getStringResource());
        notificationTimeTextArray = getResources().getStringArray(R.array.notification_array);
        onOffSwitch.setChecked(notification.isOn());
        initView();
    }

    private void initView() {
        dataList = new ArrayList<>();
        int def = getIndexFromLed(notification.getDefaultColor());
        for (int i = 0; i < notificationIcon.length; i++) {
            NotificationListItemBean bean = new NotificationListItemBean();
            bean.setLampId(notificationIcon[i]);
            bean.setNotificationTimeText(notificationTimeTextArray[i]);
            if (def == i) {
                bean.setChecked(true);
            }else{
                bean.setChecked(false);
            }
            dataList.add(bean);
        }
        watchView.setImageResource(watchIcon[def%5]);
        adapter = new EditNotificationAdapter(this, dataList);
        notificationLampList.setAdapter(adapter);
        notificationLampList.setOnItemClickListener(this);
        selectedLed = Preferences.getNotificationColor(this, notification);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setChecked(false);
            if (position == i) {
                dataList.get(i).setChecked(true);
            }
        }
        watchView.setImageResource(watchIcon[position]);
        selectedLed = ledList.get(position);
        Preferences.saveNotificationColor(this, notification, selectedLed);
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
                Preferences.saveNotificationColor(this, notification, selectedLed);
                ToastHelper.showShortToast(this,getString(R.string.save_notification_ok));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private int getIndexFromLed(NevoLed led) {
        for (int i = 0; i < ledList.size(); i++) {
            if (ledList.get(i).equals(led)) {
                return i;
            }
        }
        return -1;
    }
}
