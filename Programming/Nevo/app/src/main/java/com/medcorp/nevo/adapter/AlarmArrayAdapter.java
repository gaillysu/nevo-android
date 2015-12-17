package com.medcorp.nevo.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.view.customfontview.RobotoTextView;

import java.util.List;

/**
 * Created by karl-john on 17/12/15.
 */
public class AlarmArrayAdapter extends ArrayAdapter<Alarm> {
    private  Context context;
    private List<Alarm> alarmList;

    public AlarmArrayAdapter(Context context, int resource, List<Alarm> alarmList) {
        super(context, resource, alarmList);
        this.context = context;
        this.alarmList = alarmList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fragment_alarm_list_view_item, parent, false);
        Alarm alarm = alarmList.get(position);
        RobotoTextView alarmTimeTextView = (RobotoTextView) itemView.findViewById(R.id.fragment_alarmm_list_view_item_alarm_time);
        RobotoTextView alarmLabelTextView = (RobotoTextView) itemView.findViewById(R.id.fragment_alarmm_list_view_item_alarm_label);
        Switch onOffSwitch = (Switch) itemView.findViewById(R.id.fragment_alarmm_list_view_item_alarm_switch);
        alarmTimeTextView.setText(alarm.toString());
        alarmLabelTextView.setText(alarm.getLabel());
        onOffSwitch.setChecked(alarm.isEnable());
        return itemView;
    }


}
