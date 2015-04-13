package com.nevowatch.nevo.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nevowatch.nevo.View.DrawerIcon;
import com.nevowatch.nevo.R;

import java.util.List;

/**
 * NavigationDrawerAdapter provides items for ListView
 */
public class NavigationDrawerAdapter extends ArrayAdapter<DrawerIcon> {

    private int mListItemResourceId;

    public NavigationDrawerAdapter(Context context, int mListItemResourceId, List<DrawerIcon> objects){
        super(context, mListItemResourceId, objects);
        this.mListItemResourceId = mListItemResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DrawerIcon drawerIcon = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(mListItemResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.iconImage = (ImageView) view.findViewById(R.id.drawer_icon_imageView);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.iconImage.setImageResource(drawerIcon.getmIconId());
        return view;
    }

   class ViewHolder{

        ImageView iconImage;
    }
}
