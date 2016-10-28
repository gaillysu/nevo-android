package com.medcorp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.model.NotificationListItemBean;

import java.util.List;

/**
 * Created by Jason on 2016/9/2.
 */
public class EditNotificationAdapter extends BaseAdapter {


    private List<NotificationListItemBean> list;
    private Context context;

    public EditNotificationAdapter(Context context, List<NotificationListItemBean> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.edit_notification_adapter_item_layout, viewGroup, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.lampImage = (ImageView) convertView.findViewById(R.id.notification_red);
            holder.notificationTimeTv = (TextView) convertView.findViewById(R.id.notification_time_text_view);
            holder.isChecked = (ImageView) convertView.findViewById(R.id.open_notification_red_lamp_flag);
            holder.dividerView = convertView.findViewById(R.id.notification_divider_view);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.dividerView.setVisibility(View.VISIBLE);
        NotificationListItemBean bean = list.get(position);
        if (bean != null) {
            holder.lampImage.setImageResource(bean.getLampId());
            holder.notificationTimeTv.setText(bean.getNotificationTimeText());
            if (bean.isChecked()) {
                holder.isChecked.setVisibility(View.VISIBLE);
            } else {
                holder.isChecked.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        ImageView lampImage;
        TextView notificationTimeTv;
        ImageView isChecked;
        View dividerView;
    }
}

