package com.nevowatch.nevo.TutorialActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.FontManager;
import com.nevowatch.nevo.ble.util.Constants;

import java.util.ArrayList;
import java.util.List;

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

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity_1);

        findViewById(R.id.activateButton).setOnClickListener(this);
        findViewById(R.id.uriButton).setOnClickListener(this);

        View [] viewArray = new View []{findViewById(R.id.activateButton),findViewById(R.id.uriButton)};
        FontManager.changeFonts(viewArray,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activateButton:
                startActivity(new Intent(this, TutorialTwoActivity.class));
                break;
            case R.id.uriButton:
                Uri uri = Uri.parse(getResources().getString(R.string.nevoURL));
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
                break;
            default:
                break;
        }
    }
}
