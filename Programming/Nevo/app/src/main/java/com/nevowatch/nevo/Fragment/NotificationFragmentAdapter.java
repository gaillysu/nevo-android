package com.nevowatch.nevo.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.NotificationItem;

import java.util.List;

/**
 * NotificationFragmentAdapter populates items for ListView
 */
public class NotificationFragmentAdapter extends ArrayAdapter<NotificationItem> {

    private int mListItemResourceId;

    public NotificationFragmentAdapter(Context context, int mListItemResourceId, List<NotificationItem> objects){
        super(context, mListItemResourceId, objects);
        this.mListItemResourceId = mListItemResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NotificationItem item = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(mListItemResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.mIcon = (ImageView) view.findViewById(R.id.typeIconImage);
            viewHolder.mLabel = (TextView) view.findViewById(R.id.typeTextView);
          //  viewHolder.mSwitch = (Switch) view.findViewById(R.id.typeSwitch);
            viewHolder.mImage = (ImageView) view.findViewById(R.id.typeImage);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mIcon.setImageResource(item.getmIcon());
        viewHolder.mLabel.setText(item.getmLabel());
        viewHolder.mImage.setImageResource(item.getmImage());
        return view;
    }

    class ViewHolder{

        ImageView mIcon;
        TextView mLabel;
     //   Switch mSwitch;
        ImageView mImage;
    }
}
