package com.medcorp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.medcorp.R;
import com.medcorp.activity.EditAlarmActivity;
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
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 11/12/15.
 */
public class AlarmFragment extends BaseObservableFragment implements OnAlarmSwitchListener,
        TimePickerDialog.OnTimeSetListener, AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.fragment_alarm_list_view)
    ListView alarmListView;

    private List<Alarm> alarmList;
    private AlarmArrayAdapter alarmArrayAdapter;
    private LayoutInflater inflater;
    private Boolean isMondayChecked = false;

    private TreeMap<Integer, String> mMap;
    private Boolean isTuesdayChecked = false;
    private Boolean isWednesdayChecked = false;
    private Boolean isThursdayChecked = false;
    private Boolean isFridayChecked = false;
    private Boolean isSaturdayChecked = false;
    private Boolean isSundayChecked = false;
    private Boolean isSelectEveryDay = false;
    private int alarmSelectStyle = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, view);
        this.inflater = inflater;
        alarmList = new ArrayList<>();
        alarmArrayAdapter = new AlarmArrayAdapter(getContext(), alarmList, this);
        alarmListView.setAdapter(alarmArrayAdapter);
        alarmListView.setOnItemClickListener(this);
        mMap = new TreeMap<>();
        refreshListView();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
    public void onTimeSet(TimePicker view, final int hourOfDay, final int minute) {
        //        new MaterialDialog.Builder(getActivity())
        //        new MaterialDialog.Builder(getActivity())
        //        new MaterialDialog.Builder(getActivity())
        //                .title(R.string.alarm_add)
        //                .content(R.string.alarm_label_alarm)
        //                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
        //                .input(getString(R.string.title_alarm), "", new MaterialDialog.InputCallback() {
        //                    @Override
        //                    public void onInput(MaterialDialog dialog, CharSequence input) {
        //                        alarm.setLabel(input.toString());
        //                        getModel().addAlarm(alarm);
        //                        refreshListView();
        //                    }
        //                }).negativeText(R.string.alarm_cancel)
        //                .show();

        //        final AlertDialog.Builder selectRepeatDialog = new AlertDialog.Builder(getContext());
        //        final AlertDialog dialog = selectRepeatDialog.create();
        View alarmDialogView = inflater.inflate(R.layout.add_alarm_dialog_layout, null);
        final Dialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(alarmDialogView);

        Button calcenButton = (Button) alarmDialogView.findViewById(R.id.cancel_edit_alarm_bt);
        final Button saveNewAlarm = (Button) alarmDialogView.findViewById(R.id.add_new_alarm_bt);
        final EditText alarmName = (EditText) alarmDialogView.findViewById(R.id.edit_input_alarm_name);
        final RadioGroup alarmStyle = (RadioGroup) alarmDialogView.findViewById(R.id.select_alarm_style_radio_group);
        ToggleButton monday = (ToggleButton) alarmDialogView.findViewById(R.id.tog_btn_monday);
        ToggleButton tuesday = (ToggleButton) alarmDialogView.findViewById(R.id.tog_btn_tuesday);
        ToggleButton wednesday = (ToggleButton) alarmDialogView.findViewById(R.id.tog_btn_wednesday);
        ToggleButton thursday = (ToggleButton) alarmDialogView.findViewById(R.id.tog_btn_thursday);
        ToggleButton friday = (ToggleButton) alarmDialogView.findViewById(R.id.tog_btn_friday);
        ToggleButton saturday = (ToggleButton) alarmDialogView.findViewById(R.id.tog_btn_saturday);
        ToggleButton sunday = (ToggleButton) alarmDialogView.findViewById(R.id.tog_btn_sunday);

        monday.setOnCheckedChangeListener(this);
        tuesday.setOnCheckedChangeListener(this);
        wednesday.setOnCheckedChangeListener(this);
        thursday.setOnCheckedChangeListener(this);
        friday.setOnCheckedChangeListener(this);
        saturday.setOnCheckedChangeListener(this);
        sunday.setOnCheckedChangeListener(this);
        //        selectRepeatDialog.setView(alarmDialogView);
        alarmStyle.setOnCheckedChangeListener(this);

        calcenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm alarm = new Alarm(hourOfDay, minute, (byte) 0, "", 0, "");
                StringBuffer alarmRepeatDay = new StringBuffer();
                dialog.dismiss();
                if (TextUtils.isEmpty(alarmName.getText().toString())) {
                    alarmName.setText(getString(R.string.menu_drawer_alarm));
                } else {
                    alarm.setLabel(alarmName.getText().toString());
                }
                for (int i = 0; i < mMap.size(); i++) {
                    String selectRepeatString = mMap.get(new Integer(i));
                    if (i != (mMap.size() - 1)) {
                        alarmRepeatDay.append(selectRepeatString + ",");
                    } else {
                        alarmRepeatDay.append(selectRepeatString);
                    }
                }
                alarm.setAlarmType(alarmSelectStyle);
                alarm.setRepeatDay(alarmRepeatDay.toString());
                getModel().addAlarm(alarm);
                refreshListView();
            }
        });

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
        if (resultCode != 0) {
            syncAlarmByEditor();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshListView();

    }

    private void refreshListView() {
        if (alarmArrayAdapter != null && alarmListView != null) {
            alarmList = getModel().getAllAlarm();
            alarmArrayAdapter.clear();
            alarmArrayAdapter.addAll(alarmList);
            alarmArrayAdapter.notifyDataSetChanged();
            alarmListView.invalidate();
        }
    }

    @Override
    public void onAlarmSwitch(SwitchCompat alarmSwitch, Alarm alarm) {
        if (!getModel().isWatchConnected()) {
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            return;
        }
        boolean isChecked = alarmSwitch.isChecked();
        if (isChecked && getAlarmEnableCount() == 3) {
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_max_three_alarm);
            return;
        }
        alarm.setWeekDay(isChecked ? (byte) 0 : (byte) 1);
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
            if (thisAlarm.getWeekDay() > 0 && alarmSettingList.size() < 3) {
                alarmSettingList.add(thisAlarm);
            }
        }
        //step3:check  alarmSettingList.size() == 3 ?
        ////build 1 or 2 invaild alarm to add alarmSettingList
        if (alarmSettingList.size() == 1) {
            alarmSettingList.add(new Alarm(0, 0, (byte) 0, "unknown", 0, ""));
            alarmSettingList.add(new Alarm(0, 0, (byte) 0, "unknown", 0, ""));
        } else if (alarmSettingList.size() == 2) {
            alarmSettingList.add(new Alarm(0, 0, (byte) 0, "unknown", 0, ""));
        }
        getModel().setAlarm(alarmSettingList);
        ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
    }

    private int getAlarmEnableCount() {
        int count = 0;
        for (Alarm alarm : alarmList) {
            if (alarm.getWeekDay() > 0) {
                count++;
            }
        }
        return count;
    }

    private void syncAlarmByEditor() {
        if (!getModel().isWatchConnected()) {
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            return;
        }
        List<Alarm> list = getModel().getAllAlarm();
        List<Alarm> customerAlarmList = new ArrayList<>();
        if (!list.isEmpty()) {
            for (Alarm alarm : list) {
                if (alarm.getWeekDay() > 0) {
                    customerAlarmList.add(alarm);
                    if (customerAlarmList.size() >= SetAlarmWithTypeRequest.maxAlarmCount) {
                        break;
                    }
                }
            }
            if (customerAlarmList.isEmpty()) {
                customerAlarmList.add(list.get(0));
            }
        } else {
            customerAlarmList.add(new Alarm(0, 0, (byte) 0, "", 0, ""));
        }
        getModel().setAlarm(customerAlarmList);
        ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
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
        ((MainActivity) getActivity()).showStateString(id, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            // 选中周一
            case R.id.tog_btn_monday:
                if (isChecked) {
                    isMondayChecked = true;
                    mMap.put(1, getString(R.string.one_h));
                } else {
                    isMondayChecked = false;
                    mMap.remove(1);
                }
                break;
            // 选中周二
            case R.id.tog_btn_tuesday:
                if (isChecked) {
                    isTuesdayChecked = true;
                    mMap.put(2, getString(R.string.two_h));
                } else {
                    isTuesdayChecked = false;
                    mMap.remove(2);
                }
                break;
            // 选中周三
            case R.id.tog_btn_wednesday:
                if (isChecked) {
                    isWednesdayChecked = true;
                    mMap.put(3, getString(R.string.three_h));
                } else {
                    isWednesdayChecked = false;
                    mMap.remove(3);
                }
                break;
            // 选中周四
            case R.id.tog_btn_thursday:
                if (isChecked) {
                    isThursdayChecked = true;
                    mMap.put(4, getString(R.string.four_h));
                } else {
                    isThursdayChecked = false;
                    mMap.remove(4);
                }
                break;
            // 选中周五
            case R.id.tog_btn_friday:
                if (isChecked) {
                    isFridayChecked = true;
                    mMap.put(5, getString(R.string.five_h));
                } else {
                    isFridayChecked = false;
                    mMap.remove(5);
                }
                break;
            // 选中周六
            case R.id.tog_btn_saturday:
                if (isChecked) {
                    isSaturdayChecked = true;
                    mMap.put(6, getString(R.string.six_h));
                } else {
                    isSaturdayChecked = false;
                    mMap.remove(6);
                }
                break;
            // 选中周日
            case R.id.tog_btn_sunday:
                if (isChecked) {
                    isSundayChecked = true;
                    mMap.put(7, getString(R.string.day));
                } else {
                    isSundayChecked = false;
                    mMap.remove(7);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.alarm_style_sleep:
                alarmSelectStyle = 0;//0代表的是sleep的alarm
                break;
            case R.id.alarm_style_wake:
                alarmSelectStyle = 1;//1代表这是一个wake的alarm
                break;
        }
    }
}