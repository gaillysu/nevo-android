package com.medcorp.nevo.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.medcorp.nevo.R;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.view.customfontview.RobotoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karl-john on 17/12/15.
 */
public class AlarmArrayAdapter extends ArrayAdapter<Alarm> {
    private  Context context;
    private List<Alarm> alarmList;
    private ApplicationModel model;
    private AlarmArrayAdapter adapter;
    public AlarmArrayAdapter(Context context, ApplicationModel model, List<Alarm> alarmList) {
        super(context, 0, alarmList);
        this.context = context;
        this.model = model;
        this.alarmList = alarmList;
        adapter = this;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fragment_alarm_list_view_item, parent, false);
        final Alarm alarm = alarmList.get(position);
        RobotoTextView alarmTimeTextView = (RobotoTextView) itemView.findViewById(R.id.fragment_alarmm_list_view_item_alarm_time);
        RobotoTextView alarmLabelTextView = (RobotoTextView) itemView.findViewById(R.id.fragment_alarmm_list_view_item_alarm_label);
        Switch onOffSwitch = (Switch) itemView.findViewById(R.id.fragment_alarmm_list_view_item_alarm_switch);
        alarmTimeTextView.setText(alarm.toString());
        alarmLabelTextView.setText(alarm.getLabel());
        onOffSwitch.setOnCheckedChangeListener(null);
        onOffSwitch.setChecked(alarm.isEnable());

        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && getAlarmEnableCount() == 3) {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(context, "This alarm can't be activied over MAX 3.", Toast.LENGTH_LONG).show();
                    return;
                }
                alarm.setEnable(isChecked);
                model.updateAlarm(alarm);

                List<Alarm> alarmSettingList = new ArrayList<Alarm>();
                //step1: add this alarm
                alarmSettingList.add(alarm);
                //step2:, find other 2 alarms that is enabled.
                List<Alarm> alarmRemainsList = new ArrayList<Alarm>();
                for (int i = 0; i < alarmList.size(); i++) {
                    if (i != position) alarmRemainsList.add(alarmList.get(i));
                }
                for (Alarm thisAlarm : alarmRemainsList) {
                    if (thisAlarm.isEnable() && alarmSettingList.size() < 3) {
                        alarmSettingList.add(thisAlarm);
                    }
                }
                //step3:check  alarmSettingList.size() == 3 ?
                ////build 1 or 2 invaild alarm to add alarmSettingList
                if (alarmSettingList.size() == 1) {
                    alarmSettingList.add(new Alarm(0, 0, false, "unknown"));
                    alarmSettingList.add(new Alarm(0, 0, false, "unknown"));
                } else if (alarmSettingList.size() == 2) {
                    alarmSettingList.add(new Alarm(0,0,false,"unknown"));
                }
                model.setAlarm(alarmSettingList);
            }
        });

        return itemView;
    }

    private int getAlarmEnableCount(){
        int count = 0;
        for(Alarm alarm:alarmList)
        {
            if(alarm.isEnable()) count++;
        }
        return count;
    }

}
