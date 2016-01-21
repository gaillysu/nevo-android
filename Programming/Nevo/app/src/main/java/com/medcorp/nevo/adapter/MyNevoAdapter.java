package com.medcorp.nevo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.DfuActivity;
import com.medcorp.nevo.activity.MyNevoActivity;
import com.medcorp.nevo.model.MyNevo;
import com.medcorp.nevo.view.customfontview.RobotoTextView;

import java.util.ArrayList;


/**
 * Created by gaillysu on 15/12/28.
 */
public class MyNevoAdapter extends BaseAdapter {

    private Context context;
    private MyNevo mynevo;

    public MyNevoAdapter(Context context,MyNevo mynevo)
    {
        super();
        this.context = context;
        this.mynevo = mynevo;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return mynevo;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.activity_mynevo_list_view_item, parent, false);
        RobotoTextView title = (RobotoTextView)itemView.findViewById(R.id.activity_mynevo_list_view_item_title_label);
        RobotoTextView values = (RobotoTextView)itemView.findViewById(R.id.activity_mynevo_list_view_item_version_label);
        RobotoTextView infomation = (RobotoTextView)itemView.findViewById(R.id.activity_mynevo_list_view_item_infomation_label);
        ImageView image = (ImageView)itemView.findViewById(R.id.activity_mynevo_list_view_item_update_image);
        if(position == 0)
        {
            //TODO put in Strings.xml
            title.setText("Firmware");
            values.setText(mynevo.getBle_firmware_version() + "/" + mynevo.getMcu_firmware_version());
            if(mynevo.isAvailable_version())
            {
                //TODO put in Strings.xml
                infomation.setText("New version available");
                image.setVisibility(View.VISIBLE);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DfuActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("firmwares",(ArrayList<String>)mynevo.getFirmwareURLs());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        ((MyNevoActivity)context).finish();
                    }
                });
            }
            else
            {
                infomation.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
            }
        }
        else if(position == 1)
        {
            infomation.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
            //TODO put in Strings.xml
            title.setText("Battery");
            String str_battery="Battery low";
            if(mynevo.getBattery_level()==2)
            {
                str_battery = "Battery full";
            }
            else if(mynevo.getBattery_level()==1)
            {
                str_battery = "Battery half";
            }
            values.setText(str_battery);
        }
        else if(position == 2)
        {
            infomation.setVisibility(View.INVISIBLE);
            image.setVisibility(View.INVISIBLE);
            //TODO put in Strings.xml
            title.setText("Application");
            values.setText(mynevo.getApp_version());
        }

        return itemView;
    }
}
