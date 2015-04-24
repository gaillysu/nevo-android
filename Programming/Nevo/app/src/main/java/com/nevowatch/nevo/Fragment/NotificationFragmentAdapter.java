package com.nevowatch.nevo.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.nevowatch.nevo.FontManager;
import com.nevowatch.nevo.MainActivity;
import com.nevowatch.nevo.PaletteActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.NotificationItem;

import java.util.List;

/**
 * NotificationFragmentAdapter populates items for ListView
 */
public class NotificationFragmentAdapter extends ArrayAdapter<NotificationItem>
        implements Switch.OnCheckedChangeListener, View.OnClickListener{

    private int mListItemResourceId;
    private Context mCtx;
    private View [] mViewArray;
    public static final String TELETYPE = "tele";
    public static final String EMAILTYPE = "email";
    public static final String FACETYPE = "facebook";
    public static final String SMSTYPE = "sms";
    public static final String CALTYPE = "calendar";
    public static final String WEICHATTYPE = "weichat";

    public NotificationFragmentAdapter(Context context, int mListItemResourceId, List<NotificationItem> objects){
        super(context, mListItemResourceId, objects);
        this.mListItemResourceId = mListItemResourceId;
        this.mCtx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NotificationItem item = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(mListItemResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.mLabel = (TextView) view.findViewById(R.id.typeTextView);
            viewHolder.mSwitch = (Switch) view.findViewById(R.id.typeSwitch);
            viewHolder.mImage = (ImageView) view.findViewById(R.id.typeImage);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mLabel.setText(item.getmLabel());
        viewHolder.mImage.setImageResource(item.getmImage());
        viewHolder.mImage.setTag(position);
        viewHolder.mImage.setOnClickListener(this);
        viewHolder.mSwitch.setTag(position);
        initSwitch(viewHolder.mSwitch, position);
        viewHolder.mSwitch.setOnCheckedChangeListener(this);

        mViewArray = new View []{
                viewHolder.mLabel
        };
        FontManager.changeFonts(mViewArray, (MainActivity) mCtx);

        return view;
    }

    private void initSwitch(Switch sw, int position){
        switch (position){
            case 0:
                sw.setChecked(getTypeNFState(mCtx, TELETYPE));
                break;
            case 1:
                sw.setChecked(getTypeNFState(mCtx, EMAILTYPE));
                break;
            case 2:
                sw.setChecked(getTypeNFState(mCtx, FACETYPE));
                break;
            case 3:
                sw.setChecked(getTypeNFState(mCtx, SMSTYPE));
                break;
            case 4:
                sw.setChecked(getTypeNFState(mCtx, CALTYPE));
                break;
            case 5:
                sw.setChecked(getTypeNFState(mCtx, WEICHATTYPE));
                break;
            default:
                sw.setChecked(false);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch ((int)buttonView.getTag()){
            case 0:
                if(isChecked){
                    saveTypeNFState(mCtx, TELETYPE, true);
                }else {
                    saveTypeNFState(mCtx, TELETYPE, false);
                }
                break;
            case 1:
                if(isChecked){
                    saveTypeNFState(mCtx, EMAILTYPE, true);
                }else {
                    saveTypeNFState(mCtx, EMAILTYPE, false);
                }
                break;
            case 2:
                if(isChecked){
                    saveTypeNFState(mCtx, FACETYPE, true);
                }else {
                    saveTypeNFState(mCtx, FACETYPE, false);
                }
                break;
            case 3:
                if(isChecked){
                    saveTypeNFState(mCtx, SMSTYPE, true);
                }else {
                    saveTypeNFState(mCtx, SMSTYPE, false);
                }
                break;
            case 4:
                if(isChecked){
                    saveTypeNFState(mCtx, CALTYPE, true);
                }else {
                    saveTypeNFState(mCtx, CALTYPE, false);
                }
                break;
            case 5:
                if(isChecked){
                    saveTypeNFState(mCtx, WEICHATTYPE, true);
                }else {
                    saveTypeNFState(mCtx, WEICHATTYPE, false);
                }
                break;
            default:
                break;
        }
    }

    public static void saveTypeNFState(Context context, String tag, boolean value){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(tag.equals(TELETYPE)){
            pref.edit().putBoolean(TELETYPE, value).apply();
        }else if(tag.equals(EMAILTYPE)){
            pref.edit().putBoolean(EMAILTYPE, value).apply();
        }else if(tag.equals(FACETYPE)){
            pref.edit().putBoolean(FACETYPE, value).apply();
        }else if(tag.equals(SMSTYPE)){
            pref.edit().putBoolean(SMSTYPE, value).apply();
        }else if(tag.equals(CALTYPE)){
            pref.edit().putBoolean(CALTYPE, value).apply();
        }else if(tag.equals(WEICHATTYPE)){
            pref.edit().putBoolean(WEICHATTYPE, value).apply();
        }
    }

    public static Boolean getTypeNFState(Context context, String tag){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(tag.equals(TELETYPE)){
            return pref.getBoolean(TELETYPE, false);
        }else if(tag.equals(EMAILTYPE)){
            return pref.getBoolean(EMAILTYPE, false);
        }else if(tag.equals(FACETYPE)){
            return pref.getBoolean(FACETYPE, false);
        }else if(tag.equals(SMSTYPE)){
            return pref.getBoolean(SMSTYPE, false);
        }else if(tag.equals(CALTYPE)){
            return pref.getBoolean(CALTYPE, false);
        }else if(tag.equals(WEICHATTYPE)){
            return pref.getBoolean(WEICHATTYPE, false);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mCtx, PaletteActivity.class);
        intent.putExtra("Position", (int)v.getTag());
        mCtx.startActivity(intent);
    }

    class ViewHolder{

        TextView mLabel;
        Switch mSwitch;
        ImageView mImage;
    }
}
