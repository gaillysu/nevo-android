package com.medcorp.nevo.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.Model.Alarm;
import com.medcorp.nevo.view.TimePickerView;

import java.util.ArrayList;
import java.util.List;


/**
 * AlarmFragment, it works for setting alarm and turning alarm on or off.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener, TimePickerView.TimePickerFragmentCallbacks,OnSyncControllerListener {


    public static final String ALARMFRAGMENT = "AlarmFragment";
    public static final int ALARMPOSITION = 3;
    private TextView mClockTextView;
    private ImageView mEditClockImage;
    private Button mOnButton;
    private Button mOffButton;

    private TextView mClockTextView2;
    private ImageView mEditClockImage2;
    private Button mOnButton2;
    private Button mOffButton2;

    private TextView mClockTextView3;
    private ImageView mEditClockImage3;
    private Button mOnButton3;
    private Button mOffButton3;

    private static final String PREF_KEY_CLOCK_STATE = "clockState";
    private static final String PREF_KEY_CLOCK_STATE2 = "clockState2";
    private static final String PREF_KEY_CLOCK_STATE3 = "clockState3";
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.alarm_fragment, container, false);

        mClockTextView = (TextView) rootView.findViewById(R.id.clock_textView);
        mClockTextView.setOnClickListener(this);
        mEditClockImage = (ImageView) rootView.findViewById(R.id.edit_clock_imageButton);
        mEditClockImage.setOnClickListener(this);
        mOnButton =  (Button)rootView.findViewById(R.id.on_mode_button);
        mOnButton.setOnClickListener(this);
        mOffButton =  (Button)rootView.findViewById(R.id.off_mode_button);
        mOffButton.setOnClickListener(this);

        mClockTextView2 = (TextView) rootView.findViewById(R.id.clock_textView2);
        mClockTextView2.setOnClickListener(this);
        mEditClockImage2 = (ImageView) rootView.findViewById(R.id.edit_clock_imageButton2);
        mEditClockImage2.setOnClickListener(this);
        mOnButton2 =  (Button)rootView.findViewById(R.id.on_mode_button2);
        mOnButton2.setOnClickListener(this);
        mOffButton2 =  (Button)rootView.findViewById(R.id.off_mode_button2);
        mOffButton2.setOnClickListener(this);

        mClockTextView3 = (TextView) rootView.findViewById(R.id.clock_textView3);
        mClockTextView3.setOnClickListener(this);
        mEditClockImage3 = (ImageView) rootView.findViewById(R.id.edit_clock_imageButton3);
        mEditClockImage3.setOnClickListener(this);
        mOnButton3 =  (Button)rootView.findViewById(R.id.on_mode_button3);
        mOnButton3.setOnClickListener(this);
        mOffButton3 =  (Button)rootView.findViewById(R.id.off_mode_button3);
        mOffButton3.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mClockTextView.setText(TimePickerView.getAlarmFromPreference(0,getActivity()));
        lightClockState(0,AlarmFragment.getClockStateFromPreference(0,getActivity()));
        mClockTextView2.setText(TimePickerView.getAlarmFromPreference(1,getActivity()));
        lightClockState(1,AlarmFragment.getClockStateFromPreference(1,getActivity()));
        mClockTextView3.setText(TimePickerView.getAlarmFromPreference(2,getActivity()));
        lightClockState(2,AlarmFragment.getClockStateFromPreference(2,getActivity()));
    }

    private void lightClockState(int index,boolean enable){
        if(enable){
            if(index == 0) {
                mOffButton.setTextColor(getResources().getColor(R.color.black));
                mOnButton.setTextColor(getResources().getColor(R.color.white));
                mOffButton.setSelected(false);
                mOnButton.setSelected(true);
            }
            if(index == 1) {
                mOffButton2.setTextColor(getResources().getColor(R.color.black));
                mOnButton2.setTextColor(getResources().getColor(R.color.white));
                mOffButton2.setSelected(false);
                mOnButton2.setSelected(true);
            }
            if(index == 2) {
                mOffButton3.setTextColor(getResources().getColor(R.color.black));
                mOnButton3.setTextColor(getResources().getColor(R.color.white));
                mOffButton3.setSelected(false);
                mOnButton3.setSelected(true);
            }
        }else {
            if(index == 0) {
                mOffButton.setTextColor(getResources().getColor(R.color.white));
                mOnButton.setTextColor(getResources().getColor(R.color.black));
                mOffButton.setSelected(true);
                mOnButton.setSelected(false);
            }
            if(index == 1) {
                mOffButton2.setTextColor(getResources().getColor(R.color.white));
                mOnButton2.setTextColor(getResources().getColor(R.color.black));
                mOffButton2.setSelected(true);
                mOnButton2.setSelected(false);
            }
            if(index == 2) {
                mOffButton3.setTextColor(getResources().getColor(R.color.white));
                mOnButton3.setTextColor(getResources().getColor(R.color.black));
                mOffButton3.setSelected(true);
                mOnButton3.setSelected(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_clock_imageButton:
            case R.id.clock_textView:
                    mClockTextView.setClickable(false);
                    mEditClockImage.setClickable(false);
                    showTimePickerDialog(0);
                break;
            case R.id.on_mode_button:
                lightClockState(0,true);
                AlarmFragment.saveClockStateToPreference(0,getActivity(), true);
                setAlarm();
                break;
            case R.id.off_mode_button:
                lightClockState(0,false);
                AlarmFragment.saveClockStateToPreference(0,getActivity(), false);
                setAlarm();
                break;
            case R.id.edit_clock_imageButton2:
            case R.id.clock_textView2:
                mClockTextView2.setClickable(false);
                mEditClockImage2.setClickable(false);
                showTimePickerDialog(1);
                break;
            case R.id.on_mode_button2:
                lightClockState(1,true);
                AlarmFragment.saveClockStateToPreference(1,getActivity(), true);
                setAlarm();
                break;
            case R.id.off_mode_button2:
                lightClockState(1,false);
                AlarmFragment.saveClockStateToPreference(1,getActivity(), false);
                setAlarm();
                break;
            case R.id.edit_clock_imageButton3:
            case R.id.clock_textView3:
                mClockTextView3.setClickable(false);
                mEditClockImage3.setClickable(false);
                showTimePickerDialog(2);
                break;
            case R.id.on_mode_button3:
                lightClockState(2,true);
                AlarmFragment.saveClockStateToPreference(2,getActivity(), true);
                setAlarm();
                break;
            case R.id.off_mode_button3:
                lightClockState(2,false);
                AlarmFragment.saveClockStateToPreference(2,getActivity(), false);
                setAlarm();
                break;
            default:
                break;
        }

    }

    private void setAlarm()
    {
        List<Alarm> list = new ArrayList<Alarm>();

        String[] strAlarm = TimePickerView.getAlarmFromPreference(0,getActivity()).split(":");
        Boolean onOff = AlarmFragment.getClockStateFromPreference(0,getActivity());
        list.add(new Alarm(0,Integer.parseInt(strAlarm[0]),Integer.parseInt(strAlarm[1]),onOff));
        strAlarm = TimePickerView.getAlarmFromPreference(1,getActivity()).split(":");
        onOff = AlarmFragment.getClockStateFromPreference(1,getActivity());
        list.add(new Alarm(1,Integer.parseInt(strAlarm[0]),Integer.parseInt(strAlarm[1]),onOff));
        strAlarm = TimePickerView.getAlarmFromPreference(2,getActivity()).split(":");
        onOff = AlarmFragment.getClockStateFromPreference(2,getActivity());
        list.add(new Alarm(2,Integer.parseInt(strAlarm[0]),Integer.parseInt(strAlarm[1]),onOff));

        SyncController.Singleton.getInstance(getActivity()).setAlarm(list);
    }
    public void setClock(int index,final String time){
        switch (index){
            case 0:
                mClockTextView.setText(time);
                break;
            case 1:
                mClockTextView2.setText(time);
                break;
            case 2:
                mClockTextView3.setText(time);
                break;

        }
        /*when user click Alarm on/off button , or select new Alarm time, all the three cases,need call mSyncController.setAlarm(...)*/
        setAlarm();
    }

    /**
     * Show Time in a dialog
     * */
    public void showTimePickerDialog(int index) {
        DialogFragment newFragment = new TimePickerView();
        Bundle bundle = new Bundle();
        bundle.putInt("AlarmIndex", index);
        newFragment.setArguments(bundle);
        //mCurrentIndex
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        mClockTextView.setClickable(true);
        mEditClockImage.setClickable(true);
    }

    @Override
    public void setClockTime(int index,String clockTime) {
        setClock(index,clockTime);
    }

    public static void saveClockStateToPreference(int index,Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(index == 0) pref.edit().putBoolean(PREF_KEY_CLOCK_STATE, value).apply();
        if(index == 1) pref.edit().putBoolean(PREF_KEY_CLOCK_STATE2, value).apply();
        if(index == 2) pref.edit().putBoolean(PREF_KEY_CLOCK_STATE3, value).apply();
    }

    public static Boolean getClockStateFromPreference(int index,Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(index == 0) return pref.getBoolean(PREF_KEY_CLOCK_STATE, false);
        if(index == 1) return pref.getBoolean(PREF_KEY_CLOCK_STATE2, false);
        if(index == 2) return pref.getBoolean(PREF_KEY_CLOCK_STATE3, false);
        return false;
    }

    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        ((MainActivity)getActivity()).replaceFragment(isConnected?AlarmFragment.ALARMPOSITION:ConnectAnimationFragment.CONNECTPOSITION, isConnected?AlarmFragment.ALARMFRAGMENT:ConnectAnimationFragment.CONNECTFRAGMENT);
    }
    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

    }
}
