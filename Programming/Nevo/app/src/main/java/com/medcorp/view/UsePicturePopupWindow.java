package com.medcorp.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.medcorp.R;

/**
 * Created by Jason on 2016/10/27.
 */

public class UsePicturePopupWindow extends PopupWindow {

    private View mView;
    public TextView selectLibrary, selectCamera, selectCancel;

    public UsePicturePopupWindow(Activity context, View.OnClickListener itemsOnClick) {
        super(context);


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.select_picture_layout, null);

        selectLibrary = (TextView) mView.findViewById(R.id.user_select_library);
        selectCamera = (TextView) mView.findViewById(R.id.user_select_camera);
        selectCancel = (TextView) mView.findViewById(R.id.user_select_cancel);


        // 设置按钮监听
        selectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        selectLibrary.setOnClickListener(itemsOnClick);
        selectCamera.setOnClickListener(itemsOnClick);


        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.Animation_profile_popupwindow);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }
}
