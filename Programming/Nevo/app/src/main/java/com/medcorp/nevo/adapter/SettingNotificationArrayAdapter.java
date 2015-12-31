package com.medcorp.nevo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.medcorp.nevo.R;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorGetter;
import com.medcorp.nevo.model.Preset;
import com.medcorp.nevo.view.customfontview.RobotoTextView;

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
        RobotoTextView  notificationLabel = (RobotoTextView)itemView.findViewById(R.id.activity_setting_notification_name);
        RobotoTextView  notificationValue = (RobotoTextView)itemView.findViewById(R.id.activity_setting_notification_color);
        ImageView   notificationImage = (ImageView) itemView.findViewById(R.id.activity_setting_notification_image);
        NotificationColorGetter getter = new NotificationColorGetter(context);
        Notification notification = listNotification.get(position);
        notificationLabel.setText(notification.getTag());
        notificationValue.setText("" + notification.accept(getter).getColor());
        //notificationImage.setImageDrawable();
        return itemView;
    }
}
