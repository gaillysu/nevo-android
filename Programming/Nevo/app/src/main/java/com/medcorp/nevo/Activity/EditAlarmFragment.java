package com.medcorp.nevo.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.base.BasePreferencesFragment;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.view.ToastHelper;

/**
 * Created by karl-john on 18/12/15.
 */
public class EditAlarmFragment extends BasePreferencesFragment{

    private Alarm alarm;
    private Preference alarmPreferences;
    private EditTextPreference labelPreferences;
    private Preference deletePreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.edit_alarm);
        Bundle bundle =  getArguments();
        alarm = getModel().getAlarmById(bundle.getInt("Alarm_ID"));
        getAppCompatActivity().supportInvalidateOptionsMenu();
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_menu:
                if(getModel().updateAlarm(alarm)){
                    ToastHelper.showShortToast(getContext(),"Saved alarm!");
                    getAppCompatActivity().finish();
                }else{
                    ToastHelper.showShortToast(getContext(),"Couldn't save the alarm.!");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        alarmPreferences = findPreference("fragment_edit_alarm_edit_alarm");
        alarmPreferences.setTitle(alarm.toString());
        alarmPreferences.setOnPreferenceClickListener(alarmPrefClickListener);
        labelPreferences = (EditTextPreference) findPreference("fragment_edit_alarm_label");
        labelPreferences.setTitle(alarm.getLabel());
        labelPreferences.setOnPreferenceChangeListener(labelPrefListener);
        deletePreferences = findPreference("fragment_edit_alarm_delete");
        deletePreferences.setOnPreferenceClickListener(deletePrefClickListener);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    Preference.OnPreferenceClickListener alarmPrefClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            Log.w("Karl","On Alarm pref click");
            Dialog alarmDialog = new TimePickerDialog(getContext(), R.style.NevoDialogStyle, timeSetListener, alarm.getHour(), alarm.getMinute(), true);
            alarmDialog.setTitle("Add Alarm");
            alarmDialog.show();
            return true;
        }
    } ;

    Preference.OnPreferenceChangeListener labelPrefListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String changedLabel = o.toString();
            preference.setTitle(changedLabel);
            alarm.setLabel(changedLabel);
            return true;
        }
    };

    Preference.OnPreferenceClickListener deletePrefClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(!getModel().deleleteAlarm(alarm)){
                ToastHelper.showShortToast(getContext(), "Failed to delete alarm");
            }else{
                ToastHelper.showShortToast(getContext(), "Deleted alarm!");
            }
            getAppCompatActivity().finish();
            return true;
        }
    } ;

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            alarm.setHour(hourOfDay);
            alarm.setMinute(minute);
            if(!getModel().updateAlarm(alarm)){
                ToastHelper.showShortToast(getContext(), "Failed to change alarm");
            }else{
                alarmPreferences.setTitle(alarm.toString());
            }
        }
    };
}