package com.medcorp.nevo.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.util.Constants;

/**
 * Turorial One
 */
public class TutorialOneActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!getSharedPreferences(Constants.PREF_NAME, 0).getBoolean(Constants.FIRST_FLAG,true))
        {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_1);

        findViewById(R.id.activateButton).setOnClickListener(this);

        findViewById(R.id.uriButton).setOnClickListener(this);

        findViewById(R.id.imagewatch).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(TutorialOneActivity.this, OTAActivity.class);
                intent.putExtra("from","tutorial");
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activateButton:
                startActivity(new Intent(this, TutorialTwoActivity.class));
                overridePendingTransition(R.anim.anim_enter, R.anim.anim_exit);
                finish();
                break;
            case R.id.uriButton:
                Uri uri = Uri.parse(getResources().getString(R.string.nevoURL));
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
                finish();
                break;
            default:
                break;
        }
    }
}
