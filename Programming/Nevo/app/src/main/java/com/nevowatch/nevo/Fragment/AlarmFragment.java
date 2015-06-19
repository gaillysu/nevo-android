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
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.View.TimePickerView;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.util.Constants;


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
        mClockTextView.setText(TimePickerView.getAlarmFromPreference(getActivity()));
        lightClockState(AlarmFragment.getClockStateFromPreference(getActivity()));
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
                    showTimePickerDialog();
                break;
            case R.id.on_mode_button:
                lightClockState(true);
                AlarmFragment.saveClockStateToPreference(getActivity(), true);
                String[] strAlarm = TimePickerView.getAlarmFromPreference(getActivity()).split(":");
                SyncController.Singleton.getInstance(getActivity()).setAlarm(Integer.parseInt(strAlarm[0]),
                        Integer.parseInt(strAlarm[1]),
                        true);
                break;
            case R.id.off_mode_button:
                lightClockState(false);
                AlarmFragment.saveClockStateToPreference(getActivity(), false);
                String[] strAlarmOff = TimePickerView.getAlarmFromPreference(getActivity()).split(":");
                SyncController.Singleton.getInstance(getActivity()).setAlarm(Integer.parseInt(strAlarmOff[0]),
                        Integer.parseInt(strAlarmOff[1]),
                        false);
                break;
            default:
                break;
        }

    }

    public void setClock(final String time){
        mClockTextView.setText(time);
        /*when user click Alarm on/off button , or select new Alarm time, all the three cases,need call mSyncController.setAlarm(...)*/
        String[] strAlarm = time.split(":");
        SyncController.Singleton.getInstance(getActivity()).setAlarm(Integer.parseInt(strAlarm[0]),
                Integer.parseInt(strAlarm[1]),
                AlarmFragment.getClockStateFromPreference(getActivity()));
    }

    /**
     * Show Time in a dialog
     * */
    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerView();
        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        mClockTextView.setClickable(true);
        mEditClockImage.setClickable(true);
    }

    @Override
    public void setClockTime(String clockTime) {
        setClock(clockTime);
    }

    public static void saveClockStateToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(PREF_KEY_CLOCK_STATE, value).apply();
    }

    public static Boolean getClockStateFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_KEY_CLOCK_STATE, false);
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
