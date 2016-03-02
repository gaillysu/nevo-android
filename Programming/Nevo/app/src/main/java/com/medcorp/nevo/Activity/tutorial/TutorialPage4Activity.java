package com.medcorp.nevo.activity.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.DfuActivity;
import com.medcorp.nevo.activity.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPage4Activity extends BaseActivity implements View.OnClickListener{

    @Bind(R.id.nextTextView)
    TextView nextTextView;
    @Bind(R.id.activity_tutorial_page4_open_bt_image)
    ImageView openBtImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tutorial_page_4);
        ButterKnife.bind(this);
        nextTextView.setOnClickListener(this);
        //if BLE or MCU got broken in OTA progress, press the third key will not open BT
        //so we should give user a solution to update the nevo firmwares
        //in this page, user long press the image, will enable user to continue do OTA
        openBtImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(TutorialPage4Activity.this, DfuActivity.class);
                intent.putExtra("manualMode",true);
                intent.putExtra("backtosetting", false);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.nextTextView)
        {
            startActivity(TutorialPage5Activity.class);
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
            finish();
        }
    }
}
