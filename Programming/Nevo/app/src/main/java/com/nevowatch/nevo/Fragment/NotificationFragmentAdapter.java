package com.nevowatch.nevo.Fragment;

import android.content.Context;
import android.content.Intent;
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

import com.nevowatch.nevo.PaletteActivity;
import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.NotificationItem;

import java.util.List;

/**
 * NotificationFragmentAdapter populates items for ListView
 */
public class NotificationFragmentAdapter extends ArrayAdapter<NotificationItem>
        implements View.OnClickListener, Switch.OnCheckedChangeListener{

    private int mListItemResourceId;
    private Context mCtx;
    private static final String TELETYPE = "tele";
    private static final String EMAILTYPE = "email";
    private static final String FACETYPE = "facebook";
    private static final String SMSTYPE = "sms";
    private static final String CALTYPE = "calendar";
    private static final String WEICHATTYPE = "weichat";

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
            viewHolder.mSwitch = (Switch) view.findViewById(R.id.typeSwitch);
            viewHolder.mImage = (ImageView) view.findViewById(R.id.typeImage);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mIcon.setImageResource(item.getmIcon());
        viewHolder.mLabel.setText(item.getmLabel());
        viewHolder.mImage.setImageResource(item.getmImage());
        viewHolder.mImage.setTag(position);
        viewHolder.mImage.setOnClickListener(this);
        viewHolder.mSwitch.setTag(position);
        viewHolder.mSwitch.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        Intent intent = new Intent(mCtx, PaletteActivity.class);
        intent.putExtra("Position", position);
        mCtx.startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch ((int)buttonView.getTag()){
            case 0:
                if(isChecked){
                    saveTeleTypeToPreference(mCtx, true);
                    Log.d("STATE0", "ON");
                }else {
                    saveTeleTypeToPreference(mCtx, false);
                    Log.d("STATE0", "OFF");
                }
                break;
            case 1:
                if(isChecked){
                    saveEmailTypeToPreference(mCtx, true);
                    Log.d("STATE1", "ON");
                }else {
                    saveEmailTypeToPreference(mCtx, false);
                    Log.d("STATE1", "OFF");
                }
                break;
            case 2:
                if(isChecked){
                    saveFaceTypeToPreference(mCtx, true);
                }else {
                    saveFaceTypeToPreference(mCtx, false);
                }
                break;
            case 3:
                if(isChecked){
                    saveSmsTypeToPreference(mCtx, true);
                }else {
                    saveSmsTypeToPreference(mCtx, false);
                }
                break;
            case 4:
                if(isChecked){
                    saveCalTypeToPreference(mCtx, true);
                }else {
                    saveCalTypeToPreference(mCtx, false);
                }
                break;
            case 5:
                if(isChecked){
                    saveWeiChatTypeToPreference(mCtx, true);
                }else {
                    saveWeiChatTypeToPreference(mCtx, false);
                }
                break;
            default:
                break;
        }
    }

    public static void saveTeleTypeToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(TELETYPE, value).apply();
    }
    public static Boolean getTeleTypeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(TELETYPE, false);
    }
    public static void saveEmailTypeToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(EMAILTYPE, value).apply();
    }
    public static Boolean getEmailTypeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(EMAILTYPE, false);
    }
    public static void saveFaceTypeToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(FACETYPE, value).apply();
    }
    public static Boolean getFaceTypeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(FACETYPE, false);
    }
    public static void saveSmsTypeToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(SMSTYPE, value).apply();
    }
    public static Boolean getSmsTypeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(SMSTYPE, false);
    }
    public static void saveCalTypeToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(CALTYPE, value).apply();
    }
    public static Boolean getCalTypeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(CALTYPE, false);
    }
    public static void saveWeiChatTypeToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(WEICHATTYPE, value).apply();
    }
    public static Boolean getWeiChatTypeFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(WEICHATTYPE, false);
    }

    class ViewHolder{

        ImageView mIcon;
        TextView mLabel;
        Switch mSwitch;
        ImageView mImage;
    }
}
