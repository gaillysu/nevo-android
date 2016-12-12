package com.medcorp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.medcorp.R;
import com.medcorp.adapter.EditLunarNotificationAdapter;
import com.medcorp.application.ApplicationModel;
import com.medcorp.base.BaseActivity;
import com.medcorp.ble.model.color.LedLamp;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jason on 2016/12/8.
 */

public class EditNotificationLampActivity extends BaseActivity {

    @Bind(R.id.all_lunar_notification_lamp_list)
    SwipeMenuRecyclerView allNotificationLampList;

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    private EditLunarNotificationAdapter mEditItemAdapter;
    private List<LedLamp> userSettingAllLamp;
    private String newNtName;
    private ApplicationModel mModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_lunar_notification_lamp_activity);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.edit_notification_item_name);
        mModel = getModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        userSettingAllLamp = mModel.getAllLedLamp();
        allNotificationLampList.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        allNotificationLampList.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        allNotificationLampList.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        allNotificationLampList.setSwipeMenuCreator(swipeMenuCreator);
        allNotificationLampList.setSwipeMenuItemClickListener(menuItemClickListener);

        mEditItemAdapter = new EditLunarNotificationAdapter(userSettingAllLamp);
        allNotificationLampList.setAdapter(mEditItemAdapter);
        mEditItemAdapter.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.add_menu).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_menu:
                addNewNotification();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addNewNotification() {
        new MaterialDialog.Builder(this).title(getString(R.string.add_new_notification_dialog_title))
                .content(getString(R.string.add_new_notification_dialog_hint_content))
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input.length() == 0) {
                            newNtName = getString(R.string.new_nt_def_name);
                        } else {
                            newNtName = input.toString();
                        }
                        openChooseColor();
                    }
                }).negativeText(R.string.goal_cancel).show();
    }

    private void openChooseColor() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle(getString(R.string.new_nt_select_color))
                .initialColor(R.color.window_background_color)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(15)
                .setPositiveButton(getString(R.string.goal_ok), new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        LedLamp ledLamp = new LedLamp();
                        ledLamp.setColor(selectedColor);
                        ledLamp.setName(newNtName);
                        ledLamp.setSelect(false);
                        mModel.addLedLamp(ledLamp);//添加到数据库
                        userSettingAllLamp.add(ledLamp);
                        mEditItemAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(getString(R.string.cancel_update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }


    /**
     * item's click event save data
     */
    private EditLunarNotificationAdapter.OnItemClickListener onItemClickListener = new EditLunarNotificationAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            for (int i = 0; i < userSettingAllLamp.size(); i++) {
                LedLamp ledLamp = userSettingAllLamp.get(i);
                if (i == position) {
                    ledLamp.setSelect(true);
                } else {
                    ledLamp.setSelect(false);
                }
                mModel.upDataLedLamp(ledLamp);
            }
            mEditItemAdapter.notifyDataSetChanged();
        }
    };


    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            int width = getResources().getDimensionPixelSize(R.dimen.edit_notification_item_width);
            SwipeMenuItem deleteItem = new SwipeMenuItem(EditNotificationLampActivity.this)
                    .setBackgroundDrawable(R.drawable.selector_red)
                    .setText(getString(R.string.edit_notification_delete_item))
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);

            SwipeMenuItem closeItem = new SwipeMenuItem(EditNotificationLampActivity.this)
                    .setBackgroundDrawable(R.drawable.selector_purple)
                    .setText(getString(R.string.edit_notification_item_name))
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem);
        }
    };


    /**
     * menu's event
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。
            if (menuPosition == 0) {// 删除按钮被点击。
                LedLamp ledLamp = userSettingAllLamp.get(adapterPosition);
                mModel.removeLedLamp(ledLamp.getId());
                userSettingAllLamp.remove(adapterPosition);
                mEditItemAdapter.notifyItemRemoved(adapterPosition);
            }
            if (menuPosition == 1) {
                LedLamp ledLamp = userSettingAllLamp.get(adapterPosition);
                Intent intent = new Intent(EditNotificationLampActivity.this, EditNotificationAttributeActivity.class);
                intent.putExtra("id", ledLamp.getId());
                startActivity(intent);
            }
        }
    };
}