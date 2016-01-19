package com.medcorp.nevo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.medcorp.nevo.R;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.view.customfontview.RobotoTextView;

/**
 * Created by gaillysu on 16/1/19.
 */
public class AlarmEditAdapter extends BaseAdapter {
    Context context;
    Alarm alarm;

    public AlarmEditAdapter(Context context,Alarm alarm)
    {
        super();
        this.context = context;
        this.alarm = alarm;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return alarm;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_alarm_edit_list_view_item, parent, false);

        RobotoTextView title = (RobotoTextView)itemView.findViewById(R.id.activity_alarm_edit_list_view_item_title_label);
        RobotoTextView summary = (RobotoTextView)itemView.findViewById(R.id.activity_alarm_edit_list_view_item_summary_label);
        if(position == 0)
        {
            title.setText(alarm.toString());
            summary.setText("Set a different time");
        }
        else if(position == 1)
        {
            title.setText(alarm.getLabel());
            summary.setText("Set label for alarm");
        }
        else if(position == 2)
        {
            title.setVisibility(View.GONE);
            summary.setTextColor(Color.BLACK);
            summary.setText("Delete alarm");
        }

        return itemView;
    }
}
