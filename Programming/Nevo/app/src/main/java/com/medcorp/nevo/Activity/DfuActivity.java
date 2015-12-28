package com.medcorp.nevo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.ble.controller.OtaController;
import com.medcorp.nevo.ble.controller.OtaControllerImpl;
import com.medcorp.nevo.ble.listener.OnNevoOtaControllerListener;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.view.RoundProgressBar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/28.
 */
public class DfuActivity extends BaseActivity implements OnNevoOtaControllerListener, View.OnClickListener {

    @Bind(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;

    @Bind(R.id.activity_dfu_percent_textview)
    TextView percent;

    @Bind(R.id.activity_dfu_infomation_textview)
    TextView infomation;

    @Bind(R.id.activity_dfu_back2settings_textview)
    TextView back2settings;

    private OtaController mNevoOtaController ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfu);
        ButterKnife.bind(this);
        back2settings.setOnClickListener(this);

        mNevoOtaController = new OtaControllerImpl(this,false);
        mNevoOtaController.setConnectControllerDelegate2Self();
        mNevoOtaController.setOnNevoOtaControllerListener(this);
        showAlertDialog();
    }

    private void showAlertDialog()
    {
        new MaterialDialog.Builder(this)
                .title("Do not exit this screen")
                .content("Please follow the instructions and wait untill the update has been finished.")
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
                .positiveText("Update")
                .negativeText("Cancel")
                .show();

    }
    private void uploadPressed()
    {
        percent.setText("10%");
        infomation.setText("Updating BLE (1/2)");
        roundProgressBar.setProgress(10);
        //TODO implement build-in OTA
    }
    @Override
    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {

    }

    @Override
    public void onDFUStarted() {

    }

    @Override
    public void onDFUCancelled() {

    }

    @Override
    public void onTransferPercentage(int percent) {

    }

    @Override
    public void onSuccessfulFileTranferred() {

    }

    @Override
    public void onError(OtaController.ERRORCODE errorcode) {

    }

    @Override
    public void firmwareVersionReceived(Constants.DfuFirmwareTypes whichfirmware, String version) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.activity_dfu_back2settings_textview)
        {
            finish();
        }
    }
}
