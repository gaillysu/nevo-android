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
import com.nevowatch.nevo.ble.controller.OtaController;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.BatteryLevelNevoPacket;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.request.GetBatteryLevelNevoRequest;
import com.nevowatch.nevo.ble.model.request.GetStepsGoalNevoRequest;
import com.nevowatch.nevo.ble.model.request.NevoOTAStartRequest;
import com.nevowatch.nevo.ble.model.request.NumberOfStepsGoal;
import com.nevowatch.nevo.ble.notification.NevoNotificationListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Button mReButton;
    private ImageView mBackImage;
    private TextView mTitleTextView;
    private TextView mFirmwareTotal;
    private TextView mOtaInfomation;

    //save the build-in firmware version, it should be the latest FW version
    int buildinSoftwareVersion = 0;
    int buildinFirmwareVersion= 0;
    //TODO these static variable should be moved to mNevoOtaController due to mNevoOtaController is a singleton class
    private static DfuFirmwareTypes enumFirmwareType = DfuFirmwareTypes.APPLICATION;
    private static ArrayList<String> firmwareURLs = new ArrayList<String>();
    private static int currentIndex = 0;

    OtaController mNevoOtaController ;
    private boolean mUpdateSuccess = false;
    private String  errorMsg="";
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
            mFirmwareTotal.setText(mContext.getString(R.string.latestversion));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ota_activity);

        mContext = this;

        mNevoOtaController = OtaController.Singleton.getInstance(this);
        mNevoOtaController.setConnectControllerDelegate2Self();
        mNevoOtaController.setOnNevoOtaControllerListener(this);

        initView();

        View [] viewArray = new View []{
                findViewById(R.id.mcuVersionLabel),
                findViewById(R.id.bleVersionLabel),
                findViewById(R.id.reUpgradebutton)
        };
        FontManager.changeFonts(viewArray, this);

        initListView(false,true);
        initValue();
    }

    private void initView(){

        mOTAProgressBar = (RoundProgressBar)findViewById(R.id.otaProgressBar);

        mReButton = (Button)findViewById(R.id.reUpgradebutton);
        mReButton.setOnClickListener(this);

        mBackImage = (ImageView)findViewById(R.id.backImage);
        mBackImage.setOnClickListener(this);

        mTitleTextView = (TextView)findViewById(R.id.titleTextView);
        mTitleTextView.setOnClickListener(this);

        mFirmwareTotal = (TextView)findViewById(R.id.textFirmwareTotal);
        mOtaInfomation = (TextView)findViewById(R.id.textInfomation);
        mFirmwareTotal.setText("");
        /*
        * Hide Status Bar
         */
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*always light on screen */
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mNevoOtaController.getState() == Constants.DFUControllerState.INIT)
        {
            if(mUpdateSuccess)
               mNevoOtaController.reset(true);
            else
               mNevoOtaController.switch2SyncController();
        }
    }

    @Override
    public void onBackPressed() {
        if(mNevoOtaController.getState() != Constants.DFUControllerState.INIT) return;
        super.onBackPressed();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.reUpgradebutton:
                initListView(true,false);
                uploadPressed();
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
            //DO NOTHING
        }
    }

    @Override
    public void packetReceived(NevoPacket packet)
    {
        //DO NOTHING
    }
    @Override
    public void connectionStateChanged(boolean isConnected) {
        if(mNevoOtaController.getState() == Constants.DFUControllerState.INIT ) {
           // ((MainActivity) mContext).replaceFragment(isConnected ? OTAActivity.OTAPOSITION : ConnectAnimationFragment.CONNECTPOSITION, isConnected ? OTAActivity.OTAFRAGMENT : ConnectAnimationFragment.CONNECTFRAGMENT);
        }
        if(mNevoOtaController.getState() == Constants.DFUControllerState.SEND_RECONNECT && isConnected)
        {
            uploadPressed();
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
                mFirmwareTotal.setText((currentIndex+1)+"/"+firmwareURLs.size()+" ," + percent + "%");
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
                    mUpdateSuccess = true;
                    String message = ((Activity)mContext).getString(R.string.UpdateSuccess1);
                    if (enumFirmwareType == DfuFirmwareTypes.APPLICATION)
                    {
                        message = ((Activity)mContext).getString(R.string.UpdateSuccess2);
                    }
                    new AlertDialog.Builder(((Activity)mContext),AlertDialog.THEME_HOLO_LIGHT)
                            .setTitle(R.string.FirmwareUpgrade)
                            .setMessage(message)
                            .setNegativeButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    OTAActivity.this.finish();
                                }
                            }).setCancelable(false).show();
                    //show success text or image
                    mNevoOtaController.reset(false);
                    initValue();
                    //save date
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate = format.format(Calendar.getInstance().getTimeInMillis());
                    getSharedPreferences(OtaController.PREF_NAME, Context.MODE_PRIVATE).edit().putString(OtaController.SYNCDATE, strDate).commit();

                }
                else
                {
                    //check MCU OK,first reset and wait 10s do BLE OTA
                    mNevoOtaController.reset(false);
                    //set new state and hide rebutton
                    mNevoOtaController.setState(Constants.DFUControllerState.SEND_RECONNECT);
                    initValue();

                    mFirmwareTotal.setText(mContext.getString(R.string.waiting));

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!mNevoOtaController.isConnected() && mNevoOtaController.getState() == Constants.DFUControllerState.SEND_RECONNECT)
                            {
                                onError(OtaController.ERRORCODE.TIMEOUT);
                            }
                        }
                    },10000);
                }
            }
        });
    }

    @Override
    public void onError(final OtaController.ERRORCODE errorcode) {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNevoOtaController.reset(false);
                initValue();
                if(mUpdateSuccess) return; //fix a bug when BLE OTA done,connect it before BT off, it can't find any characteristics and throw exception

                if(errorcode == OtaController.ERRORCODE.TIMEOUT)
                    errorMsg = mContext.getString(R.string.update_error_timeout);
                else if(errorcode == OtaController.ERRORCODE.NOCONNECTION)
                    errorMsg = mContext.getString(R.string.update_error_noconnect);
                else if(errorcode == OtaController.ERRORCODE.CHECKSUMERROR)
                    errorMsg = mContext.getString(R.string.update_error_checksum);
                else if(errorcode == OtaController.ERRORCODE.OPENFILEERROR)
                    errorMsg = mContext.getString(R.string.update_error_openfile);
                else if (errorcode == OtaController.ERRORCODE.NODFUSERVICE)
                    errorMsg = mContext.getString(R.string.update_error_nofounDFUservice);
                else if (errorcode == OtaController.ERRORCODE.NOFINISHREADVERSION)
                    errorMsg = mContext.getString(R.string.checking_firmware);
                else
                    errorMsg = mContext.getString(R.string.update_error_other);

                Log.e(TAG,errorMsg);
                mFirmwareTotal.setText(errorMsg);
                Toast.makeText(mContext,errorMsg,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void firmwareVersionReceived(DfuFirmwareTypes whichfirmware, String version) {

        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mMCUVersionTextView.setText(mContext.getString(R.string.mcu_version) + mNevoOtaController.getSoftwareVersion());
                //mBleVersionTextView.setText(mContext.getString(R.string.ble_version) + mNevoOtaController.getFirmwareVersion());
                //when OTA finish done,reset the upload list again and show the lastest Version message
                //initListView(false,false);
            }
        });
    }

    /**
    *Set the OTA upgrade progress
    */
    public void setOTAProgressBar(final int progress){
        mOTAProgressBar.setProgress(progress);
    }

    //init data function
    private void initValue()
    {
       // nevoOtaView.backButton.enabled = true
        if (mNevoOtaController.getState() == Constants.DFUControllerState.INIT) {
            mReButton.setVisibility(View.VISIBLE);
            mBackImage.setVisibility(View.VISIBLE);
        }
        else
        {
            mReButton.setVisibility(View.INVISIBLE);
            mBackImage.setVisibility(View.INVISIBLE);
        }
    }

    //upload button function
    private void uploadPressed()
    {
        //reset false here
        mUpdateSuccess = false;
        errorMsg="";
        if(!mNevoOtaController.isConnected())
        {
             Log.e(TAG,mContext.getString(R.string.connect_error_no_nevo_do_ota));
             onError(OtaController.ERRORCODE.NOCONNECTION);
             return;
        }
        if (currentIndex >= firmwareURLs.size() || firmwareURLs.size() == 0 )
        {
            //check firmwareURLs is null, should hide the button
            Log.e(TAG,mContext.getString(R.string.checking_firmware));
            onError(OtaController.ERRORCODE.NOFINISHREADVERSION);
            return;
        }

        String selectedFileURL = firmwareURLs.get(currentIndex);
        refreshTimeCount(20,true);
        if (selectedFileURL.contains(".bin"))
        {
            enumFirmwareType = DfuFirmwareTypes.SOFTDEVICE;
        }
        if (selectedFileURL.contains(".hex"))
        {
            enumFirmwareType = DfuFirmwareTypes.APPLICATION;
        }
        mOTAProgressBar.setProgress(0);
        //when doing OTA, disable Cancel/Back button, enable them by callback function invoke initValue()/checkConnection()
        //nevoOtaView.backButton.enabled = false
        mReButton.setVisibility(View.INVISIBLE); //The process of OTA hide this control
        mBackImage.setVisibility(View.INVISIBLE);
        mNevoOtaController.performDFUOnFile(selectedFileURL, enumFirmwareType);

    }

    private void refreshTimeCount(final int count,final boolean checkStatus)
    {
        if(count == 0 )
        {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //timeout or get exception, show it on screen
                    mFirmwareTotal.setText(errorMsg);
                }
            });
            return;
        }

        if(checkStatus && mNevoOtaController.getState() != Constants.DFUControllerState.INIT
                && mNevoOtaController.getState() != Constants.DFUControllerState.SEND_START_COMMAND
                && mNevoOtaController.getState() != Constants.DFUControllerState.SEND_RECONNECT)
        {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //status got changed,return
                    mFirmwareTotal.setText("");
                }
            });
            return;
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFirmwareTotal.setText(mContext.getString(R.string.waiting)+ count);
                    }
                });
                refreshTimeCount(count-1,checkStatus);
            }
        },1000);
    }

}
