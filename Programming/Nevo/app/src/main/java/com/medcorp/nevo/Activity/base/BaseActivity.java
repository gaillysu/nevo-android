package com.medcorp.nevo.activity.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.application.ApplicationModel;

/**
 * Created by Karl on 10/15/15.
 */
public abstract class BaseActivity extends AppCompatActivity{

    private ApplicationModel application;
    public ApplicationModel getModel() {

        if (application == null) {
            application = (ApplicationModel) getApplication();
        }
        return application;
    }


    public void startActivity(Class <?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void askForPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                new MaterialDialog.Builder(this)
                        .title(R.string.location_access_title)
                        .content(R.string.location_access_content)
                        .positiveText(getString(android.R.string.ok))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        }
    }
}
