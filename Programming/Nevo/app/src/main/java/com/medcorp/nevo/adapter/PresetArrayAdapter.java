package com.medcorp.nevo.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.medcorp.nevo.R;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.model.Preset;
import com.medcorp.nevo.view.customfontview.RobotoTextView;

import java.util.List;

/**
 * Created by gaillysu on 15/12/23.
 */
public class PresetArrayAdapter extends ArrayAdapter<Preset> {
    private Context context;
    private ApplicationModel model;
    private List<Preset> listPreset;

    public PresetArrayAdapter(Context context,ApplicationModel model, List<Preset> listPreset)
    {
        super(context,0,listPreset);
        this.context = context;
        this.model = model;
        this.listPreset = listPreset;
    }

    public void setDataset(List<Preset> listPreset)
    {
        this.listPreset = listPreset;
    }

    @Override
    public int getCount() {
        return listPreset==null?0:listPreset.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_goals_list_view_item, parent, false);
        RobotoTextView  presetLabel = (RobotoTextView)itemView.findViewById(R.id.activity_goals_list_view_item_goals_label);
        RobotoTextView  presetValue = (RobotoTextView)itemView.findViewById(R.id.activity_goals_list_view_item_goal_steps);
        SwitchCompat presetOnOff = (SwitchCompat) itemView.findViewById(R.id.activity_goals_list_view_item_goals_switch);
        final Preset preset = listPreset.get(position);
        presetLabel.setText(preset.getLabel());
        //TODO put in Strings incl format.xml
        presetValue.setText(preset.getSteps() + " steps");
        presetOnOff.setOnCheckedChangeListener(null);
        presetOnOff.setChecked(preset.isStatus());
        presetOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Preset preset = listPreset.get(position);
                preset.setStatus(isChecked);
                model.updatePreset(preset);
            }
        });
        return itemView;
    }
}
