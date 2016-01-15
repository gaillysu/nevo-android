package com.medcorp.nevo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorGetter;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationNameVisitor;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationIconVisitor;
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
        RobotoTextView notificationLabel = (RobotoTextView)itemView.findViewById(R.id.activity_setting_notification_name);
        RobotoTextView notificationValue = (RobotoTextView)itemView.findViewById(R.id.activity_setting_notification_color);
        ImageView notificationImage = (ImageView) itemView.findViewById(R.id.activity_setting_notification_image);

        NotificationIconVisitor iconGetter = new NotificationIconVisitor(context);
        NotificationNameVisitor nameGetter = new NotificationNameVisitor(context);
        NotificationColorGetter colorGetter = new NotificationColorGetter(context);

        Notification notification = listNotification.get(position);

        notificationImage.setImageDrawable(notification.accept(iconGetter));
        notificationLabel.setText(notification.accept(nameGetter));
        if(notification.isOn()) {
            notificationValue.setText(convertLEDColor2Clock(notification.accept(colorGetter).getTag()));
        }
        else {
            notificationValue.setText("Deactivated");
        }
        return itemView;
    }

    private String convertLEDColor2Clock(String color)
    {
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
}
