package com.medcorp.nevo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.EditAlarmActivity;
import com.medcorp.nevo.activity.GoalsActivity;
import com.medcorp.nevo.activity.MyNevoActivity;
import com.medcorp.nevo.activity.SettingAboutActivity;
import com.medcorp.nevo.activity.SettingNotificationActivity;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.util.Preferences;

/**
 * Created by karl-john on 18/12/15.
 */
public class SettingsImplFragment extends PreferenceFragmentCompat {

    private Preference findMyWatchPreference;
    private Preference goalsPreference;
    private Preference myNevoPreference;
    private Preference notificationPreference;
    private SwitchPreferenceCompat link_loss_notificationPreference;
    private Preference aboutPreference;

    private Preference.OnPreferenceClickListener goalsPreferenceListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            return startNextPreferencesActivity(GoalsActivity.class);

        }
    };
    private Preference.OnPreferenceClickListener myNevoPreferenceListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            return startNextPreferencesActivity(MyNevoActivity.class);

        }
    };

    private Preference.OnPreferenceClickListener notificationPreferenceListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            return startNextPreferencesActivity(SettingNotificationActivity.class);

        }
    };
    private Preference.OnPreferenceClickListener linklossPreferenceListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat)preference;
            Preferences.saveLinklossNotification(getActivity(), switchPreference.isChecked());
            return true;
        }
    };

    private Preference.OnPreferenceClickListener aboutPreferenceListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
        return startNextPreferencesActivity(SettingAboutActivity.class);
        }
    };

    private Preference.OnPreferenceClickListener findMyWatchClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            ((ApplicationModel)getActivity().getApplication()).blinkWatch();
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
        myNevoPreference = findPreference("my_nevo_key");
        myNevoPreference.setOnPreferenceClickListener(myNevoPreferenceListener);
        notificationPreference = findPreference("notification_key");
        notificationPreference.setOnPreferenceClickListener(notificationPreferenceListener);
        link_loss_notificationPreference = (SwitchPreferenceCompat)findPreference("link_loss_key");
        link_loss_notificationPreference.setChecked(Preferences.getLinklossNotification(getActivity()));
        link_loss_notificationPreference.setOnPreferenceClickListener(linklossPreferenceListener);
        aboutPreference = findPreference("about_key");
        aboutPreference.setOnPreferenceClickListener(aboutPreferenceListener);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private boolean startNextPreferencesActivity(Class<?> cls){
        Intent intent = new Intent(getActivity(),cls);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        setEnterTransition(new Fade().setDuration(300));
        return true;
    }

}
