package com.nevowatch.nevo.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.OTAActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.StepPickerView;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.OtaController;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.request.GetBatteryLevelNevoRequest;
import com.nevowatch.nevo.ble.model.request.NumberOfStepsGoal;
import com.nevowatch.nevo.ble.util.Constants;

/**
 * GoalFragment aims to set goals including Moderate, Intensive, Sportive and Custom
 */
public class MyNevoFragment extends Fragment implements View.OnClickListener,OnSyncControllerListener {


    private static final String TAG="MyNevoFragment";
    public static final String MYNEVOFRAGMENT = "MyNevoFragment";
    public static final int MYNEVOPOSITION = 4;
    private Context mCtx;
    private Button mynevo_pushOTAButton;
    private ImageView mNevoBatteryImage;
    private TextView mNameTextView;
    private TextView mUpdateuTextView;
    private TextView mBatteryValueTextView;
    private TextView mVersionInfoTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mynevo_fragment, container, false);
        mCtx = getActivity();
        mynevo_pushOTAButton = (Button) rootView.findViewById(R.id.mynevo_push_ota);
        mynevo_pushOTAButton.setOnClickListener(this);

        mNevoBatteryImage = (ImageView) rootView.findViewById(R.id.batteryImage);
        mNameTextView = (TextView) rootView.findViewById(R.id.mynevoTextView);
        mUpdateuTextView = (TextView) rootView.findViewById(R.id.updateuTextView);
        mBatteryValueTextView = (TextView) rootView.findViewById(R.id.batteryValueTextView);
        mUpdateuTextView.setText(getString(R.string.lastestSyncDate) + " " + getActivity().getSharedPreferences(OtaController.PREF_NAME, 0).getString(OtaController.SYNCDATE, ""));

        mVersionInfoTextView = (TextView) rootView.findViewById(R.id.textVersionInfo);

        mVersionInfoTextView.setText(getString(R.string.mcu_version)
                                    + SyncController.Singleton.getInstance(getActivity()).getSoftwareVersion()
                                    + " , "
                                    + getString(R.string.ble_version)
                                    + SyncController.Singleton.getInstance(getActivity()).getFirmwareVersion()
                                    );
        //show MCU/BLE version

        /*
        * my nevo 电量显示设置,初始值 2
        * */
        setBatteryValueText(2);
        View [] viewArray = new View []{
                rootView.findViewById(R.id.mynevoTextView),
                rootView.findViewById(R.id.updateuTextView),
                rootView.findViewById(R.id.batteryValueTextView)
        };
        FontManager.changeFonts(viewArray, getActivity());
        return rootView;
    }

    /*
    * 显示nevo电池电量值函数
    * batteryValue：0，1，2
    * */
    public void setBatteryValueText(int batteryValue) {
        if (batteryValue == 0){
            mBatteryValueTextView.setText(">10%");
            mNevoBatteryImage.getDrawable().setLevel(0);
        }else if (batteryValue == 1){
            mBatteryValueTextView.setText("<50%");
            mNevoBatteryImage.getDrawable().setLevel(1);
        }else if (batteryValue == 2){
            mBatteryValueTextView.setText("100%");
            mNevoBatteryImage.getDrawable().setLevel(2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SyncController.Singleton.getInstance(getActivity()).isConnected())
            SyncController.Singleton.getInstance(getActivity()).getBatteryLevel();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.mynevo_push_ota:
                Intent intent = new Intent(mCtx, OTAActivity.class);
                mCtx.startActivity(intent);
                break;
            default:
                break;
        }

    }


    @Override
    public void packetReceived(NevoPacket packet) {
        if((byte) GetBatteryLevelNevoRequest.HEADER == packet.getHeader())
        {
            final byte value = packet.newBatteryLevelNevoPacket().getBatteryLevel();
            Log.i(TAG, "Battery level:" + value);//0,1,2
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //show value or IOCN
                    setBatteryValueText(value);
                }
            });
        }
    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        ((MainActivity)getActivity()).replaceFragment(isConnected?MyNevoFragment.MYNEVOPOSITION:ConnectAnimationFragment.CONNECTPOSITION, isConnected?MyNevoFragment.MYNEVOFRAGMENT:ConnectAnimationFragment.CONNECTFRAGMENT);
    }
    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mVersionInfoTextView.setText(getString(R.string.mcu_version)
                                + SyncController.Singleton.getInstance(getActivity()).getSoftwareVersion()
                                + " , "
                                + getString(R.string.ble_version)
                                + SyncController.Singleton.getInstance(getActivity()).getFirmwareVersion()
                );
            }
        });

    }
}