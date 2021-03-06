package com.medcorp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.R;
import com.medcorp.activity.EditAlarmActivity;
import com.medcorp.activity.MainActivity;
import com.medcorp.adapter.AlarmArrayAdapter;
import com.medcorp.event.bluetooth.RequestResponseEvent;
import com.medcorp.fragment.base.BaseObservableFragment;
import com.medcorp.fragment.listener.OnAlarmSwitchListener;
import com.medcorp.model.Alarm;
import com.medcorp.view.ToastHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 11/12/15.
 */
public class AlarmFragment extends BaseObservableFragment implements OnAlarmSwitchListener,
        TimePickerDialog.OnTimeSetListener, AdapterView.OnItemClickListener,
        CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    @Bind(R.id.fragment_alarm_list_view)
    ListView alarmListView;

    private List<Alarm> alarmList;
    private AlarmArrayAdapter alarmArrayAdapter;
    private LayoutInflater inflater;
    private Boolean isMondayChecked = false;

    private TreeMap<Integer, String> mMap;
    private byte alarmSelectStyle = 0;
    private Button monday;
    private byte weekDay = 0;
    private Button tuesday;
    private Button thursday;
    private Button wednesday;
    private Button friday;
    private Button sunday;
    private Button saturday;
    private Alarm editAlarm;
    private boolean isRepeat = false;
    private boolean showSyncAlarm = false;
    private int firmWareVersion = 0;
    private int softwareVersion = 0;
    private boolean isLowVersion = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, view);
        this.inflater = inflater;
        alarmList = getModel().getAllAlarm();
        firmWareVersion = Integer.parseInt(getModel().getWatchFirmware() == null ? 0 + "" : getModel().getWatchFirmware());
        softwareVersion = Integer.parseInt(getModel().getWatchSoftware() == null ? 0 + "" : getModel().getWatchSoftware());
        if (firmWareVersion <= 31 && softwareVersion <= 18) {
            isLowVersion = true;
        }

        alarmArrayAdapter = new AlarmArrayAdapter(getContext(), alarmList, this, isLowVersion);
        alarmListView.setAdapter(alarmArrayAdapter);
        alarmListView.setOnItemClickListener(this);
        isMondayChecked = true;
        mMap = new TreeMap<>();
        refreshListView();
        setHasOptionsMenu(true);
        return view;
    }

    private void initAddAlarm() {
        initAlarmDialog((byte) 2);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_menu:
                if (!getModel().isWatchConnected()) {
                    ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
                    return false;
                }
                if (!isLowVersion) {
                    Dialog alarmDialog = new TimePickerDialog(getContext(), R.style.NevoDialogStyle, this, 8, 0, true);
                    alarmDialog.setTitle(R.string.alarm_add);
                    alarmDialog.show();
                } else if (isLowVersion && getModel().getAllAlarm().size() <3) {
                    Dialog alarmDialog = new TimePickerDialog(getContext(), R.style.NevoDialogStyle, this, 8, 0, true);
                    alarmDialog.setTitle(R.string.alarm_add);
                    alarmDialog.show();
                } else if (isLowVersion && getModel().getAllAlarm().size() >= 3) {
                    ToastHelper.showShortToast(AlarmFragment.this.getContext(), getString(R.string.tell_user_update_more_alarm));
                }
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
        //set today 's week day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        weekDay = (byte) calendar.get(Calendar.DAY_OF_WEEK);
        alarmSelectStyle = 0;//here must reset it when add new alarm, due to it is the class member variable
        if (!isLowVersion) {
            editAlarmDialog(hourOfDay, minute);
        } else {
            new MaterialDialog.Builder(AlarmFragment.this.getContext()).title(getString(R.string.alarm_add))
                    .content(getString(R.string.alarm_label_alarm))
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(getString(R.string.alarm_label), "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            String newName;
                            if (input.toString().length() == 0) {
                                newName = getString(R.string.menu_drawer_alarm) + " " + (alarmList.size() + 1);
                            } else {
                                newName = input.toString();
                            }
                            Alarm lowVersionAlarm = new Alarm(hourOfDay, minute, (byte) 0x80, newName, (byte) 1, (byte) (getModel().getAllAlarm().size() + 1));
                            getModel().addAlarm(lowVersionAlarm);
                            showSyncAlarm = true;
                            getModel().getSyncController().setAlarm(getModel().getAllAlarm(), false);
                            refreshListView();
                        }
                    }).negativeText(getString(R.string.goal_cancel)).show();
        }
    }

    private void editAlarmDialog(final int hourOfDay, final int minute) {
        View alarmDialogView = inflater.inflate(R.layout.add_alarm_dialog_layout, null);
        final Dialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(alarmDialogView);

        Button cancelButton = (Button) alarmDialogView.findViewById(R.id.cancel_edit_alarm_bt);
        final Button saveNewAlarm = (Button) alarmDialogView.findViewById(R.id.add_new_alarm_bt);
        final EditText alarmName = (EditText) alarmDialogView.findViewById(R.id.edit_input_alarm_name);
        final RadioGroup alarmStyle = (RadioGroup) alarmDialogView.findViewById(R.id.select_alarm_style_radio_group);
        monday = (Button) alarmDialogView.findViewById(R.id.tag_btn_monday);
        tuesday = (Button) alarmDialogView.findViewById(R.id.tag_btn_tuesday);
        wednesday = (Button) alarmDialogView.findViewById(R.id.tag_btn_wednesday);
        thursday = (Button) alarmDialogView.findViewById(R.id.tag_btn_thursday);
        friday = (Button) alarmDialogView.findViewById(R.id.tog_btn_friday);
        saturday = (Button) alarmDialogView.findViewById(R.id.tag_btn_saturday);
        sunday = (Button) alarmDialogView.findViewById(R.id.tag_btn_sunday);
        initAddAlarm();
        monday.setOnClickListener(this);
        tuesday.setOnClickListener(this);
        wednesday.setOnClickListener(this);
        thursday.setOnClickListener(this);
        friday.setOnClickListener(this);
        saturday.setOnClickListener(this);
        sunday.setOnClickListener(this);
        alarmStyle.setOnCheckedChangeListener(this);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        saveNewAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Alarm newAlarm = new Alarm(hourOfDay, minute, (byte) 0, "", (byte) 0, (byte) 0);
                StringBuffer alarmRepeatDay = new StringBuffer();

                if (TextUtils.isEmpty(alarmName.getText().toString())) {
                    newAlarm.setLabel(getString(R.string.menu_drawer_alarm) + " " + (alarmList.size() + 1));
                } else {
                    newAlarm.setLabel(alarmName.getText().toString());
                }
                List<Alarm> allAlarm = getModel().getAllAlarm();

                int num = 0;//awake/normal alarm: 0~6
                if (alarmSelectStyle == 0) {
                    num = 7;//sleep alarm: 7~13
                }

                for (int i = 0; i < allAlarm.size(); i++) {
                    if (allAlarm.get(i).getAlarmType() == alarmSelectStyle) {
                        num++;
                    }

                }
                for (int i = 0; i < allAlarm.size(); i++) {
                    byte repeatDay = allAlarm.get(i).getWeekDay();
                    byte alarmType = allAlarm.get(i).getAlarmType();
                    if ((repeatDay & 0x0F) == weekDay && alarmType == alarmSelectStyle) {
                        isRepeat = true;
                        break;
                    } else {
                        isRepeat = false;
                    }
                }
                if ((alarmSelectStyle == 0 && num <= 13) || (alarmSelectStyle == 1 && num <= 6)) {
                    if (!isRepeat) {
                        newAlarm.setWeekDay((byte) (0x80 | weekDay));
                        newAlarm.setAlarmType(alarmSelectStyle);
                        newAlarm.setAlarmNumber((byte) num);
                        getModel().addAlarm(newAlarm);
                        showSyncAlarm = true;
                        getModel().getSyncController().setAlarm(newAlarm);
                        ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_syncing_alarm, false);
                        refreshListView();
                    } else {
                        String[] weekDayArray = getContext().getResources().getStringArray(R.array.week_day);
                        if (alarmSelectStyle == 0) {
                            ToastHelper.showShortToast(AlarmFragment.this.getActivity()
                                    , getString(R.string.prompt_user_alarm_no_repeat) + " " + weekDayArray[weekDay]);
                        } else {
                            ToastHelper.showShortToast(AlarmFragment.this.getActivity()
                                    , getString(R.string.prompt_user_alarm_no_repeat_two) + " " + weekDayArray[weekDay]);
                        }
                    }
                } else {
                    ToastHelper.showShortToast(getContext(), getResources().getString(R.string.add_alarm_index_out));
                }
                dialog.dismiss();

            }
        });

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!getModel().isWatchConnected()) {
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            return;
        }
        Intent i = new Intent(getContext(), EditAlarmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.key_alarm_id), alarmList.get(position).getId());
        i.putExtras(bundle);
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefs.putString(getString(R.string.alarm_label), alarmList.get(position).getLabel());
        prefs.commit();
        editAlarm = alarmList.get(position);
        getAppCompatActivity().startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when delete (resultCode == -1) or update (resultCode == 1) the enable alarm, do alarm sync
        //resultCode == 0 ,do nothing
        if (resultCode != 0) {
            syncAlarmByEditor(resultCode);
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
        if (isChecked && getAlarmEnableCount() == 14) {
            alarmSwitch.setChecked(!alarmSwitch.isChecked());
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_max_fourteen_alarm);
            return;
        }
        //save weekday to low 4 bit,bit 7 to save enable or disable
        alarm.setWeekDay(isChecked ? (byte) (alarm.getWeekDay() | 0x80) : (byte) (alarm.getWeekDay() & 0x0F));
        getModel().updateAlarm(alarm);
        showSyncAlarm = true;
        if(isLowVersion) {
            getModel().getSyncController().setAlarm(getModel().getAllAlarm(), false);
        }
        else {
            getModel().getSyncController().setAlarm(alarm);
        }
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

    private void syncAlarmByEditor(int deleteOrUpdate) {
        if (!getModel().isWatchConnected()) {
            ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            return;
        }
        if (deleteOrUpdate == -1) {
            editAlarm.setWeekDay((byte) 0);
        } else if (deleteOrUpdate == 1) {
            editAlarm = getModel().getAlarmById(editAlarm.getId());
        }
        showSyncAlarm = true;
        if(isLowVersion) {
            getModel().getSyncController().setAlarm(getModel().getAllAlarm(), false);
        }
        else {
            getModel().getSyncController().setAlarm(editAlarm);
        }
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
        if (showSyncAlarm) {
            showSyncAlarm = false;
            int id = event.isSuccess() ? R.string.alarm_synced : R.string.alarm_error_sync;
            ((MainActivity) getActivity()).showStateString(id, false);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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


    private void initAlarmDialog(byte repeatday) {
        switch (repeatday) {
            // 选中周一
            case 2:
                weekDay = repeatday;
                monday.setTextColor(getResources().getColor(R.color.colorPrimary));
                tuesday.setTextColor(getResources().getColor(R.color.text_color));
                wednesday.setTextColor(getResources().getColor(R.color.text_color));
                thursday.setTextColor(getResources().getColor(R.color.text_color));
                friday.setTextColor(getResources().getColor(R.color.text_color));
                saturday.setTextColor(getResources().getColor(R.color.text_color));
                sunday.setTextColor(getResources().getColor(R.color.text_color));
                break;
            // 选中周二
            case 3:
                monday.setTextColor(getResources().getColor(R.color.text_color));
                tuesday.setTextColor(getResources().getColor(R.color.colorPrimary));
                wednesday.setTextColor(getResources().getColor(R.color.text_color));
                thursday.setTextColor(getResources().getColor(R.color.text_color));
                friday.setTextColor(getResources().getColor(R.color.text_color));
                saturday.setTextColor(getResources().getColor(R.color.text_color));
                sunday.setTextColor(getResources().getColor(R.color.text_color));
                weekDay = repeatday;
                break;
            // 选中周三
            case 4:
                weekDay = repeatday;
                monday.setTextColor(getResources().getColor(R.color.text_color));
                tuesday.setTextColor(getResources().getColor(R.color.text_color));
                wednesday.setTextColor(getResources().getColor(R.color.colorPrimary));
                thursday.setTextColor(getResources().getColor(R.color.text_color));
                friday.setTextColor(getResources().getColor(R.color.text_color));
                saturday.setTextColor(getResources().getColor(R.color.text_color));
                sunday.setTextColor(getResources().getColor(R.color.text_color));
                break;
            // 选中周四
            case 5:
                weekDay = repeatday;
                monday.setTextColor(getResources().getColor(R.color.text_color));
                tuesday.setTextColor(getResources().getColor(R.color.text_color));
                wednesday.setTextColor(getResources().getColor(R.color.text_color));
                thursday.setTextColor(getResources().getColor(R.color.colorPrimary));
                friday.setTextColor(getResources().getColor(R.color.text_color));
                saturday.setTextColor(getResources().getColor(R.color.text_color));
                sunday.setTextColor(getResources().getColor(R.color.text_color));
                break;
            // 选中周五
            case 6:
                weekDay = repeatday;
                monday.setTextColor(getResources().getColor(R.color.text_color));
                tuesday.setTextColor(getResources().getColor(R.color.text_color));
                wednesday.setTextColor(getResources().getColor(R.color.text_color));
                thursday.setTextColor(getResources().getColor(R.color.text_color));
                friday.setTextColor(getResources().getColor(R.color.colorPrimary));
                saturday.setTextColor(getResources().getColor(R.color.text_color));
                sunday.setTextColor(getResources().getColor(R.color.text_color));
                break;
            // 选中周六
            case 7:
                weekDay = repeatday;
                monday.setTextColor(getResources().getColor(R.color.text_color));
                tuesday.setTextColor(getResources().getColor(R.color.text_color));
                wednesday.setTextColor(getResources().getColor(R.color.text_color));
                thursday.setTextColor(getResources().getColor(R.color.text_color));
                friday.setTextColor(getResources().getColor(R.color.text_color));
                saturday.setTextColor(getResources().getColor(R.color.colorPrimary));
                sunday.setTextColor(getResources().getColor(R.color.text_color));
                break;
            // 选中周日
            case 1:
                weekDay = repeatday;
                monday.setTextColor(getResources().getColor(R.color.text_color));
                tuesday.setTextColor(getResources().getColor(R.color.text_color));
                wednesday.setTextColor(getResources().getColor(R.color.text_color));
                thursday.setTextColor(getResources().getColor(R.color.text_color));
                friday.setTextColor(getResources().getColor(R.color.text_color));
                saturday.setTextColor(getResources().getColor(R.color.text_color));
                sunday.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 选中周一
            case R.id.tag_btn_monday:
                initAlarmDialog((byte) 2);
                break;
            // 选中周二
            case R.id.tag_btn_tuesday:
                initAlarmDialog((byte) 3);

                weekDay = 3;
                break;
            // 选中周三
            case R.id.tag_btn_wednesday:
                initAlarmDialog((byte) 4);

                break;
            // 选中周四
            case R.id.tag_btn_thursday:
                initAlarmDialog((byte) 5);
                break;
            // 选中周五
            case R.id.tog_btn_friday:
                initAlarmDialog((byte) 6);
                break;
            // 选中周六
            case R.id.tag_btn_saturday:
                initAlarmDialog((byte) 7);
                break;
            // 选中周日
            case R.id.tag_btn_sunday:
                initAlarmDialog((byte) 1);
                break;
        }
    }
}