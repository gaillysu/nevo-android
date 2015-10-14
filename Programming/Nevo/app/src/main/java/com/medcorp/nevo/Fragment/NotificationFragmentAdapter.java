package com.medcorp.nevo.Fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;

import com.medcorp.nevo.R;
import com.medcorp.nevo.Activity.PaletteActivity;
import com.medcorp.nevo.ble.datasource.NotificationDataHelper;
import com.medcorp.nevo.ble.model.notification.Notification;
import com.medcorp.nevo.ble.model.notification.CalendarNotification;
import com.medcorp.nevo.ble.model.notification.EmailNotification;
import com.medcorp.nevo.ble.model.notification.FacebookNotification;
import com.medcorp.nevo.ble.model.notification.SmsNotification;
import com.medcorp.nevo.ble.model.notification.TelephoneNotification;
import com.medcorp.nevo.ble.model.notification.WeChatNotification;
import com.medcorp.nevo.ble.model.notification.WhatsappNotification;
import com.medcorp.nevo.ble.model.notification.visitor.NotificationColorGetter;
import com.medcorp.nevo.ble.model.color.BlueLed;
import com.medcorp.nevo.ble.model.color.GreenLed;
import com.medcorp.nevo.ble.model.color.LightGreenLed;
import com.medcorp.nevo.ble.model.color.OrangeLed;
import com.medcorp.nevo.ble.model.color.RedLed;
import com.medcorp.nevo.ble.model.color.UnknownLed;
import com.medcorp.nevo.ble.model.color.YellowLed;
import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitable;
import com.medcorp.nevo.ble.model.color.visitor.NevoLedVisitor;
import com.medcorp.nevo.View.NotificationItem;
import com.medcorp.nevo.View.customfontview.RalewayTextView;

import java.util.List;

/**
 * NotificationFragmentAdapter populates items for ListView
 */
public class NotificationFragmentAdapter extends ArrayAdapter<NotificationItem>
        implements Switch.OnCheckedChangeListener, View.OnClickListener{

    private int listItemResourceId;
    private Context context;
    private ListView listView;
    private NotificationDataHelper helper;
    private NotificationColorGetter getter;

    public NotificationFragmentAdapter(Context context, int mListItemResourceId, List<NotificationItem> objects, ListView listView){
        super(context, mListItemResourceId, objects);
        this.listItemResourceId = mListItemResourceId;
        this.context = context;
        this.listView = listView;
        this.helper = new NotificationDataHelper(context);
        this.getter = new NotificationColorGetter(context);
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

        LedImageVisitor imageVisitor = new LedImageVisitor();
        switch (position){
            case 0:
                initWidgetHelper(new TelephoneNotification(), viewHolder, imageVisitor);
                break;
            case 1:
                initWidgetHelper(new EmailNotification(), viewHolder, imageVisitor);
                break;
            case 2:
                initWidgetHelper(new FacebookNotification(), viewHolder, imageVisitor);
                break;
            case 3:
                initWidgetHelper(new SmsNotification(), viewHolder, imageVisitor);
                break;
            case 4:
                initWidgetHelper(new CalendarNotification(), viewHolder, imageVisitor);
                break;
            case 5:
                initWidgetHelper(new WeChatNotification(), viewHolder, imageVisitor);
                break;
            case 6:
                initWidgetHelper(new WhatsappNotification(), viewHolder, imageVisitor);
                break;
            default:
                viewHolder.mSwitch.setChecked(false);
                break;
        }
    }

    private void initWidgetHelper(Notification applicationNotification, ViewHolder viewHolder, LedImageVisitor imageVisitor){
        applicationNotification = helper.getState(applicationNotification);
        viewHolder.mSwitch.setChecked(applicationNotification.isOn());
        setImg(viewHolder, applicationNotification.isOn());
        viewHolder.mIcon.setImageResource(new TelephoneNotification().accept(getter).accept(imageVisitor));
    }

    private void checkedImg(int pos, boolean isChecked){
        LinearLayout linearLayout = (LinearLayout) getViewByPosition(pos, listView);
        RalewayTextView tv = (RalewayTextView) linearLayout.findViewById(R.id.typeTextView);
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
                helper.saveState(new TelephoneNotification(isChecked));
                checkedImg(position, isChecked);
                break;
            case 1:
                helper.saveState(new EmailNotification(isChecked));
                checkedImg(position, isChecked);
                break;
            case 2:
                helper.saveState(new FacebookNotification(isChecked));
                checkedImg(position, isChecked);

                break;
            case 3:
                helper.saveState(new SmsNotification(isChecked));
                checkedImg(position, isChecked);
                break;
            case 4:
                helper.saveState(new CalendarNotification(isChecked));
                checkedImg(position, isChecked);
                break;
            case 5:
                helper.saveState(new WeChatNotification(isChecked));
                checkedImg(position, isChecked);
                break;
            case 6:
                helper.saveState(new WhatsappNotification(isChecked));
                checkedImg(position, isChecked);
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, PaletteActivity.class);
        intent.putExtra("Position", (int)v.getTag());
        context.startActivity(intent);
    }

    public class ViewHolder{
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


    private class LedImageVisitor implements NevoLedVisitor<Integer>{

        @Override
        public Integer visit(BlueLed led) {
            return R.drawable.blue_indicator;
        }

        @Override
        public Integer visit(GreenLed led) {
            return R.drawable.green_indicator;
        }

        @Override
        public Integer visit(LightGreenLed led) {
            return R.drawable.grass_green_indicator;
        }

        @Override
        public Integer visit(OrangeLed led) {
            return R.drawable.orange_indicator;
        }

        @Override
        public Integer visit(RedLed led) {
            return R.drawable.red_indicator;
        }

        @Override
        public Integer visit(YellowLed led) {
            return R.drawable.yellow_indicator;
        }

        @Override
        public Integer visit(UnknownLed led) {
            return 0;
        }

        @Override
        public Integer visit(NevoLedVisitable led) {
            return 0;
        }

    }

}
