package com.medcorp.nevo.activity.base;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

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
}
