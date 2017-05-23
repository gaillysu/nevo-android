package com.medcorp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.model.ScanDurationItemModel;

import java.util.List;

/**
 * Created by Jason on 2017/5/22.
 */

public class ScanDurationAdapter extends BaseAdapter {
    private Context mContext;
    private List<ScanDurationItemModel> listData;

    public ScanDurationAdapter(Context context, List<ScanDurationItemModel> listData) {
        mContext = context;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position) != null ? listData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.scan_duration_adapter_item, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.duration = (TextView) convertView.findViewById(R.id.scan_duration_item_time);
            holder.select = (ImageView) convertView.findViewById(R.id.scan_duration_select_iv);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanDurationItemModel scanDurationItemModel = listData.get(position);
        if (scanDurationItemModel != null) {
            if (scanDurationItemModel.isSelect()) {
                holder.select.setVisibility(View.VISIBLE);
            } else {
                holder.select.setVisibility(View.GONE);
            }
            if(scanDurationItemModel.getTime()==60){
                holder.duration.setText(mContext.getString(R.string.scan_duration_item_select_one_hour));
            }else{
                holder.duration.setText(scanDurationItemModel.getTime()+mContext.getString(R.string.scan_duration_time_unit));
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView duration;
        ImageView select;
    }
}
