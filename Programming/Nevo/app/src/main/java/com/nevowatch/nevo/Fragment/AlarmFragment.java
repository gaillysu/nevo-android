package com.nevowatch.nevo.Fragment;

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

import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.Model.Alarm;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.View.TimePickerView;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.util.Constants;

import java.util.ArrayList;


/**
 * AlarmFragment, it works for setting alarm and turning alarm on or off.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener, TimePickerView.TimePickerFragmentCallbacks,OnSyncControllerListener {


    public static final String ALARMFRAGMENT = "AlarmFragment";
    public static final int ALARMPOSITION = 2;
    private TextView mClockTextView;
    private ImageView mEditClockImage;
    private Button mOnButton;
    private Button mOffButton;
    private static final String PREF_KEY_CLOCK_STATE = "clockState";
    private static final String PREF_KEY_CLOCK_STATE2 = "clockState2";
    private static final String PREF_KEY_CLOCK_STATE3 = "clockState3";
    private int mCurrentIndex = 0; //Alarm index, values: 0,1,2
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

        View [] viewArray = new View []{
                rootView.findViewById(R.id.clock_textView),
                rootView.findViewById(R.id.on_mode_button),
                rootView.findViewById(R.id.off_mode_button)
        };
        FontManager.changeFonts(viewArray,getActivity());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO ,fixed to 3 alarm TextView
        mClockTextView.setText(TimePickerView.getAlarmFromPreference(0,getActivity()));
        lightClockState(AlarmFragment.getClockStateFromPreference(0,getActivity()));
    }

    private void lightClockState(boolean enable){
        if(enable){
            mOffButton.setTextColor(getResources().getColor(R.color.black));
            mOnButton.setTextColor(getResources().getColor(R.color.white));
            mOffButton.setSelected(false);
            mOnButton.setSelected(true);
        }else {
            mOffButton.setTextColor(getResources().getColor(R.color.white));
            mOnButton.setTextColor(getResources().getColor(R.color.black));
            mOffButton.setSelected(true);
            mOnButton.setSelected(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_clock_imageButton:
            case R.id.clock_textView:
                    mClockTextView.setClickable(false);
                    mEditClockImage.setClickable(false);
                    showTimePickerDialog(mCurrentIndex);
                break;
            case R.id.on_mode_button:
                lightClockState(true);
                AlarmFragment.saveClockStateToPreference(mCurrentIndex,getActivity(), true);
                setAlarm();
                break;
            case R.id.off_mode_button:
                lightClockState(false);
                AlarmFragment.saveClockStateToPreference(mCurrentIndex,getActivity(), false);
                setAlarm();
                break;
            default:
                break;
        }

    }

    private void setAlarm()
    {
        ArrayList<Alarm> list = new ArrayList<Alarm>();

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
        //TODO: fixed to 3 Clock view,index means which one alarm
        mClockTextView.setText(time);
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
