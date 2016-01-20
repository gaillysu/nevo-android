package com.medcorp.nevo.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.adapter.SettingNotificationArrayAdapter;
import com.medcorp.nevo.ble.datasource.NotificationDataHelper;
import com.medcorp.nevo.ble.model.color.BlueLed;
import com.medcorp.nevo.ble.model.color.GreenLed;
import com.medcorp.nevo.ble.model.color.LightGreenLed;
import com.medcorp.nevo.ble.model.color.NevoLed;
import com.medcorp.nevo.ble.model.color.OrangeLed;
import com.medcorp.nevo.ble.model.color.RedLed;
import com.medcorp.nevo.ble.model.color.YellowLed;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorGetter;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorSaver;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationNameVisitor;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationVisitor;
import com.medcorp.nevo.model.Preset;
import com.medcorp.nevo.util.Preferences;

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

    @Bind(R.id.activity_setting_notification_edit_image)
    ImageView colorImage;

    @Bind(R.id.activity_setting_notification_edit_onoff)
    Switch onOffSwitch;

    @Bind(R.id.activity_setting_notification_edit_color_label)
    TextView colorLabel;

    @Bind(R.id.activity_setting_notification_edit_layout)
    RelativeLayout colorLayout;

    private String color;
    private final String[] colors ={"RED","BLUE","LIGHT_GREEN","YELLOW","ORANGE","GREEN"};
    private NotificationDataHelper helper;
    private NotificationColorGetter getter;
    private NotificationNameVisitor nameGetter;
    private Notification notification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        helper = new NotificationDataHelper(this);
        getter = new NotificationColorGetter(this);
        nameGetter = new NotificationNameVisitor(this);

        notification = (Notification) getIntent().getSerializableExtra("notification");
        color = notification.accept(getter).getTag();
        setTitle(notification.accept(nameGetter));
        onOffSwitch.setChecked(notification.isOn());

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notification.setState(isChecked);
                helper.saveState(notification);
            }
        });

        colorImage.setImageDrawable(convertLEDColor2Drawable(color));
        colorLabel.setText(convertLEDColor2Clock(color));
        //TODO  add to Strings.xml
        colorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> stringList = new ArrayList<String>();

                stringList.add("2 o'clock (red)");
                stringList.add("4 o'clock (blue)");
                stringList.add("6 o'clock (light green)");
                stringList.add("8 o'clock (yellow)");
                stringList.add("10 o'clock (orange)");
                stringList.add("12 o'clock (green)");
                CharSequence[] cs = stringList.toArray(new CharSequence[stringList.size()]);
                new MaterialDialog.Builder(EditSettingNotificationActivity.this)
                        .title("Position")
                        .items(cs)
                        .itemsCallbackSingleChoice(convertLEDColor2Index(color), new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if(which>=0)
                                {
                                    color = colors[which];
                                    colorImage.setImageDrawable(convertLEDColor2Drawable(color));
                                    colorLabel.setText(convertLEDColor2Clock(color));
                                    notification.accept(new NotificationColorSaver(EditSettingNotificationActivity.this, convertLEDColor2Object(color)));
                                }
                                return true;
                            }
                        })
                        .positiveText("Ok")
                        .negativeText("Cancel")
                        .show();
            }
        });
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

    String convertLEDColor2Clock(String color)
    {
        //TODO  add to Strings.xml && refactor Code
        if(color.equals("RED")) {
            return "Red LED" + " - 2 o'clock";
        }
        if(color.equals("BLUE")) {
            return "Blue LED" + " - 4 o'clock";
        }
        if(color.equals("LIGHT_GREEN")) {
            return "Light Green LED" + " - 6 o'clock";
        }
        if(color.equals("YELLOW")) {
            return "Yellow LED" + " - 8 o'clock";
        }
        if(color.equals("ORANGE")) {
            return "Orange LED" + " - 10 o'clock";
        }
        if(color.equals("GREEN")) {
            return "Green LED" + " - 12 o'clock";
        }

        return color;
    }

    Drawable convertLEDColor2Drawable(String color)
    {

        if(color.equals("RED")) {
            return  getDrawable(R.drawable.red_dot);
        }
        if(color.equals("BLUE")) {
            return getDrawable(R.drawable.blue_dot);
        }
        if(color.equals("LIGHT_GREEN")) {
            return getDrawable(R.drawable.green_dot);
        }
        if(color.equals("YELLOW")) {
            return getDrawable(R.drawable.yellow_dot);
        }
        if(color.equals("ORANGE")) {
            return getDrawable(R.drawable.orange_dot);
        }
        if(color.equals("GREEN")) {
            return getDrawable(R.drawable.dark_green_dot);
        }

        return null;
    }

    int convertLEDColor2Index(String color)
    {
        for(int i=0;i<colors.length;i++)
        {
            if(color.equals(colors[i])) return i;
        }

        return -1;
    }

    NevoLed convertLEDColor2Object(String color){

        if(color.equals("RED")) {
            return new RedLed();
        }
        if(color.equals("BLUE")) {
            return new BlueLed();
        }
        if(color.equals("LIGHT_GREEN")) {
            return new LightGreenLed();
        }
        if(color.equals("YELLOW")) {
            return new YellowLed();
        }
        if(color.equals("ORANGE")) {
            return new OrangeLed();
        }
        if(color.equals("GREEN")) {
            return new GreenLed();
        }

        return null;
    }


}
