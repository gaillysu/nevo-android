package com.medcorp.nevo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;

import com.medcorp.nevo.R;
import com.medcorp.nevo.model.Preset;
import com.medcorp.nevo.view.customfontview.RobotoTextView;

import java.util.List;

/**
 * Created by gaillysu on 15/12/23.
 */
public class PresetArrayAdapter extends ArrayAdapter<Preset> {
    private Context context;
    private List<Preset> listPreset;
    public PresetArrayAdapter(Context context, List<Preset> listPreset)
    {
        super(context,0,listPreset);
        this.context = context;
        this.listPreset = listPreset;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_goals_list_view_item, parent, false);
        RobotoTextView  presetLabel = (RobotoTextView)itemView.findViewById(R.id.activity_goals_list_view_item_goals_label);
        RobotoTextView  presetValue = (RobotoTextView)itemView.findViewById(R.id.activity_goals_list_view_item_goal_steps);
        Switch presetOnOff = (Switch) itemView.findViewById(R.id.activity_goals_list_view_item_goals_switch);
        Preset preset = listPreset.get(position);
        presetLabel.setText(preset.getLabel());
        presetValue.setText(preset.getSteps() + " steps");
        presetOnOff.setChecked(preset.isStatus());
        return itemView;
    }
}
