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
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.request.NevoOTAStartRequest;
import com.nevowatch.nevo.ble.model.request.NumberOfStepsGoal;
import com.nevowatch.nevo.ble.notification.NevoNotificationListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nevowatch.nevo.ble.util.Constants;
import com.nevowatch.nevo.ble.util.Constants.DfuFirmwareTypes;
import com.nevowatch.nevo.ble.controller.OtaController;
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
    int buildinSoftwareVersion = 17;
    int buildinFirmwareVersion= 31;
    ArrayList<String> firmwareURLs = new ArrayList<String>();
    int currentIndex = 0;
    OtaController mNevoOtaController ;
    //save the attached Activity, should be MainActivity, when doing OTA, user perhaps switch other fragment
    //but the OTA should be continue on background. when user come back, the progress should be showing
    Context mContext;
    private void initListView(){

        if(!mNevoOtaController.isConnected()
                || mNevoOtaController.getSoftwareVersion() == null
                || mNevoOtaController.getFirmwareVersion() == null) return;

        firmwareURLs.clear();

        String[]files;
        int  currentSoftwareVersion = Integer.parseInt(mNevoOtaController.getSoftwareVersion());
        int  currentFirmwareVersion = Integer.parseInt(mNevoOtaController.getFirmwareVersion());
        try {
            files = getActivity().getAssets().list("firmware");
            for(String file:files)
            {
                if(file.contains(".bin")&& currentSoftwareVersion < buildinSoftwareVersion)
                {
                    firmwareURLs.add("firmware/"+file);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            files = mContext.getAssets().list("firmware");
            for(String file:files)
            {
                if(file.contains(".hex")&& currentFirmwareVersion < buildinFirmwareVersion)
                {
                    firmwareURLs.add("firmware/"+file);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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

        mMCUVersionTextView.setText(getString(R.string.mcu_version) + mNevoOtaController.getSoftwareVersion());
        mBleVersionTextView.setText(getString(R.string.ble_version) + mNevoOtaController.getFirmwareVersion());

        initListView();
        initValue();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(!isTransferring)
        {
           // mNevoOtaController.reset(true);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.reUpgradebutton:
                currentIndex = 0;
                uploadPressed();
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
    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        if(!isTransferring && mContext instanceof MainActivity ) {
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
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadPressed();
                        }
                    },1000);
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
                mMCUVersionTextView.setText(getString(R.string.mcu_version) + mNevoOtaController.getSoftwareVersion());
                mBleVersionTextView.setText(getString(R.string.ble_version) + mNevoOtaController.getFirmwareVersion());
                initListView();
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
                    .setMessage("pelease wait for reading firmware version")
                    .setNegativeButton("OK",null).show();
            return;
        }
        selectedFileURL = firmwareURLs.get(currentIndex);

        if (selectedFileURL.contains(".bin"))
        {
            enumFirmwareType = DfuFirmwareTypes.SOFTDEVICE;
        }
        if (selectedFileURL.contains(".hex"))
        {
            enumFirmwareType = DfuFirmwareTypes.APPLICATION;
        }

        mOTAProgressBar.setProgress(0);
        mOTAProgressValueTextView.setText(R.string.waiting);
        isTransferring = true;
        //when doing OTA, disable Cancel/Back button, enable them by callback function invoke initValue()/checkConnection()
        //nevoOtaView.backButton.enabled = false
        mReButton.setVisibility(View.INVISIBLE); //The process of OTA hide this control
        mNevoOtaController.performDFUOnFile(selectedFileURL, enumFirmwareType);

    }

}
