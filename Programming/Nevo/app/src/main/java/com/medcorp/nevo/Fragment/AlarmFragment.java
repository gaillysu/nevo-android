package com.medcorp.nevo.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.EditAlarmActivity;
import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.adapter.AlarmArrayAdapter;
import com.medcorp.nevo.ble.model.request.SetAlarmNevoRequest;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.fragment.listener.OnAlarmSwitchListener;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.view.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 11/12/15.
 */
public class AlarmFragment extends BaseObservableFragment implements OnAlarmSwitchListener, TimePickerDialog.OnTimeSetListener, AdapterView.OnItemClickListener {

    @Bind(R.id.fragment_alarm_list_view)
    ListView alarmListView;

    private List<Alarm> alarmList;
    private AlarmArrayAdapter alarmArrayAdapter;

    @Nullable
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
    public void notifyDatasetChanged() {

    }

    @Override
    public void notifyOnConnected() {

    }

    @Override
    public void notifyOnDisconnected() {

    }

    @Override
    public void batteryInfoReceived(Battery battery) {

    }

    @Override
    public void findWatchSuccess() {
    }

    @Override
    public void onSearching() {

    }

    @Override
    public void onSearchSuccess() {

    }

    @Override
    public void onSearchFailure() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncEnd() {

    }

    @Override
    public void onRequestResponse(boolean success) {
        int id = success ? R.string.alarm_synced : R.string.alarm_error_sync;
        ((MainActivity)getActivity()).showStateString(id,false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final Alarm alarm = new Alarm(hourOfDay,minute,false,"");
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
        //TODO put into keys.xml
        bundle.putInt("Alarm_ID", alarmList.get(position).getId());
        i.putExtras(bundle);
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefs.putString("fragment_edit_alarm_label", alarmList.get(position).getLabel());
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
        alarm.setEnable(isChecked);
        getModel().updateAlarm(alarm);

        List<Alarm> alarmSettingList = new ArrayList<Alarm>();
        //step1: add this alarm
        alarmSettingList.add(alarm);
        //step2:, find other 2 alarms that is enabled.
        List<Alarm> alarmRemainsList = new ArrayList<Alarm>();
        for (int i = 0; i < alarmList.size(); i++) {
            Alarm theAlarm = alarmList.get(i);
            if (theAlarm.getId() != alarm.getId()) {
                alarmRemainsList.add(alarmList.get(i));
            }
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
        getModel().setAlarm(alarmSettingList);
        ((MainActivity)getActivity()).showStateString(R.string.in_app_notification_syncing_alarm,false);
    }
    private int getAlarmEnableCount(){
        int count = 0;
        for(Alarm alarm:alarmList)
        {
            if(alarm.isEnable()){
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
        List<Alarm> customerAlarmList = new ArrayList<Alarm>();
        if(!list.isEmpty())
        {
            for(Alarm alarm: list)
            {
                if(alarm.isEnable())
                {
                    customerAlarmList.add(alarm);
                    if(customerAlarmList.size()>= SetAlarmNevoRequest.maxAlarmCount)
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
            customerAlarmList.add(new Alarm(0, 0, false, ""));
        }
        getModel().setAlarm(customerAlarmList);
        ((MainActivity)getActivity()).showStateString(R.string.in_app_notification_syncing_alarm,false);
    }
}