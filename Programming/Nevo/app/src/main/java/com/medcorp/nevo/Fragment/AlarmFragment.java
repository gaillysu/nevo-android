package com.medcorp.nevo.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
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
                alarmDialog.setTitle("Add Alarm");
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
        String resultString = success?"Alarm Synced":"Error Syncing Alarm";
        ((MainActivity)getActivity()).showStateString(resultString,false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final Alarm alarm = new Alarm(hourOfDay,minute,false,"");
        new MaterialDialog.Builder(getActivity())
                .title("Add Alarm")
                .content("Label your alarm. ")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                .input("Alarm", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        alarm.setLabel(input.toString());
                            getModel().addAlarm(alarm);
                        refreshListView();
                    }
                }).negativeText("Cancel")
                .show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getContext(), EditAlarmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("Alarm_ID", alarmList.get(position).getId());
        i.putExtras(bundle);
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefs.putString("fragment_edit_alarm_label", alarmList.get(position).getLabel());
        prefs.commit();
        getAppCompatActivity().startActivity(i);
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
    public void onAlarmSwitch(Switch alarmSwitch, Alarm alarm) {
        if(!getModel().isWatchConnected()){
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(),"No watch connected.");
            return;
        }
        boolean isChecked = alarmSwitch.isChecked();
        if (isChecked && getAlarmEnableCount() == 3) {
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(), "This alarm can't be activied over MAX 3.");
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
        ((MainActivity)getActivity()).showStateString("Syncing Alarm...",false);
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
}