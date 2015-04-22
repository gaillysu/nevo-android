package com.nevowatch.nevo.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.NotificationItem;
import com.nevowatch.nevo.View.SwitchButton;

import java.util.List;

/**
 * NotificationFragmentAdapter populates items for ListView
 */
public class NotificationFragmentAdapter extends ArrayAdapter<NotificationItem>
        implements Switch.OnCheckedChangeListener{

    private int mListItemResourceId;
    private Context mCtx;
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
            viewHolder.mIcon = (ImageView) view.findViewById(R.id.typeIconImage);
            viewHolder.mLabel = (TextView) view.findViewById(R.id.typeTextView);
            viewHolder.mSwitch = (SwitchButton) view.findViewById(R.id.typeSwitch);
            viewHolder.mImage = (ImageView) view.findViewById(R.id.typeImage);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mIcon.setImageResource(item.getmIcon());
        viewHolder.mLabel.setText(item.getmLabel());
        viewHolder.mImage.setImageResource(item.getmImage());
        viewHolder.mSwitch.setTag(position);
        viewHolder.mSwitch.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch ((int)buttonView.getTag()){
            case 0:
                if(isChecked){
                    saveTypeNFState(mCtx, TELETYPE, true);
                    Log.d("SWITCH0", "ON");
                }else {
                    saveTypeNFState(mCtx, TELETYPE, false);
                    Log.d("SWITCH0", "OFF");
                }
                break;
            case 1:
                if(isChecked){
                    saveTypeNFState(mCtx, EMAILTYPE, true);
                    Log.d("SWITCH1", "ON");
                }else {
                    saveTypeNFState(mCtx, EMAILTYPE, false);
                    Log.d("SWITCH1", "OFF");
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

    class ViewHolder{

        ImageView mIcon;
        TextView mLabel;
        SwitchButton mSwitch;
        ImageView mImage;
    }
}
