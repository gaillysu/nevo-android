package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.nevo.R;

/**
 * Created by karl-john on 18/12/15.
 */
public class SettingsImplFragment extends PreferenceFragmentCompat {

    private Preference findMyWatchPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        findMyWatchPreference = findPreference("find_watch_key");
        findMyWatchPreference.setOnPreferenceClickListener(findMyWatchClickListener);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private Preference.OnPreferenceClickListener findMyWatchClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {

            //TODO let the watch vibrate.
            return true;

        }
    };

}
