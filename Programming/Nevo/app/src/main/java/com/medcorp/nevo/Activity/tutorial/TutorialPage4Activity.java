package com.medcorp.nevo.activity.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.DfuActivity;
import com.medcorp.nevo.activity.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage4Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_4);
        ButterKnife.bind(this);
        //if BLE or MCU got broken in OTA progress, press the third key will not open BT
        //so we should give user a solution to update the nevo firmwares
        //in this page, user long press the image, will enable user to continue do OTA
        askForPermission();
    }

    @OnLongClick(R.id.activity_tutorial_page4_open_bt_image)
    public boolean btImageClicked(){
        Intent intent = new Intent(TutorialPage4Activity.this, DfuActivity.class);
        intent.putExtra(getString(R.string.key_manual_mode), true);
        intent.putExtra(getString(R.string.key_back_to_settings), false);
        startActivity(intent);
        return true;
    }

    @OnClick(R.id.activity_tutorial_4_next_button)
    public void nextButtonClicked(){
        startActivity(TutorialPage5Activity.class);
        overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
        finish();
    }
}
