package com.medcorp.nevo.Activity.BaseActivities;

import android.app.Activity;

import com.medcorp.nevo.application.ApplicationModel;

/**
 * Created by Karl on 10/15/15.
 */
public abstract class BaseFragmentActivity extends Activity {

    private ApplicationModel application;

    public ApplicationModel getModel() {

        if (application == null) {
            application = (ApplicationModel) getApplication();
        }
        return application;
    }
}
