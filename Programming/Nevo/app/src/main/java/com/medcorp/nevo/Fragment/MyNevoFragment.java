package com.medcorp.nevo.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.medcorp.nevo.FontManager;
import com.medcorp.nevo.MainActivity;
import com.medcorp.nevo.OTAActivity;
import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.controller.OnSyncControllerListener;
import com.medcorp.nevo.ble.controller.OtaController;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.model.request.GetBatteryLevelNevoRequest;
import com.medcorp.nevo.ble.util.Constants;

import java.io.IOException;

/**
 * GoalFragment aims to set goals including Moderate, Intensive, Sportive and Custom
 */
public class MyNevoFragment extends Fragment implements View.OnClickListener,OnSyncControllerListener {


    private static final String TAG="MyNevoFragment";
    public static final String MYNEVOFRAGMENT = "MyNevoFragment";
    public static final int MYNEVOPOSITION = 5;
    private Context mCtx;
    private Button mynevo_pushOTAButton;
    private ImageView mNevoBatteryImage;
    private TextView mNameTextView;
    private TextView mUpdateuTextView;
    private TextView mBatteryValueTextView;
    private TextView mVersionInfoTextView;
    private TextView mAppVersionInfoTextView;
    private int mBatteryValue = 0;
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
        mAppVersionInfoTextView = (TextView) rootView.findViewById(R.id.appVersionInfo);
        String version = null;
        try {
            version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mAppVersionInfoTextView.setText(getString(R.string.app_version)+version);
        //show MCU/BLE version
        mynevo_pushOTAButton.setVisibility(needUpdate()?View.VISIBLE:View.INVISIBLE);
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
                //fix bug: https://med-corp.atlassian.net/projects/ONA/issues/ONA-10
                if(!needUpdate())
                {
                    new AlertDialog.Builder(((Activity) mCtx), AlertDialog.THEME_HOLO_LIGHT)
                            .setMessage(R.string.latestversion)
                            .setPositiveButton(android.R.string.ok, null)
                            .setCancelable(false).create().show();
                    return;
                }
                if(mBatteryValue<2 || SyncController.Singleton.getInstance(getActivity()).getMyphoneBattery()<20)
                {
                    //fix bug: https://med-corp.atlassian.net/projects/ONA/issues/ONA-11
                    new AlertDialog.Builder(((Activity) mCtx), AlertDialog.THEME_HOLO_LIGHT)
                            .setTitle(R.string.update_error_lowbattery_title)
                            .setMessage(mBatteryValue<2?R.string.update_error_lowbattery:R.string.update_error_lowbattery_phone)
                            .setPositiveButton(android.R.string.ok, null)
                            .setCancelable(false).create().show();
                    return;
                }
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
            mBatteryValue = value;
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
        ((MainActivity)getActivity()).replaceFragment(isConnected ? MyNevoFragment.MYNEVOPOSITION : ConnectAnimationFragment.CONNECTPOSITION, isConnected ? MyNevoFragment.MYNEVOFRAGMENT : ConnectAnimationFragment.CONNECTFRAGMENT);
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
                mynevo_pushOTAButton.setVisibility(needUpdate()?View.VISIBLE:View.INVISIBLE);
            }
        });

    }
    private Boolean needUpdate()
    {
        String[]files;
        String vString;
        int start;
        int end;

        int  currentSoftwareVersion = 0;
        int  currentFirmwareVersion = 0;

        vString = SyncController.Singleton.getInstance(getActivity()).getSoftwareVersion();
        if(null!=vString)currentSoftwareVersion= Integer.parseInt(vString);
        else return false;

        vString = SyncController.Singleton.getInstance(getActivity()).getFirmwareVersion();
        if(null!=vString)currentFirmwareVersion= Integer.parseInt(vString);
        else return false;

        int buildinSoftwareVersion = 0;
        int buildinFirmwareVersion= 0;

        try {
            files = mCtx.getAssets().list("firmware");
            for(String file:files)
            {
                if(file.contains(".hex"))
                {
                    start  = file.toLowerCase().indexOf("_v");
                    end = file.toLowerCase().indexOf(".hex");
                    vString = file.substring(start+2,end);
                    if(vString != null) buildinFirmwareVersion = Integer.parseInt(vString);
                    else return false;
                }
                if(file.contains(".bin"))
                {
                    start  = file.toLowerCase().indexOf("_v");
                    end = file.toLowerCase().indexOf(".bin");
                    vString = file.substring(start+2,end);
                    if(vString != null) buildinSoftwareVersion = Integer.parseInt(vString);
                    else return false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return (buildinSoftwareVersion>currentSoftwareVersion || buildinFirmwareVersion>currentFirmwareVersion);
    }
}