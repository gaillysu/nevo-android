package com.medcorp.nevo.activity;

import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.base.BasePreferencesFragment;
import com.medcorp.nevo.model.Preset;
import com.medcorp.nevo.view.ToastHelper;

/**
 * Created by gaillysu on 15/12/23.
 */
public class EditGoalsFragment extends BasePreferencesFragment{

    private Preset preset;
    private Preference presetPreferences;
    private EditTextPreference labelPreferences;
    private Preference deletePreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.edit_preset);
        Bundle bundle =  getArguments();
        //TODO put in keys.xml
        preset = getModel().getPresetById(bundle.getInt("Preset_ID"));
        getAppCompatActivity().supportInvalidateOptionsMenu();
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_menu:
                if(getModel().updatePreset(preset)){
                    //TODO put in Strings.xml
                    ToastHelper.showShortToast(getContext(),"Saved Goal!");
                    getAppCompatActivity().setResult(1);
                    getAppCompatActivity().finish();
                }else{
                    //TODO put in Strings.xml
                    ToastHelper.showShortToast(getContext(),"Couldn't save the goal.!");
                }
                return true;
            case android.R.id.home:
                getAppCompatActivity().setResult(0);
                getAppCompatActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO put in keys.xml
        presetPreferences = findPreference("fragment_edit_preset_value");
        presetPreferences.setTitle(preset.getSteps()+"");
        presetPreferences.setOnPreferenceClickListener(presetPrefClickListener);
        labelPreferences = (EditTextPreference) findPreference("fragment_edit_preset_label");
        labelPreferences.setTitle(preset.getLabel());
        labelPreferences.setText(preset.getLabel());
        labelPreferences.setOnPreferenceChangeListener(labelPrefListener);
        deletePreferences = findPreference("fragment_edit_preset_delete");
        deletePreferences.setOnPreferenceClickListener(deletePrefClickListener);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    Preference.OnPreferenceClickListener presetPrefClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            new MaterialDialog.Builder(getActivity())
                    //TODO put in Strings.xml
                    .title("Edit Goal")
                    .content("input your goal.")
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input("", ""+preset.getSteps(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if(input.length()==0)return;
                            presetPreferences.setTitle(input);
                            preset.setSteps(Integer.parseInt(input.toString()));
                        }
                    }).negativeText("Cancel").show();
            return true;
        }
    } ;

    Preference.OnPreferenceChangeListener labelPrefListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String changedLabel = o.toString();
            preference.setTitle(changedLabel);
            preset.setLabel(changedLabel);
            return true;
        }
    };

    Preference.OnPreferenceClickListener deletePrefClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(!getModel().deleteAlarm(preset)){
                //TODO put in Strings.xml
                ToastHelper.showShortToast(getContext(), "Failed to delete goal");
            }else{
                //TODO put in Strings.xml
                ToastHelper.showShortToast(getContext(), "Deleted goal!");
            }
            getAppCompatActivity().setResult(-1);
            getAppCompatActivity().finish();
            return true;
        }
    } ;


}