package com.medcorp.nevo.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorGetter;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorSaver;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationNameVisitor;

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
    SwitchCompat onOffSwitch;

    @Bind(R.id.activity_setting_notification_edit_color_label)
    TextView colorLabel;

    @Bind(R.id.activity_setting_notification_edit_layout)
    RelativeLayout colorLayout;

    private String color;
    //TODO make enum and not strings. Omg.
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
        //TODO put in keys.xml
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
        colorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> stringList = new ArrayList<String>();

                stringList.add(getString(R.string.notification_led_red));
                stringList.add(getString(R.string.notification_led_blue));
                stringList.add(getString(R.string.notification_led_light));
                stringList.add(getString(R.string.notification_led_yellow));
                stringList.add(getString(R.string.notification_led_orange));
                stringList.add(getString(R.string.notification_led_green));
                CharSequence[] cs = stringList.toArray(new CharSequence[stringList.size()]);
                new MaterialDialog.Builder(EditSettingNotificationActivity.this)
                        .title(R.string.notification_position)
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
                        .positiveText(R.string.notification_ok)
                        .negativeText(R.string.notification_cancel)
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
        //TODO refactor Code
        if(color.equals("RED")) {
            return getString(R.string.notification_led_red);
        }
        if(color.equals("BLUE")) {
            return getString(R.string.notification_led_blue);
        }
        if(color.equals("LIGHT_GREEN")) {
            return getString(R.string.notification_led_light);
        }
        if(color.equals("YELLOW")) {
            return getString(R.string.notification_led_yellow);
        }
        if(color.equals("ORANGE")) {
            return getString(R.string.notification_led_orange);
        }
        if(color.equals("GREEN")) {
            return getString(R.string.notification_led_green);
        }
        return color;
    }

    Drawable convertLEDColor2Drawable(String color)
    {
        // TODO please.
        if(color.equals("RED")) {
            return ContextCompat.getDrawable(this, R.drawable.red_dot);
        }
        if(color.equals("BLUE")) {
            return ContextCompat.getDrawable(this, R.drawable.blue_dot);
        }
        if(color.equals("LIGHT_GREEN")) {
            return ContextCompat.getDrawable(this, R.drawable.green_dot);
        }
        if(color.equals("YELLOW")) {
            return ContextCompat.getDrawable(this, R.drawable.yellow_dot);
        }
        if(color.equals("ORANGE")) {
            return ContextCompat.getDrawable(this, R.drawable.orange_dot);
        }
        if(color.equals("GREEN")) {
            return ContextCompat.getDrawable(this, R.drawable.dark_green_dot);
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
        // TODO please
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
