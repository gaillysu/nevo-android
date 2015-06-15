package com.nevowatch.nevo;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
public class OTAActivity extends Activity
        implements View.OnClickListener,OnNevoOtaControllerListener {
    private static final String TAG="OTAActivity";
    public static final String OTAACTIVITY = "OTAActivity";
    public static final int OTAPOSITION = 4;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private RoundProgressBar mOTAProgressBar;
    private TextView mMCUVersionTextView;
    private TextView mBleVersionTextView;
    private TextView mOTAProgressValueTextView;
    private Button mReButton;
    private ImageView mWarningButton;
    private ImageView mBackImage;
    private TextView mTitleTextView;

    static DfuFirmwareTypes enumFirmwareType = DfuFirmwareTypes.APPLICATION;
    //save the build-in firmware version, it should be the latest FW version
    int buildinSoftwareVersion = 0;
    int buildinFirmwareVersion= 0;
    private static ArrayList<String> firmwareURLs = new ArrayList<String>();
    private static int currentIndex = 0;
    OtaController mNevoOtaController ;
    //save the attached Activity, should be MainActivity, when doing OTA, user perhaps switch other fragment
    //but the OTA should be continue on background. when user come back, the progress should be showing
    Context mContext;
    private static AlertDialog mAlertDialog = null;
    private void initListView(boolean forceUpdate,boolean popupMessage){

        if(mNevoOtaController.getSoftwareVersion() == null
                || mNevoOtaController.getFirmwareVersion() == null
                || mNevoOtaController.getState() != Constants.DFUControllerState.INIT)
        {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ota_activity);
        initView();

        View [] viewArray = new View []{
                findViewById(R.id.mcuVersionLabel),
                findViewById(R.id.bleVersionLabel),
                findViewById(R.id.progressValue),
                findViewById(R.id.reUpgradebutton)
        };
        FontManager.changeFonts(viewArray, this);


        initListView(false,true);
        initValue();
    }

    private void initView(){
        mContext = this;

        mOTAProgressBar = (RoundProgressBar)findViewById(R.id.otaProgressBar);

        mMCUVersionTextView = (TextView)findViewById(R.id.mcuVersionLabel);

        mBleVersionTextView = (TextView)findViewById(R.id.bleVersionLabel);

        mOTAProgressValueTextView = (TextView)findViewById(R.id.progressValue);

        mReButton = (Button)findViewById(R.id.reUpgradebutton);
        mReButton.setOnClickListener(this);

        mWarningButton = (ImageView)findViewById(R.id.warningButton);
        mWarningButton.setOnClickListener(this);

        mBackImage = (ImageView)findViewById(R.id.backImage);
        mBackImage.setOnClickListener(this);

        mTitleTextView = (TextView)findViewById(R.id.titleTextView);
        mTitleTextView.setOnClickListener(this);

        mNevoOtaController = OtaController.Singleton.getInstance(this);
        mNevoOtaController.setConnectControllerDelegate2Self();
        mNevoOtaController.setOnNevoOtaControllerListener(this);

        mOTAProgressValueTextView.setTextSize(30.0f);
        mMCUVersionTextView.setText(getString(R.string.mcu_version) + mNevoOtaController.getSoftwareVersion());
        mBleVersionTextView.setText(getString(R.string.ble_version) + mNevoOtaController.getFirmwareVersion());
        /*
        * Hide Status Bar
         */
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.reUpgradebutton:
                initListView(true,false);
                uploadPressed();
                break;
            case R.id.warningButton:
                Toast.makeText(mContext,R.string.otahelp,Toast.LENGTH_LONG).show();
                break;

            case R.id.backImage:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(SyncController.Singleton.getInstance(this)!=null && !SyncController.Singleton.getInstance(this).isConnected()){

        }
    }

    @Override
    public void packetReceived(NevoPacket packet)
    {
        if((byte) GetBatteryLevelNevoRequest.HEADER == packet.getHeader())
        {
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
            //((MainActivity) mContext).replaceFragment(isConnected ? OTAActivity.OTAPOSITION : ConnectAnimationFragment.CONNECTPOSITION, isConnected ? OTAActivity.OTAFRAGMENT : ConnectAnimationFragment.CONNECTFRAGMENT);
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
                mNevoOtaController.reset(false);
                initValue();
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
                    initValue();
                }
                else
                {
                    //check MCU OK,first reset and wait 5s do BLE OTA
                    mNevoOtaController.reset(false);
                    //set new state and hide rebutton
                    mNevoOtaController.setState(Constants.DFUControllerState.SEND_RECONNECT);
                    initValue();

                    mOTAProgressValueTextView.setText(mContext.getString(R.string.waiting));
                    refreshTimeCount(15);

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
                mOTAProgressValueTextView.setText(error);
                mNevoOtaController.reset(false);
                initValue();
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
                //when OTA finish done,reset the upload list again and show the lastest Version message
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
        if (mNevoOtaController.getState() == Constants.DFUControllerState.INIT) {
            mReButton.setVisibility(View.VISIBLE);
            mReButton.setEnabled(mNevoOtaController.isConnected());
        }
        else
        {
            mReButton.setVisibility(View.INVISIBLE);
        }
    }

    //upload button function
    private void uploadPressed()
    {
        String selectedFileURL;
        if (currentIndex >= firmwareURLs.size() || firmwareURLs.size() == 0 )
        {
            //check firmwareURLs is null, should hide the button
            Toast.makeText(mContext, "Reading firmware version,please wait...", Toast.LENGTH_LONG).show();
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
