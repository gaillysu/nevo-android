package com.nevowatch.nevo.Tutorial;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.nevowatch.nevo.R;

/**
 * Turorial One
 */
public class TutorialOne extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_1);

        findViewById(R.id.activateButton).setOnClickListener(this);
        findViewById(R.id.uriButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activateButton:
                startActivity(new Intent(this, TutorialTwo.class));
                break;
            case R.id.uriButton:
                Uri uri = Uri.parse("http://baidu.com");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
                break;
            default:
                break;
        }
    }
}
