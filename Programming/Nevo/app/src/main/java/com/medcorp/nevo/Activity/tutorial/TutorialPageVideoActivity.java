package com.medcorp.nevo.activity.tutorial;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.activity.base.BaseActivity;

import net.medcorp.library.ble.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 16/1/14.
 */
public class TutorialPageVideoActivity extends BaseActivity implements View.OnClickListener{

    @Bind(R.id.nextTextView)
    TextView nextTextView;

    @Bind(R.id.thourTextView)
    TextView thourTextView;

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
        setContentView(R.layout.activity_help_page_1);
        ButterKnife.bind(this);
        nextTextView.setOnClickListener(this);
        thourTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.nextTextView)
        {
            startActivity(TutorialPage1Activity.class);
            finish();
        }
        if(v.getId() == R.id.thourTextView)
        {
            Uri uri = Uri.parse("http://nevowatch.com/wp-content/uploads/2016/03/video.mp4");
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.setDataAndType(uri , "video/*");
            startActivity(intent);
        }
    }
}
