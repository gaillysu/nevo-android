package com.medcorp.nevo.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.ble.controller.OtaController;
import com.medcorp.nevo.ble.listener.OnNevoOtaControllerListener;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.view.RoundProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/28.
 */
public class DfuActivity extends BaseActivity implements OnNevoOtaControllerListener, View.OnClickListener {

    private static final String TAG="DfuActivity";

    @Bind(R.id.clock_imageView)
    ImageView clockImage;

    @Bind(R.id.HomeClockHour)
    ImageView hourImage;

    @Bind(R.id.HomeClockMinute)
    ImageView minImage;

    @Bind(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;

    @Bind(R.id.activity_dfu_percent_textview)
    TextView percentTextView;

    @Bind(R.id.activity_dfu_infomation_textview)
    TextView infomationTextView;

    @Bind(R.id.activity_dfu_back2settings_textview)
    TextView back2settings;

    private OtaController mNevoOtaController ;
    private Constants.DfuFirmwareTypes enumFirmwareType = Constants.DfuFirmwareTypes.APPLICATION;
    private List<String> firmwareURLs;
    private int currentIndex;
    private String  errorMsg="";
    private Context mContext;
    private boolean mUpdateSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfu);
        ButterKnife.bind(this);
        mContext = this;
        back2settings.setOnClickListener(this);
        back2settings.setVisibility(View.INVISIBLE);
        back2settings.setText(R.string.dfu_re_upgrade);
        back2settings.setTag(new ButtonTag(getString(R.string.dfu_retry)));

        mNevoOtaController = getModel().getOtaController();
        mNevoOtaController.switch2OtaController();
        mNevoOtaController.setOnNevoOtaControllerListener(this);

