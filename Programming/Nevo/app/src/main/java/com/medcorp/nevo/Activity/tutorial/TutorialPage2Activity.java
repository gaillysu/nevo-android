package com.medcorp.nevo.activity.tutorial;

import android.app.Activity;
import android.app.AlertDialog;
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
        new MaterialDialog.Builder(this)
                .content(R.string.tutorial_2_dialog_positive)
                .positiveText(R.string.ok_button)
                .negativeText(R.string.tutorial_2_dialog_negative)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.nextTextView)
        {
            startActivity(new Intent(this, TutorialPage3Activity.class));
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
            finish();
        }
    }
}
