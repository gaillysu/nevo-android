package com.medcorp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.medcorp.R;
import com.medcorp.ble.model.notification.Notification;
import com.medcorp.util.Preferences;
import com.medcorp.view.customfontview.RobotoTextView;

import java.util.List;

/**
 * Created by gaillysu on 15/12/23.
 */
public class SettingNotificationArrayAdapter extends ArrayAdapter<Notification> {
    private Context context;
    private List<Notification> listNotification;

    public SettingNotificationArrayAdapter(Context context, List<Notification> listNotification)
    {
        super(context,0,listNotification);
        this.context = context;
        this.listNotification = listNotification;
    }

    @Override
    public int getCount() {
        return listNotification==null?0:listNotification.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_setting_notification_list_view_item, parent, false);
        RobotoTextView notificationValue = (RobotoTextView)itemView.findViewById(R.id.activity_setting_notification_color);
        Notification notification = listNotification.get(position);
        ((ImageView) itemView.findViewById(R.id.activity_setting_notification_image)).setImageDrawable(ContextCompat.getDrawable(context, notification.getImageResource()));
        ((RobotoTextView)itemView.findViewById(R.id.activity_setting_notification_name)).setText(notification.getStringResource());
        if(notification.isOn()) {
            notificationValue.setText(context.getString(Preferences.getNotificationColor(context,notification).getStringResource()));
        }
        else {
            notificationValue.setText(R.string.notification_deactivated);
        }
        return itemView;
    }
}
