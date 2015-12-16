package com.medcorp.nevo.activity.base;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

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
}
