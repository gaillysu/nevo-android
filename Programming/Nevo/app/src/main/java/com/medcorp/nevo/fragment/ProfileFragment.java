package com.medcorp.nevo.fragment;


import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.nevo.R;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.util.Preferences;

/**
 * Created by med on 16/4/6.
 */
public class ProfileFragment extends PreferenceFragmentCompat {

    private ApplicationModel getModel() {
         return (ApplicationModel) getActivity().getApplication();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_profile);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EditTextPreference preference = (EditTextPreference) findPreference("fragment_edit_profile_height");
        preference.setTitle(getModel().getNevoUser().getHeight() + "");
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String value = o.toString();
                if (value.isEmpty() || !value.matches("[0-9]+")) {
                    return false;
                }
                preference.setTitle(value);
                getModel().getNevoUser().setHeight(Integer.parseInt(value));
                return true;
            }
        });

        preference = (EditTextPreference) findPreference("fragment_edit_profile_weight");
        preference.setTitle(getModel().getNevoUser().getWeight() + "");
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String value = o.toString();
                if (value.isEmpty() || !value.matches("[0-9]+")) {
                    return false;
                }
                preference.setTitle(value);
                getModel().getNevoUser().setWeight(Integer.parseInt(value));
                return true;
            }
        });

        preference = (EditTextPreference) findPreference("fragment_edit_profile_age");
        preference.setTitle(getModel().getNevoUser().getAge() + "");
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String value = o.toString();
                if (value.isEmpty() || !value.matches("[0-9]+")) {
                    return false;
                }
                preference.setTitle(value);
                getModel().getNevoUser().setAge(Integer.parseInt(value));
                return true;
            }
        });

        final String genderArray[] = getContext().getResources().getStringArray(R.array.profile_gender);
        Preference preferenceList = findPreference("fragment_edit_profile_gender");
        preferenceList.setTitle(genderArray[getModel().getNevoUser().getSex()]);
        preferenceList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String value = o.toString();
                if (value.isEmpty() || !value.matches("[0-9]+")) {
                    return false;
                }
                preference.setTitle(genderArray[Integer.parseInt(value)]);
                getModel().getNevoUser().setSex(Integer.parseInt(value));
                return true;
            }
        });

        final String unitArray[] = getContext().getResources().getStringArray(R.array.profile_unit);
        preferenceList = findPreference("fragment_edit_profile_unit");
        preferenceList.setTitle(unitArray[Preferences.getProfileUnit(getContext())]);
        preferenceList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String value = o.toString();
                if (value.isEmpty() || !value.matches("[0-9]+")) {
                    return false;
                }
                preference.setTitle(unitArray[Integer.parseInt(value)]);
                Preferences.setProfileUnit(getContext(), Integer.parseInt(value));
                return true;
            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
