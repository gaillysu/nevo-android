package com.medcorp.nevo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.EditAlarmActivity;
import com.medcorp.nevo.activity.GoalsActivity;
import com.medcorp.nevo.application.ApplicationModel;

/**
 * Created by karl-john on 18/12/15.
 */
public class SettingsImplFragment extends PreferenceFragmentCompat {

    private Preference findMyWatchPreference;
    private Preference goalsPreference;
    private Preference.OnPreferenceClickListener goalsPreferenceListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            //TODO show dialog to list all preset goals, with add/edit feature
            Intent intent = new Intent(getActivity(), GoalsActivity.class);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
            setEnterTransition(new Fade().setDuration(300));
            return true;
        }
    };

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
        goalsPreference = findPreference("goals_key");
        goalsPreference.setOnPreferenceClickListener(goalsPreferenceListener);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private Preference.OnPreferenceClickListener findMyWatchClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            ((ApplicationModel)getActivity().getApplication()).blinkWatch();
            return true;

        }
    };

}
