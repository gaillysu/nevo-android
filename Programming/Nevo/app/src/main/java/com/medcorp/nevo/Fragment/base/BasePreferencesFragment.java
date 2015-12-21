package com.medcorp.nevo.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;

import com.medcorp.nevo.application.ApplicationModel;

/**
 * Created by Karl on 10/16/15.
 */
public abstract class BasePreferencesFragment extends PreferenceFragmentCompat {

    private ApplicationModel applicationModel;
    public ApplicationModel getModel() {
        if (applicationModel == null) {
            applicationModel = (ApplicationModel) getActivity().getApplication();
        }
        return applicationModel;
    }

    public AppCompatActivity getAppCompatActivity(){
        return (AppCompatActivity) getActivity();
    }
}