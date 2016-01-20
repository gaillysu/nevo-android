package com.medcorp.nevo.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.adapter.AlarmEditAdapter;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.view.ToastHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 21/12/15.
 */
public class EditAlarmActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_edit_alarm_list_view)
    ListView listView;

    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        //TODO save keys to XML
        alarm = getModel().getAlarmById(bundle.getInt("Alarm_ID"));
        listView.setAdapter(new AlarmEditAdapter(this,alarm));
        listView.setOnItemClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_menu:
                if(getModel().updateAlarm(alarm)){
                    //TODO save to Strings.xml
                    ToastHelper.showShortToast(this, "Saved alarm!");
                    finish();
                }else{
                    //TODO save to Strings.xml
                    ToastHelper.showShortToast(this,"Couldn't save the alarm.!");
                }
                return true;
            default:
                finish();
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0){
            Dialog alarmDialog = new TimePickerDialog(this, R.style.NevoDialogStyle, timeSetListener, alarm.getHour(), alarm.getMinute(), true);
            //TODO save to Strings.xml
            alarmDialog.setTitle("Edit Alarm");
            alarmDialog.show();
        }else if(position == 1){
            //TODO save to Strings.xml
            new MaterialDialog.Builder(EditAlarmActivity.this)
                    .title("Edit Alarm")
                    .content("Label your alarm.")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("label name", alarm.getLabel(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if(input.length()==0) return;
                            alarm.setLabel(input.toString());
                            listView.setAdapter(new AlarmEditAdapter(EditAlarmActivity.this, alarm));
                        }
                    }).negativeText("Cancel")
                    .show();
        }else if(position == 2){
            if(!getModel().deleteAlarm(alarm)){
                //TODO save to Strings.xml
                ToastHelper.showShortToast(EditAlarmActivity.this, "Failed to delete alarm");
            }else{
                //TODO save to Strings.xml
                ToastHelper.showShortToast(EditAlarmActivity.this, "Deleted alarm!");
            }
            finish();
        }
    }

    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            alarm.setHour(hourOfDay);
            alarm.setMinute(minute);
            if(!getModel().updateAlarm(alarm)){
                //TODO save to Strings.xml
                ToastHelper.showShortToast(EditAlarmActivity.this, "Failed to change alarm");
            }else{
                listView.setAdapter(new AlarmEditAdapter(EditAlarmActivity.this,alarm));
            }
        }
    };
}