        /*always light on screen */
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initFirmwareList();
        initNevoLogo();
        showAlertDialog();
    }

    private void showAlertDialog()
    {
        new MaterialDialog.Builder(this)
                .title(R.string.dfu_update_title)
                .content(R.string.dfu_update_content)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        uploadPressed();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        finish();
                    }
                })
                .positiveText(R.string.dfu_update_positive)
                .negativeText(R.string.dfu_update_negative)
                .cancelable(false)
                .show();

    }
    //nevo fixed time : 11:05
    private void initNevoLogo()
    {
        hourImage.setVisibility(View.VISIBLE);
        minImage.setVisibility(View.VISIBLE);
        clockImage.setVisibility(View.VISIBLE);
        clockImage.setImageResource(R.drawable.clockview600);

        int hour = 11;
        int minute = 5;
        final float degreeHour = (float) ((hour + minute / 60.0) * 30);
        final float degreeMin  = minute * 6;
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hourImage.setRotation(degreeHour);
                minImage.setRotation(degreeMin);
            }
        });
    }
    private void initFirmwareList()
    {
        firmwareURLs = new ArrayList<String>();
        Bundle bundle = getIntent().getExtras();
        firmwareURLs = bundle.getStringArrayList("firmwares");
        currentIndex = 0;
    }

    private void uploadPressed()
    {
        mUpdateSuccess = false;
        errorMsg="";
        if(!mNevoOtaController.isConnected())
        {
            Log.e(TAG,mContext.getString(R.string.dfu_connect_error_no_nevo_do_ota));
            onError(OtaController.ERRORCODE.NOCONNECTION);
            return;
        }
        if (currentIndex >= firmwareURLs.size() || firmwareURLs.size() == 0 )
        {
            //check firmwareURLs is null, should hide the button
            Log.e(TAG,mContext.getString(R.string.dfu_checking_firmware));
            onError(OtaController.ERRORCODE.NOFINISHREADVERSION);
            return;
        }

        String selectedFileURL = firmwareURLs.get(currentIndex);
        if (selectedFileURL.contains(".bin"))
        {
            enumFirmwareType = Constants.DfuFirmwareTypes.SOFTDEVICE;
        }
        if (selectedFileURL.contains(".hex"))
        {
            enumFirmwareType = Constants.DfuFirmwareTypes.APPLICATION;
        }
        initNevoLogo();
        roundProgressBar.setProgress(0);
        roundProgressBar.setVisibility(View.VISIBLE);
        percentTextView.setText("");
        infomationTextView.setText("");
        back2settings.setVisibility(View.INVISIBLE); //The process of OTA hide this control
        mNevoOtaController.performDFUOnFile(selectedFileURL, enumFirmwareType);
    }

    @Override
    public void onPrepareOTA(Constants.DfuFirmwareTypes which) {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infomationTextView.setText(R.string.dfu_update_prepare);
            }
        });
    }

    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {

        if(mNevoOtaController.getState() == Constants.DFUControllerState.INIT ) {
            if(errorMsg != "" && isConnected )
            {
                back2settings.setText(R.string.dfu_re_upgrade);
                back2settings.setTag(new ButtonTag(getString(R.string.dfu_retry)));
                back2settings.setVisibility(View.VISIBLE);
            }
        }
        if((mNevoOtaController.getState() == Constants.DFUControllerState.SEND_RESET)
                && isConnected)
        {
            back2settings.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onDFUStarted() {
        Log.i(TAG, "onDFUStarted");
    }

    @Override
    public void onDFUCancelled() {
        Log.i(TAG,"onDFUCancelled");
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNevoOtaController.reset(false);
            }
        });
    }

    @Override
    public void onTransferPercentage(final int percent) {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roundProgressBar.setProgress(percent);
                percentTextView.setText(percent+"%");
                infomationTextView.setText(getString(R.string.dfu_update_message)  + ((enumFirmwareType == Constants.DfuFirmwareTypes.APPLICATION)? "BLE":"MCU") +  " ("+(currentIndex + 1) + "/" + firmwareURLs.size() + ")");
            }
        });
    }

    @Override
    public void onSuccessfulFileTranferred() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                currentIndex = currentIndex + 1;
                if (currentIndex == firmwareURLs.size()) {
                    mUpdateSuccess = true;

                    roundProgressBar.setVisibility(View.INVISIBLE);
                    clockImage.setVisibility(View.INVISIBLE);
                    percentTextView.setText("");
                    infomationTextView.setText(R.string.dfu_firmware_updated);
                    back2settings.setText(R.string.dfu_back_to_settings);
                    back2settings.setTag(new ButtonTag(getString(R.string.dfu_back)));
                    back2settings.setVisibility(View.VISIBLE);
                    //show success text or image
                    mNevoOtaController.reset(false);

                    //save date
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate = format.format(Calendar.getInstance().getTimeInMillis());
                    getSharedPreferences(OtaController.PREF_NAME, Context.MODE_PRIVATE).edit().putString(OtaController.SYNCDATE, strDate).commit();
                    //BLE OTA done, unPair nevo, due to the pair infomation has got destory in the smartphone side.
                    if (enumFirmwareType == Constants.DfuFirmwareTypes.APPLICATION)
                        mNevoOtaController.forGetDevice();
                } else {
                    //unpair this watch, when reconnect it, repair it again, otherwiase, it will lead the cmd can't get response.
                    if (enumFirmwareType == Constants.DfuFirmwareTypes.APPLICATION) {
                        mNevoOtaController.forGetDevice();
                    }
                    mNevoOtaController.reset(false);
                    mNevoOtaController.setState(Constants.DFUControllerState.SEND_RESET);

                    back2settings.setText(getString(R.string.dfu_continue_button));
                    back2settings.setTag(new ButtonTag(getString(R.string.dfu_continue_button)));
                    back2settings.setVisibility(View.INVISIBLE);

                    roundProgressBar.setVisibility(View.INVISIBLE);
                    hourImage.setVisibility(View.INVISIBLE);
                    minImage.setVisibility(View.INVISIBLE);
                    clockImage.setImageDrawable(getDrawable(R.drawable.firmware_clock_ble_button));
                    percentTextView.setText(R.string.dfu_press_third_button);
                    infomationTextView.setText(R.string.dfu_press_third_button_description);
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

                if(mUpdateSuccess) return; //fix a bug when BLE OTA done,connect it before BT off, it can't find any characteristics and throw exception

                if(errorcode == OtaController.ERRORCODE.TIMEOUT)
                    //errorMsg = mContext.getString(R.string.update_error_timeout);
                    errorMsg = getString(R.string.dfu_failed_preparing);
                else if(errorcode == OtaController.ERRORCODE.NOCONNECTION)
                    errorMsg = mContext.getString(R.string.dfu_error_noconnect);
                else if(errorcode == OtaController.ERRORCODE.CHECKSUMERROR)
                    errorMsg = mContext.getString(R.string.dfu_error_checksum);
                else if(errorcode == OtaController.ERRORCODE.OPENFILEERROR)
                    errorMsg = mContext.getString(R.string.dfu_error_openfile);
                else if (errorcode == OtaController.ERRORCODE.NODFUSERVICE)
                    errorMsg = mContext.getString(R.string.dfu_error_nofounDFUservice);
                else if (errorcode == OtaController.ERRORCODE.NOFINISHREADVERSION)
                    errorMsg = mContext.getString(R.string.dfu_checking_firmware);
                else
                    errorMsg = mContext.getString(R.string.dfu_error_other);

                Log.e(TAG,errorMsg);
                infomationTextView.setText(errorMsg);
                Toast.makeText(mContext, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

    }

    @Override
    public void onBackPressed() {
        if(mNevoOtaController.getState() != Constants.DFUControllerState.INIT) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mNevoOtaController.getState() == Constants.DFUControllerState.INIT)
        {
            mNevoOtaController.reset(true);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.activity_dfu_back2settings_textview)
        {
            //TODO OMG this is so bad. Come on. Change getString to some keys but not this. also dont work with tags.
            if (v.getTag().toString().equals(getString(R.string.dfu_back)))
            {
                finish();
            }
            else if (v.getTag().toString().equals(getString(R.string.dfu_retry)) || v.getTag().toString().equals(getString(R.string.dfu_continue)))
            {
                uploadPressed();
            }
        }
    }

    class ButtonTag {
        private String name;
        public ButtonTag(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
