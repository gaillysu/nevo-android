package com.medcorp.nevo.activity.tutorial;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage2Activity extends BaseActivity implements View.OnClickListener{

    @Bind(R.id.nextTextView)
    TextView nextTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_2);
        ButterKnife.bind(this);
        nextTextView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.nextTextView)
        {
            if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                startActivity(TutorialPage3Activity.class);
                finish();
            }else{
                new MaterialDialog.Builder(this)
                        .content(R.string.tutorial_2_dialog_positive)
                        .positiveText(android.R.string.ok)
                        .negativeText(R.string.tutorial_2_dialog_negative)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                            }
                        }).show();
            }
        }
    }
}
