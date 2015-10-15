package com.medcorp.nevo.activity.base;

import android.support.v7.app.ActionBarActivity;

import com.medcorp.nevo.application.ApplicationModel;

/**
 * Created by Karl on 10/15/15.
 */
public abstract class BaseActionBarActivity  extends ActionBarActivity {
    private ApplicationModel application;

    public ApplicationModel getModel() {

        if (application == null) {
            application = (ApplicationModel) getApplication();
        }
        return application;
    }
}
