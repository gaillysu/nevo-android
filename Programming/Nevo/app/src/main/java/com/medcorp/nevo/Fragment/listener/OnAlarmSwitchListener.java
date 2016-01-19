package com.medcorp.nevo.fragment.listener;

import android.widget.Switch;

import com.medcorp.nevo.model.Alarm;

/**
 * Created by gaillysu on 16/1/19.
 */
public interface OnAlarmSwitchListener {
    public void onAlarmSwitch(Switch alarmSwitch, Alarm alarm);
}
