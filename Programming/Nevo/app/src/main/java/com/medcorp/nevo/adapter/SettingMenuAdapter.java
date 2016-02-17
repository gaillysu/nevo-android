package com.medcorp.nevo.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.listener.OnCheckedChangeInListListener;
import com.medcorp.nevo.model.SettingsMenuItem;
import com.medcorp.nevo.view.customfontview.RobotoTextView;

import java.util.List;

/**
 * Created by gaillysu on 16/1/6.
 */
public class SettingMenuAdapter extends ArrayAdapter<SettingsMenuItem> {
    private Context context;
    private List<SettingsMenuItem> listMenu;
    private OnCheckedChangeInListListener onCheckedChangeInListListener;

    public SettingMenuAdapter(Context context,List<SettingsMenuItem> listMenu, OnCheckedChangeInListListener listener){
        super(context,0,listMenu);
        this.context = context;
        this.listMenu = listMenu;
        this.onCheckedChangeInListListener = listener;
    }

    public SettingMenuAdapter(Context context,List<SettingsMenuItem> listMenu){
        super(context,0,listMenu);
        this.context = context;
        this.listMenu = listMenu;
        onCheckedChangeInListListener = null;
    }

    @Override
    public int getCount() {
        return listMenu==null?0:listMenu.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_setting_menu_list_view_item, parent, false);
        ImageView menuImage = (ImageView) itemView.findViewById(R.id.activity_setting_menu_image);
        RobotoTextView menuNameTextView = (RobotoTextView) itemView.findViewById(R.id.activity_setting_menu_name);
        SwitchCompat onOffSwitch = (SwitchCompat) itemView.findViewById(R.id.activity_setting_menu_switch);
        menuImage.setImageResource(listMenu.get(position).getIcon());
        menuNameTextView.setText(listMenu.get(position).getTitle());
        if(listMenu.get(position).isWithSwitch())
        {
            onOffSwitch.setVisibility(View.VISIBLE);
            onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onCheckedChangeInListListener.onCheckedChange(buttonView,isChecked,position);
                }
            });
        } else {
            onOffSwitch.setVisibility(View.INVISIBLE);
        }
        return itemView;
    }
}
