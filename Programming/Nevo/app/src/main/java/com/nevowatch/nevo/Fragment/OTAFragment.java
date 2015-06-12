package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.Model.Notification.NotificationType;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.NotificationItem;
import com.nevowatch.nevo.View.RoundProgressBar;
import com.nevowatch.nevo.ble.controller.OnNevoOtaControllerListener;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.BatteryLevelNevoPacket;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.request.GetBatteryLevelNevoRequest;
import com.nevowatch.nevo.ble.model.request.GetStepsGoalNevoRequest;
import com.nevowatch.nevo.ble.model.request.NevoOTAStartRequest;
import com.nevowatch.nevo.ble.model.request.NumberOfStepsGoal;
import com.nevowatch.nevo.ble.notification.NevoNotificationListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.nevowatch.nevo.ble.util.Constants;
import com.nevowatch.nevo.ble.util.Constants.DfuFirmwareTypes;
import com.nevowatch.nevo.ble.controller.OtaController;
import com.nevowatch.nevo.ble.util.QueuedMainThreadHandler;

/**
 * NotificationFragment
 */
public class OTAFragment extends Fragment
        implements View.OnClickListener,OnNevoOtaControllerListener {
    private static final String TAG="OTAFragment";
    public static final String OTAFRAGMENT = "OTAFragment";
    public static final int OTAPOSITION = 4;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private RoundProgressBar mOTAProgressBar;
    private TextView mMCUVersionTextView;
    private TextView mBleVersionTextView;
    private TextView mOTAProgressValueTextView;
    private Button mReButton;
    private ImageView mWarningButton;

    boolean isTransferring = false;
    DfuFirmwareTypes enumFirmwareType = DfuFirmwareTypes.APPLICATION;
    String selectedFileURL;
    //save the build-in firmware version, it should be the latest FW version
    int buildinSoftwareVersion = 0;
    int buildinFirmwareVersion= 0;
    ArrayList<String> firmwareURLs = new ArrayList<String>();
    int currentIndex = 0;
    OtaController mNevoOtaController ;
    //save the attached Activity, should be MainActivity, when doing OTA, user perhaps switch other fragment
    //but the OTA should be continue on background. when user come back, the progress should be showing
    Context mContext;
    private AlertDialog mAlertDialog = null;
    private void initListView(boolean forceUpdate,boolean popupMessage){

        if(!mNevoOtaController.isConnected()
                || mNevoOtaController.getSoftwareVersion() == null
                || mNevoOtaController.getFirmwareVersion() == null)
        {
            new AlertDialog.Builder(((Activity)mContext),AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle(R.string.FirmwareUpgrade)
                    .setMessage("Reading firmware version,please wait...")
                    .setNegativeButton("OK",null).show();
            return;
        }

        firmwareURLs.clear();
        currentIndex = 0;

        String[]files;
        int  currentSoftwareVersion = Integer.parseInt(mNevoOtaController.getSoftwareVersion());
        int  currentFirmwareVersion = Integer.parseInt(mNevoOtaController.getFirmwareVersion());
        try {
            files = mContext.getAssets().list("firmware");
            for(String file:files)
            {
                if(file.contains(".bin"))
                {
                    int start  = file.toLowerCase().indexOf("_v");
                    int end = file.toLowerCase().indexOf(".bin");
                    String vString = file.substring(start+2,end);
                    if(vString != null) buildinSoftwareVersion = Integer.parseInt(vString);

                    if(currentSoftwareVersion < buildinSoftwareVersion || forceUpdate)
                    {
                        firmwareURLs.add("firmware/" + file);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            files = mContext.getAssets().list("firmware");
            for(String file:files)
            {
                if(file.contains(".hex"))
                {
                    int start  = file.toLowerCase().indexOf("_v");
                    int end = file.toLowerCase().indexOf(".hex");
                    String vString = file.substring(start+2,end);
                    if(vString != null) buildinFirmwareVersion = Integer.parseInt(vString);
                    if(currentFirmwareVersion < buildinFirmwareVersion || forceUpdate) {
                        firmwareURLs.add("firmware/" + file);
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(firmwareURLs.size()>0)
        {
            String versionInfo = mContext.getString(R.string.currentFWversion)
                                + "("+ currentFirmwareVersion +","+ currentSoftwareVersion+"),"
                                + mContext.getString(R.string.latestFWversion)
                                + "("+ buildinFirmwareVersion +","+ buildinSoftwareVersion+")";

            if(!(mAlertDialog !=null && mAlertDialog.isShowing()) && popupMessage) {
                //mAlertDialog.dismiss();
                //mAlertDialog =null;
                AlertDialog.Builder ab = new AlertDialog.Builder(((Activity) mContext), AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle(R.string.FirmwareUpgrade)
                        .setMessage(versionInfo)
                        .setPositiveButton(android.R.string.cancel, null)
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                uploadPressed();
                            }
                        }).setCancelable(false);

                mAlertDialog = ab.create();
                mAlertDialog.show();
            }
        }
        else
        {
            mOTAProgressValueTextView.setText(mContext.getString(R.string.latestversion));
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNevoOtaController = OtaController.Singleton.getInstance(getActivity());
        mNevoOtaController.setConnectControllerDelegate2Self();
        mNevoOtaController.setOnNevoOtaControllerListener(this);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ota_fragment, container, false);

        mOTAProgressBar = (RoundProgressBar) rootView.findViewById(R.id.otaProgressBar);

        mMCUVersionTextView = (TextView) rootView.findViewById(R.id.mcuVersionLabel);

        mBleVersionTextView = (TextView) rootView.findViewById(R.id.bleVersionLabel);

        mOTAProgressValueTextView = (TextView) rootView.findViewById(R.id.progressValue);

        mReButton = (Button) rootView.findViewById(R.id.reUpgradebutton);
        mReButton.setOnClickListener(this);

        mWarningButton = (ImageView) rootView.findViewById(R.id.warningButton);
        mReButton.setOnClickListener(this);

        View [] viewArray = new View []{
                rootView.findViewById(R.id.mcuVersionLabel),
                rootView.findViewById(R.id.bleVersionLabel),
                rootView.findViewById(R.id.progressValue),
                rootView.findViewById(R.id.reUpgradebutton)
        };
        FontManager.changeFonts(viewArray, getActivity());
        mOTAProgressValueTextView.setTextSize(30.0f);
        mMCUVersionTextView.setText(getString(R.string.mcu_version) + mNevoOtaController.getSoftwareVersion());
        mBleVersionTextView.setText(getString(R.string.ble_version) + mNevoOtaController.getFirmwareVersion());

        initListView(false,true);
        initValue();

        return rootView;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.reUpgradebutton:
                initListView(true,true);
                //uploadPressed();
                break;
            case R.id.warningButton:

                break;
            default:
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(SyncController.Singleton.getInstance(getActivity())!=null && !SyncController.Singleton.getInstance(getActivity()).isConnected()){
            ((MainActivity)getActivity()).replaceFragment(ConnectAnimationFragment.CONNECTPOSITION, ConnectAnimationFragment.CONNECTFRAGMENT);
        }
        else
        {
            if(SyncController.Singleton.getInstance(getActivity())!=null
                    && SyncController.Singleton.getInstance(getActivity()).isConnected()
                    && mNevoOtaController.getState() == Constants.DFUControllerState.INIT)
            {
                SyncController.Singleton.getInstance(getActivity()).getBatteryLevel();
            }
        }
    }
    @Override
    public void packetReceived(NevoPacket packet)
    {
        if((byte) GetBatteryLevelNevoRequest.HEADER == packet.getHeader())
        {
            QueuedMainThreadHandler.getInstance(QueuedMainThreadHandler.QueueType.SyncController).next();

            final byte value = packet.newBatteryLevelNevoPacket().getBatteryLevel();
            Log.e(TAG,"Battery level:"+value);//0,1,2
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //show value or IOCN
                }
            });
        }
    }
    @Override
    public void connectionStateChanged(boolean isConnected) {
        if(mNevoOtaController.getState() == Constants.DFUControllerState.INIT && mContext instanceof MainActivity ) {
            ((MainActivity) mContext).replaceFragment(isConnected ? OTAFragment.OTAPOSITION : ConnectAnimationFragment.CONNECTPOSITION, isConnected ? OTAFragment.OTAFRAGMENT : ConnectAnimationFragment.CONNECTFRAGMENT);
        }
    }

    @Override
    public void onDFUStarted() {
        Log.i(TAG,"onDFUStarted");
    }

    @Override
    public void onDFUCancelled() {
        Log.i(TAG,"onDFUCancelled");
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initValue();
                mNevoOtaController.reset(false);
            }
        });
    }

    @Override
    public void onTransferPercentage(final int percent) {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setOTAProgressBar(percent);
            }
        });
    }

    @Override
    public void onSuccessfulFileTranferred() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                initValue();
                currentIndex = currentIndex + 1;
                if (currentIndex == firmwareURLs.size())
                {
                    String message = ((Activity)mContext).getString(R.string.UpdateSuccess1);
                    if (enumFirmwareType == DfuFirmwareTypes.APPLICATION)
                    {
                        message = ((Activity)mContext).getString(R.string.UpdateSuccess2);
                    }
                    new AlertDialog.Builder(((Activity)mContext),AlertDialog.THEME_HOLO_LIGHT)
                            .setTitle(R.string.FirmwareUpgrade)
                            .setMessage(message)
                            .setNegativeButton("OK",null).show();
                    //show success text or image
                    mOTAProgressValueTextView.setText(R.string.UpdateSuccess1);
                    mNevoOtaController.reset(false);
                }
                else
                {
                    //check MCU OK,first reset and wait 5s do BLE OTA
                    mNevoOtaController.reset(false);

                    mOTAProgressValueTextView.setText(mContext.getString(R.string.waiting));
                    refreshTimeCount(15);
                    //set new state and hide rebutton
                    mNevoOtaController.setState(Constants.DFUControllerState.SEND_RECONNECT);
                    mReButton.setVisibility(View.INVISIBLE);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //wait reconnect OK
                            uploadPressed();
                        }
                    },5000);
                }
            }
        });
    }

    @Override
    public void onError(final String error) {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initValue();
                mOTAProgressValueTextView.setText(error);
                mNevoOtaController.reset(false);
            }
        });
    }

    @Override
    public void firmwareVersionReceived(DfuFirmwareTypes whichfirmware, String version) {

        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMCUVersionTextView.setText(mContext.getString(R.string.mcu_version) + mNevoOtaController.getSoftwareVersion());
                mBleVersionTextView.setText(mContext.getString(R.string.ble_version) + mNevoOtaController.getFirmwareVersion());
                initListView(false,false);
            }
        });
    }

    /**
    *Set the OTA upgrade progress
    */
    public void setOTAProgressBar(final int progress){
        mOTAProgressBar.setProgress(progress);
        mOTAProgressValueTextView.setText(progress+"%");
    }

    //init data function
    private void initValue()
    {
       // nevoOtaView.backButton.enabled = true
        isTransferring = false ;
        mReButton.setVisibility(View.VISIBLE);
        mReButton.setEnabled(mNevoOtaController.isConnected());
    }

    //upload button function
    private void uploadPressed()
    {
        if (currentIndex >= firmwareURLs.size() || firmwareURLs.size() == 0 )
        {
            //check firmwareURLs is null, should hide the button
            new AlertDialog.Builder(((Activity)mContext),AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle(R.string.FirmwareUpgrade)
                    .setMessage("Reading firmware version,please wait...")
                    .setNegativeButton("OK",null).show();
            return;
        }
        selectedFileURL = firmwareURLs.get(currentIndex);
        mOTAProgressValueTextView.setText(mContext.getString(R.string.waiting));

        if (selectedFileURL.contains(".bin"))
        {
            enumFirmwareType = DfuFirmwareTypes.SOFTDEVICE;
        }
        if (selectedFileURL.contains(".hex"))
        {
            enumFirmwareType = DfuFirmwareTypes.APPLICATION;
            refreshTimeCount(10);
        }
        mOTAProgressBar.setProgress(0);
        isTransferring = true;
        //when doing OTA, disable Cancel/Back button, enable them by callback function invoke initValue()/checkConnection()
        //nevoOtaView.backButton.enabled = false
        mReButton.setVisibility(View.INVISIBLE); //The process of OTA hide this control
        mNevoOtaController.performDFUOnFile(selectedFileURL, enumFirmwareType);

    }

    private void refreshTimeCount(final int count)
    {
        if(count == 0) return;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOTAProgressValueTextView.setText(mContext.getString(R.string.waiting) + count);
                    }
                });
                refreshTimeCount(count-1);
            }
        },1000);
    }

}
