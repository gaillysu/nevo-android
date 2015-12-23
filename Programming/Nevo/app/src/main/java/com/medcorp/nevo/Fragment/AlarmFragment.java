package com.medcorp.nevo.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.text.InputType;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.EditAlarmActivity;
import com.medcorp.nevo.activity.EditAlarmFragment;
import com.medcorp.nevo.adapter.AlarmArrayAdapter;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.fragment.base.BasePreferencesFragment;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.Battery;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 11/12/15.
 */
public class AlarmFragment extends BaseObservableFragment implements  TimePickerDialog.OnTimeSetListener, AdapterView.OnItemClickListener{

    @Bind(R.id.fragment_alarm_list_view)
    ListView alarmListView;

    private List<Alarm> alarmList;

    private AlarmArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        ButterKnife.bind(this, view);
        alarmList = getModel().getAllAlarm();
        adapter = new AlarmArrayAdapter(getContext(), 0, alarmList);
        alarmListView.setAdapter(adapter);
        alarmListView.setOnItemClickListener(this);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w("Karl","edit alarm fragment " + item.getItemId());
        switch (item.getItemId()){
            case R.id.add_menu:
                Dialog alarmDialog = new TimePickerDialog(getContext(), R.style.NevoDialogStyle, this, 8, 0, true);
                alarmDialog.setTitle("Add Alarm");
                alarmDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(true);
        getAppCompatActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getAppCompatActivity().getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
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
                        alarmList = getModel().getAllAlarm();
                        adapter.clear();
                        adapter.addAll(alarmList);
                        adapter.notifyDataSetChanged();
                        ;
                    }
                }).negativeText("Cancel")
                .show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getContext(), EditAlarmActivity.class);
        Log.w("Karl","Test");
        Bundle bundle = new Bundle();
        bundle.putInt("Alarm_ID", alarmList.get(position).getId());
        i.putExtras(bundle);
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        prefs.putString("fragment_edit_alarm_label", alarmList.get(position).getLabel());
        prefs.commit();
        getAppCompatActivity().startActivity(i);
        getAppCompatActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
        setEnterTransition(new Fade().setDuration(300));
    }

}