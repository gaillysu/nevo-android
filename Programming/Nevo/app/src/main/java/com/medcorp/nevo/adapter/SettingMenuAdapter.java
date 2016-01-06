package com.medcorp.nevo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.medcorp.nevo.R;
import com.medcorp.nevo.model.SettingMenu;
import com.medcorp.nevo.util.Preferences;
import com.medcorp.nevo.view.customfontview.RobotoTextView;

import java.util.List;

/**
 * Created by gaillysu on 16/1/6.
 */
public class SettingMenuAdapter extends ArrayAdapter<SettingMenu> {
    private Context context;
    private List<SettingMenu> listMenu;

    public SettingMenuAdapter(Context context,List<SettingMenu> listMenu){
        super(context,0,listMenu);
        this.context = context;
        this.listMenu = listMenu;
    }

    @Override
    public int getCount() {
        return listMenu==null?0:listMenu.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_setting_menu_list_view_item, parent, false);

        ImageView menuImage = (ImageView) itemView.findViewById(R.id.activity_setting_menu_image);
        RobotoTextView menuNameTextView = (RobotoTextView) itemView.findViewById(R.id.activity_setting_menu_name);
        Switch onOffSwitch = (Switch) itemView.findViewById(R.id.activity_setting_menu_switch);

        menuImage.setImageDrawable(context.getDrawable(listMenu.get(position).getIcon()));
        menuNameTextView.setText(listMenu.get(position).getTitle());
        if(listMenu.get(position).isWithSwitch())
        {
            onOffSwitch.setVisibility(View.VISIBLE);
            onOffSwitch.setOnCheckedChangeListener(null);
            onOffSwitch.setChecked(Preferences.getLinklossNotification(context));
            onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Preferences.saveLinklossNotification(context,isChecked);
                }
            });
        }
        else
        {
            onOffSwitch.setVisibility(View.INVISIBLE);
        }
        return itemView;
    }
}
