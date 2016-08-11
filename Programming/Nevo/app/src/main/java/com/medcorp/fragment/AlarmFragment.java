package com.medcorp.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.activity.EditAlarmActivity;
import com.medcorp.R;
import com.medcorp.activity.MainActivity;
import com.medcorp.adapter.AlarmArrayAdapter;
import com.medcorp.ble.model.request.SetAlarmWithTypeRequest;
import com.medcorp.event.bluetooth.RequestResponseEvent;
import com.medcorp.fragment.base.BaseObservableFragment;
import com.medcorp.fragment.listener.OnAlarmSwitchListener;
import com.medcorp.model.Alarm;
import com.medcorp.view.ToastHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 11/12/15.
 *
 */
public class AlarmFragment extends BaseObservableFragment implements OnAlarmSwitchListener, TimePickerDialog.OnTimeSetListener, AdapterView.OnItemClickListener {

    @Bind(R.id.fragment_alarm_list_view)
    ListView alarmListView;

    private List<Alarm> alarmList;
    private AlarmArrayAdapter alarmArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, view);
        alarmList = new ArrayList<>();
        alarmArrayAdapter = new AlarmArrayAdapter(getContext(),alarmList,this);
        alarmListView.setAdapter(alarmArrayAdapter);
        alarmListView.setOnItemClickListener(this);
        refreshListView();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_menu:
                Dialog alarmDialog = new TimePickerDialog(getContext(), R.style.NevoDialogStyle, this, 8, 0, true);
                alarmDialog.setTitle(R.string.alarm_add);
                alarmDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(true);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final Alarm alarm = new Alarm(hourOfDay,minute,(byte)0,"");
        new MaterialDialog.Builder(getActivity())
                .title(R.string.alarm_add)
                .content(R.string.alarm_label_alarm)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.title_alarm), "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        alarm.setLabel(input.toString());
                        getModel().addAlarm(alarm);
                        refreshListView();
                    }
                }).negativeText(R.string.alarm_cancel)
                .show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getContext(), EditAlarmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.key_alarm_id), alarmList.get(position).getId());
        i.putExtras(bundle);
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefs.putString(getString(R.string.alarm_label), alarmList.get(position).getLabel());
        prefs.commit();
        getAppCompatActivity().startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when delete (resultCode == -1) or update (resultCode == 1) the enable alarm, do alarm sync
        if(resultCode!=0)
        {
            syncAlarmByEditor();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListView();

    }

    private void refreshListView(){
        if (alarmArrayAdapter != null && alarmListView != null ){
            alarmList = getModel().getAllAlarm();
            alarmArrayAdapter.clear();
            alarmArrayAdapter.addAll(alarmList);
            alarmArrayAdapter.notifyDataSetChanged();
            alarmListView.invalidate();
        }
    }

    @Override
    public void onAlarmSwitch(SwitchCompat alarmSwitch, Alarm alarm) {
        if(!getModel().isWatchConnected()){
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(),R.string.in_app_notification_no_watch);
            return;
        }
        boolean isChecked = alarmSwitch.isChecked();
        if (isChecked && getAlarmEnableCount() == 3) {
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_max_three_alarm);
            return;
        }
        alarm.setWeekDay(isChecked?(byte)0:(byte)1);
        getModel().updateAlarm(alarm);

        List<Alarm> alarmSettingList = new ArrayList<>();
        //step1: add this alarm
        alarmSettingList.add(alarm);
        //step2:, find other 2 alarms that is enabled.
        List<Alarm> alarmRemainsList = new ArrayList<>();
        for (int i = 0; i < alarmList.size(); i++) {
            Alarm theAlarm = alarmList.get(i);
            if (theAlarm.getId() != alarm.getId()) {
                alarmRemainsList.add(alarmList.get(i));
            }
        }
        for (Alarm thisAlarm : alarmRemainsList) {
            if (thisAlarm.getWeekDay()>0 && alarmSettingList.size() < 3) {
                alarmSettingList.add(thisAlarm);
            }
        }
        //step3:check  alarmSettingList.size() == 3 ?
        ////build 1 or 2 invaild alarm to add alarmSettingList
        if (alarmSettingList.size() == 1) {
            alarmSettingList.add(new Alarm(0, 0, (byte) 0, "unknown"));
            alarmSettingList.add(new Alarm(0, 0, (byte) 0, "unknown"));
        } else if (alarmSettingList.size() == 2) {
            alarmSettingList.add(new Alarm(0,0, (byte) 0,"unknown"));
        }
        getModel().setAlarm(alarmSettingList);
        ((MainActivity)getActivity()).showStateString(R.string.in_app_notification_syncing_alarm,false);
    }
    private int getAlarmEnableCount(){
        int count = 0;
        for(Alarm alarm:alarmList)
        {
            if(alarm.getWeekDay()>0){
                count++;
            }
        }
        return count;
    }
    private void syncAlarmByEditor()
    {
        if(!getModel().isWatchConnected()){
            ToastHelper.showShortToast(getContext(),R.string.in_app_notification_no_watch);
            return;
        }
        List<Alarm> list = getModel().getAllAlarm();
        List<Alarm> customerAlarmList = new ArrayList<>();
        if(!list.isEmpty())
        {
            for(Alarm alarm: list)
            {
                if(alarm.getWeekDay()>0)
                {
                    customerAlarmList.add(alarm);
                    if(customerAlarmList.size()>= SetAlarmWithTypeRequest.maxAlarmCount)
                    {
                        break;
                    }
                }
            }
            if(customerAlarmList.isEmpty())
            {
                customerAlarmList.add(list.get(0));
            }
        }
        else
        {
            customerAlarmList.add(new Alarm(0, 0, (byte) 0, ""));
        }
        getModel().setAlarm(customerAlarmList);
        ((MainActivity)getActivity()).showStateString(R.string.in_app_notification_syncing_alarm,false);
    }



    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(RequestResponseEvent event) {
        int id = event.isSuccess() ? R.string.alarm_synced : R.string.alarm_error_sync;
        ((MainActivity)getActivity()).showStateString(id,false);
    }
}