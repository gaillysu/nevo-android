package com.medcorp.nevo.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.ble.datasource.NotificationDataHelper;
import com.medcorp.nevo.ble.model.color.BlueLed;
import com.medcorp.nevo.ble.model.color.GreenLed;
import com.medcorp.nevo.ble.model.color.LightGreenLed;
import com.medcorp.nevo.ble.model.color.NevoLed;
import com.medcorp.nevo.ble.model.color.OrangeLed;
import com.medcorp.nevo.ble.model.color.RedLed;
import com.medcorp.nevo.ble.model.color.YellowLed;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.util.Preferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by gaillysu on 15/12/31.
 */
public class EditSettingNotificationActivity extends BaseActivity {
    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_setting_notification_edit_image)
    ImageView colorImage;

    @Bind(R.id.activity_setting_notification_edit_onoff)
    SwitchCompat onOffSwitch;

    @Bind(R.id.activity_setting_notification_edit_color_label)
    TextView colorLabel;

    @Bind(R.id.activity_setting_notification_edit_layout)
    RelativeLayout colorLayout;

    private final List<NevoLed> ledList = new ArrayList<>();
    private NotificationDataHelper helper;
    private Notification notification;
    private NevoLed selectedLed;
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
        selectedLed = Preferences.getNotificationColor(this,notification);
        setTitle(notification.getStringResource());
        onOffSwitch.setChecked(notification.isOn());
        colorImage.setImageDrawable(ContextCompat.getDrawable(this, selectedLed.getImageResource()));
        colorLabel.setText(getString(selectedLed.getStringResource()));
    }

    @OnCheckedChanged(R.id.activity_setting_notification_edit_onoff)
    public void notificationEditTriggered(CompoundButton buttonView, boolean isChecked){
        notification.setState(isChecked);
        helper.saveState(notification);
    }

    @OnClick(R.id.activity_setting_notification_edit_layout)
    public void colorLayoutClicked(){
        List<String> stringList = new ArrayList<String>();
        for (int i = 0; i < ledList.size(); i ++) {
            stringList.add(getString(ledList.get(i).getStringResource()));
        }
        CharSequence[] cs = stringList.toArray(new CharSequence[stringList.size()]);
        selectedLed = Preferences.getNotificationColor(this,notification);
        new AlertDialog.Builder(EditSettingNotificationActivity.this)
                    .setTitle(R.string.notification_position)
                    .setSingleChoiceItems(cs, getIndexFromLed(selectedLed), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which >= 0) {
                                selectedLed = ledList.get(which);
                            }
                        }
                    })
                    .setNegativeButton(R.string.notification_cancel, null)
                    .setPositiveButton(R.string.notification_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                colorImage.setImageDrawable(ContextCompat.getDrawable(EditSettingNotificationActivity.this, selectedLed.getImageResource()));
                                colorLabel.setText(getString(selectedLed.getStringResource()));
                                Preferences.saveNotificationColor(EditSettingNotificationActivity.this, notification, selectedLed);
                                dialog.dismiss();
                        }
                    })
                    .show();
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

    private int getIndexFromLed(NevoLed led) {
        for (int i = 0; i < ledList.size(); i++) {
            if (ledList.get(i).equals(led)) {
                return i;
            }
        }
        return -1;
    }
}
