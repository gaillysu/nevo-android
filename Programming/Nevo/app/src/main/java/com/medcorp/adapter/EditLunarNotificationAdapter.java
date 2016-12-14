package com.medcorp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medcorp.R;
import com.medcorp.ble.model.color.LedLamp;
import com.medcorp.ble.model.notification.Notification;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * Created by Jason on 2016/12/8.
 *
 */

public class EditLunarNotificationAdapter extends SwipeMenuAdapter<EditLunarNotificationAdapter.ViewHolder> {

    private List<LedLamp> allList;
    private OnItemClickListener mOnItemClickListener;
    private static Notification mNotification;

    public EditLunarNotificationAdapter(List<LedLamp> list, Notification notification) {
        this.allList = list;
        this.mNotification = notification;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_lunar_notification_lamp_item, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(allList.get(position));
        holder.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return allList.size() > 0 ? allList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnItemClickListener mOnItemClickListener;
        TextView name;
        ImageView colorPoint;
        ImageView isSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.edit_notification_name_text_view);
            colorPoint = (ImageView) itemView.findViewById(R.id.edit_notification_lamp_iv);
            isSelect = (ImageView) itemView.findViewById(R.id.open_notification_select_lamp_flag);
        }

        public void setData(LedLamp data) {
            if (data != null) {
                name.setText(data.getName());
                colorPoint.setColorFilter(data.getColor());
                if (data.getHexColor() == mNotification.getDefaultColor().getHexColor()) {
                    isSelect.setVisibility(View.VISIBLE);
                } else {
                    isSelect.setVisibility(View.GONE);
                }
            }
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
