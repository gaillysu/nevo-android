package com.medcorp.nevo.activity.old;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.ble.controller.OtaController;
import com.medcorp.nevo.ble.controller.OtaControllerImpl;
import com.medcorp.nevo.ble.listener.OnNevoOtaControllerListener;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.ble.util.Constants.DfuFirmwareTypes;
import com.medcorp.nevo.view.RoundProgressBar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * NotificationFragment
 */
public class OTAActivity extends BaseActivity
        implements View.OnClickListener,OnNevoOtaControllerListener {
    private static final String TAG="OTAActivity";
    public static final int OTAPOSITION = 4;
    private RoundProgressBar mOTAProgressBar;
    private Button mReButton;
    private ImageView mBackImage;
    private TextView mFirmwareTotal;

    //save the build-in firmware version, it should be the latest FW version
    int buildinSoftwareVersion = 0;
    int buildinFirmwareVersion= 0;
    //TODO these static variable should be moved to mNevoOtaController due to mNevoOtaController is a singleton class
    private static DfuFirmwareTypes enumFirmwareType = DfuFirmwareTypes.APPLICATION;
    private static List<String> firmwareURLs = new ArrayList<String>();
    private static int currentIndex = 0;

    OtaController mNevoOtaController ;
    private boolean mUpdateSuccess = false;
    private String  errorMsg="";
    //save the attached Activity, should be MainActivity, when doing OTA, user perhaps switch other fragment
    //but the OTA should be continue on background. when user come back, the progress should be showing
    Context mContext;
    private static AlertDialog mAlertDialog = null;
    //press A/B and install battery mode.
    private boolean bHelpMode = false;

    //for good user experience, add Animation when waiting...
    private Animation mAnimation = null;

    private void initListView(boolean forceUpdate,boolean popupMessage){

        if(mNevoOtaController.getSoftwareVersion() == null
                || mNevoOtaController.getFirmwareVersion() == null
                || mNevoOtaController.getState() != Constants.DFUControllerState.INIT)
        {
            if(!bHelpMode) {
                return;
            }
        }

        firmwareURLs.clear();
        currentIndex = 0;

        String[]files;
        int  currentSoftwareVersion = 0;
        int  currentFirmwareVersion = 0;

        if(mNevoOtaController.getSoftwareVersion() != null) {
            currentSoftwareVersion = Integer.parseInt(mNevoOtaController.getSoftwareVersion());
        }
        if(mNevoOtaController.getFirmwareVersion() != null) {
            currentFirmwareVersion = Integer.parseInt(mNevoOtaController.getFirmwareVersion());
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
                        if(currentSoftwareVersion == 0 && !bHelpMode)
                            firmwareURLs.add(0,"firmware/" + file);
                        else
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
            findViewById(R.id.textView5).setVisibility(View.INVISIBLE);
            findViewById(R.id.textInfomation).setVisibility(View.INVISIBLE);

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ota_activity);

        mContext = this;
        bHelpMode = getIntent().getStringExtra("from") == null ? false: getIntent().getStringExtra("from").equals("tutorial");
        mNevoOtaController = new OtaControllerImpl(this,bHelpMode);
        mNevoOtaController.setConnectControllerDelegate2Self();
        mNevoOtaController.setOnNevoOtaControllerListener(this);

        initView();

        if(bHelpMode)
        {
            initListView(true, false);
            mReButton.setVisibility(View.INVISIBLE);
        }
        else {
            initListView(false, true);
            initValue();
        }
    }

    private void initView(){

        mOTAProgressBar = (RoundProgressBar)findViewById(R.id.otaProgressBar);

        mReButton = (Button)findViewById(R.id.reUpgradebutton);
        mReButton.setOnClickListener(this);

        mBackImage = (ImageView)findViewById(R.id.backImage);
        mBackImage.setOnClickListener(this);

        findViewById(R.id.titleTextView).setOnClickListener(this);


        mFirmwareTotal = (TextView)findViewById(R.id.textFirmwareTotal);
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
        if(bHelpMode)
        {
            return;
        }
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
    }

    @Override
    public void packetReceived(NevoPacket packet)
    {
        //DO NOTHING
    }
    @Override
    public void connectionStateChanged(boolean isConnected) {
        if(mNevoOtaController.getState() == Constants.DFUControllerState.INIT ) {
            if(errorMsg !="" && isConnected && !bHelpMode)
            {
                mReButton.setText(R.string.re_upgrade);
                mReButton.setVisibility(View.VISIBLE);
            }
        }
        if((mNevoOtaController.getState() == Constants.DFUControllerState.SEND_RESET
                || (mNevoOtaController.getState() == Constants.DFUControllerState.INIT && bHelpMode))
                && isConnected)
        {
            //bLinkWaitingMessage(0);
            //uploadPressed();
            mReButton.setVisibility(View.VISIBLE);
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
                mFirmwareTotal.setText((currentIndex + 1) + "/" + firmwareURLs.size() + " ," + percent + "%");
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
                    //BLE OTA done, unPair nevo, due to the pair infomation has got destory in the smartphone side.
                    if (enumFirmwareType == DfuFirmwareTypes.APPLICATION) mNevoOtaController.forGetDevice();
                }
                else
                {
                    //removed for first do MCU ota,then do BLE ota
                    /**
                    //check MCU OK,first reset and wait 10s do BLE OTA
                    mNevoOtaController.reset(false);
                    //set new state and hide rebutton
                    mNevoOtaController.setState(Constants.DFUControllerState.SEND_RECONNECT);
                    initValue();

                    bLinkWaitingMessage(10);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!mNevoOtaController.isConnected() && mNevoOtaController.getState() == Constants.DFUControllerState.SEND_RECONNECT)
                            {
                                bLinkWaitingMessage(0);
                                onError(OtaController.ERRORCODE.TIMEOUT);
                            }
                        }
                    },10000);
                    */
                        //unpair this watch, when reconnect it, repair it again, otherwiase, it will lead the cmd can't get response.
                        if (enumFirmwareType == DfuFirmwareTypes.APPLICATION){
                            mNevoOtaController.forGetDevice();
                        }
                        mNevoOtaController.reset(false);
                        mNevoOtaController.setState(Constants.DFUControllerState.SEND_RESET);
                        initValue();

                        mReButton.setText(getString(R.string.continue_button));

                        String msgInfo = getString(R.string.update_ble_success_message);

                        if (enumFirmwareType == DfuFirmwareTypes.SOFTDEVICE) {
                            msgInfo = getString(R.string.update_mcu_success_message);
                            // Does this still occur?
                        }
                        new AlertDialog.Builder(((Activity) mContext), AlertDialog.THEME_HOLO_LIGHT)
                                .setTitle(R.string.FirmwareUpgrade)
                                .setMessage(msgInfo)
                                .setPositiveButton(android.R.string.ok, null)
                                .setCancelable(false).show();



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
            mReButton.setVisibility(View.INVISIBLE);
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
        refreshTimeCount(30,true);
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
                    //for MCU OTA,no delay time, right now return.
                    if(enumFirmwareType == DfuFirmwareTypes.SOFTDEVICE) {
                        mFirmwareTotal.setText((currentIndex+1)+"/"+firmwareURLs.size()+" ," + 0 + "%");
                        return;
                    }
                    //for BLE OTA, need wait more about 3s, I blink the message to user for good experience
                    bLinkWaitingMessage(3);
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
    private void bLinkWaitingMessage(int count)
    {
        if(count==0 && mAnimation!=null ){
            mAnimation.cancel();
            mAnimation=null;
            return;
        }
        mFirmwareTotal.setText(mContext.getString(R.string.waiting));
        mAnimation = new AlphaAnimation(0.0f,1.0f);
        mAnimation.setDuration(1000);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(count);
        mFirmwareTotal.startAnimation(mAnimation);
    }

}
