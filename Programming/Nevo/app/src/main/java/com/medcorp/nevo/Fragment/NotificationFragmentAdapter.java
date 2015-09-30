package com.medcorp.nevo.fragment;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.PaletteActivity;
import com.medcorp.nevo.view.NotificationItem;
import com.medcorp.nevo.view.customfontview.RalewayTextView;

import java.util.List;

/**
 * NotificationFragmentAdapter populates items for ListView
 */
public class NotificationFragmentAdapter extends ArrayAdapter<NotificationItem>
        implements Switch.OnCheckedChangeListener, View.OnClickListener{

    private int listItemResourceId;
    private Context context;
    private ListView listView;
    public static final String TELETYPE = "tele";
    public static final String EMAILTYPE = "email";
    public static final String FACETYPE = "facebook";
    public static final String SMSTYPE = "sms";
    public static final String CALTYPE = "calendar";
    public static final String WEICHATTYPE = "weichat";
    public static final String WHATSTYPE = "whatsapp";

    public NotificationFragmentAdapter(Context context, int mListItemResourceId, List<NotificationItem> objects, ListView listView){
        super(context, mListItemResourceId, objects);
        this.listItemResourceId = mListItemResourceId;
        this.context = context;
        this.listView = listView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NotificationItem item = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(listItemResourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.mIcon = (ImageView) view.findViewById(R.id.typeIconImage);
            viewHolder.mLabel = (RalewayTextView) view.findViewById(R.id.typeTextView);
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
        initWidget(viewHolder, position);
        viewHolder.mSwitch.setOnCheckedChangeListener(this);
        return view;
    }

    private int iconResource(int choosenColor){
        switch (choosenColor){
            case PaletteActivity.BLUE_LED:
                return R.drawable.blue_indicator;
            case PaletteActivity.YELLOW_LED:
                return R.drawable.yellow_indicator;
            case PaletteActivity.GREEN_LED:
                return R.drawable.green_indicator;
            case PaletteActivity.LIGHTGREEN_LED:
                return R.drawable.grass_green_indicator;
            case PaletteActivity.ORANGE_LED:
                return R.drawable.orange_indicator;
            case PaletteActivity.RED_LED:
                return R.drawable.red_indicator;
            default:
                break;
        }
        return 0;
    }

    private void setImg(ViewHolder viewHolder, boolean isChecked){
        if(isChecked){
            viewHolder.mLabel.setTextColor(context.getResources().getColor(R.color.customBlack));
            viewHolder.mIcon.setVisibility(View.VISIBLE);
        }else {
            viewHolder.mLabel.setTextColor(context.getResources().getColor(R.color.customGray));
            viewHolder.mIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void initWidget(ViewHolder viewHolder, int position){
        switch (position){
            case 0:
                viewHolder.mSwitch.setChecked(getTypeNFState(context, TELETYPE));
                setImg(viewHolder, getTypeNFState(context, TELETYPE));
                viewHolder.mIcon.setImageResource(iconResource(PaletteActivity.getTypeChoosenColor(context, PaletteActivity.TELECHOOSENCOLOR)));
                break;
            case 1:
                viewHolder.mSwitch.setChecked(getTypeNFState(context, EMAILTYPE));
                setImg(viewHolder, getTypeNFState(context, EMAILTYPE));
                viewHolder.mIcon.setImageResource(iconResource(PaletteActivity.getTypeChoosenColor(context, PaletteActivity.EMAILCHOOSENCOLOR)));
                break;
            case 2:
                viewHolder.mSwitch.setChecked(getTypeNFState(context, FACETYPE));
                setImg(viewHolder, getTypeNFState(context, FACETYPE));
                viewHolder.mIcon.setImageResource(iconResource(PaletteActivity.getTypeChoosenColor(context, PaletteActivity.FACECHOOSENCOLOR)));
                break;
            case 3:
                viewHolder.mSwitch.setChecked(getTypeNFState(context, SMSTYPE));
                setImg(viewHolder, getTypeNFState(context, SMSTYPE));
                viewHolder.mIcon.setImageResource(iconResource(PaletteActivity.getTypeChoosenColor(context, PaletteActivity.SMSCHOOSENCOLOR)));
                break;
            case 4:
                viewHolder.mSwitch.setChecked(getTypeNFState(context, CALTYPE));
                setImg(viewHolder, getTypeNFState(context, CALTYPE));
                viewHolder.mIcon.setImageResource(iconResource(PaletteActivity.getTypeChoosenColor(context, PaletteActivity.CALCHOOSENCOLOR)));
                break;
            case 5:
                viewHolder.mSwitch.setChecked(getTypeNFState(context, WEICHATTYPE));
                setImg(viewHolder, getTypeNFState(context, WEICHATTYPE));
                viewHolder.mIcon.setImageResource(iconResource(PaletteActivity.getTypeChoosenColor(context, PaletteActivity.WECHATCHOOSENCOLOR)));
                break;
            case 6:
                viewHolder.mSwitch.setChecked(getTypeNFState(context, WHATSTYPE));
                setImg(viewHolder, getTypeNFState(context, WHATSTYPE));
                viewHolder.mIcon.setImageResource(iconResource(PaletteActivity.getTypeChoosenColor(context, PaletteActivity.WHATSAPPCHOOSENCOLOR)));
                break;
            default:
                viewHolder.mSwitch.setChecked(false);
                break;
        }
    }

    private void checkedImg(int pos, boolean isChecked){
        LinearLayout linearLayout = (LinearLayout) getViewByPosition(pos, listView);
        TextView tv = (TextView) linearLayout.findViewById(R.id.typeTextView);
        ImageView icon = (ImageView) linearLayout.findViewById(R.id.typeIconImage);
        if(isChecked){
            tv.setTextColor(context.getResources().getColor(R.color.customBlack));
            icon.setVisibility(View.VISIBLE);
        }else {
            tv.setTextColor(context.getResources().getColor(R.color.customGray));
            icon.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = (int)buttonView.getTag();

        switch (position){
            case 0:
                if(isChecked){
                    saveTypeNFState(context, TELETYPE, true);
                    checkedImg(position, isChecked);
                }else {
                    saveTypeNFState(context, TELETYPE, false);
                    checkedImg(position, isChecked);
                }
                break;
            case 1:
                if(isChecked){
                    saveTypeNFState(context, EMAILTYPE, true);
                    checkedImg(position, isChecked);
                }else {
                    saveTypeNFState(context, EMAILTYPE, false);
                    checkedImg(position, isChecked);
                }
                break;
            case 2:
                if(isChecked){
                    saveTypeNFState(context, FACETYPE, true);
                    checkedImg(position, isChecked);
                }else {
                    saveTypeNFState(context, FACETYPE, false);
                    checkedImg(position, isChecked);
                }
                break;
            case 3:
                if(isChecked){
                    saveTypeNFState(context, SMSTYPE, true);
                    checkedImg(position, isChecked);
                }else {
                    saveTypeNFState(context, SMSTYPE, false);
                    checkedImg(position, isChecked);
                }
                break;
            case 4:
                if(isChecked){
                    saveTypeNFState(context, CALTYPE, true);
                    checkedImg(position, isChecked);
                }else {
                    saveTypeNFState(context, CALTYPE, false);
                    checkedImg(position, isChecked);
                }
                break;
            case 5:
                if(isChecked){
                    saveTypeNFState(context, WEICHATTYPE, true);
                    checkedImg(position, isChecked);
                }else {
                    saveTypeNFState(context, WEICHATTYPE, false);
                    checkedImg(position, isChecked);
                }
                break;
            case 6:
                if(isChecked){
                    saveTypeNFState(context, WHATSTYPE, true);
                    checkedImg(position, isChecked);
                }else {
                    saveTypeNFState(context, WHATSTYPE, false);
                    checkedImg(position, isChecked);
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
        }else if(tag.equals(WHATSTYPE)){
            pref.edit().putBoolean(WHATSTYPE, value).apply();
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
        }else if(tag.equals(WHATSTYPE)){
            return pref.getBoolean(WHATSTYPE, false);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, PaletteActivity.class);
        intent.putExtra("Position", (int)v.getTag());
        context.startActivity(intent);
    }

    class ViewHolder{
        ImageView mIcon;
        RalewayTextView mLabel;
        Switch mSwitch;
        ImageView mImage;
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
