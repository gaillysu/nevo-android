package com.medcorp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.R;
import com.medcorp.activity.MainActivity;
import com.medcorp.adapter.LunarMainFragmentAdapter;
import com.medcorp.event.bluetooth.GetWatchInfoChangedEvent;
import com.medcorp.event.bluetooth.RequestResponseEvent;
import com.medcorp.fragment.base.BaseObservableFragment;
import com.medcorp.model.Goal;
import com.medcorp.util.Common;
import com.medcorp.util.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/19.
 */
public class MainFragment extends BaseObservableFragment {


    @Bind(R.id.fragment_lunar_main_view_pager)
    ViewPager showWatchViewPage;
    @Bind(R.id.ui_page_control_point)
    LinearLayout uiPageControl;
    private boolean showSyncGoal;
    private LunarMainFragmentAdapter adapter;
    private String[] fragmentAdapterArray;
    private Date userSelectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lunar_main_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        String selectDate = Preferences.getSelectDate(this.getContext());
        if (selectDate == null) {
            userSelectDate = new Date();
        } else {
            try {
                userSelectDate = new SimpleDateFormat("yyyy-MM-dd").parse(selectDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        setHasOptionsMenu(true);
        initUiControl();
        adapter = new LunarMainFragmentAdapter(getChildFragmentManager(), this);
        showWatchViewPage.setAdapter(adapter);
        return view;
    }

    private void initUiControl() {
        if (getModel().getSyncController().getWatchInfomation().getWatchID() == 1) {
            fragmentAdapterArray = getResources().getStringArray(R.array.nevo_main_adapter_fragment);
        } else {
            fragmentAdapterArray = getResources().getStringArray(R.array.lunar_main_adapter_fragment);

        }
        uiPageControl.removeAllViews();
        for (int i = 0; i < fragmentAdapterArray.length; i++) {
            ImageView imageView = new ImageView(MainFragment.this.getContext());
            if (i == 0) {
                imageView.setImageResource(R.drawable.ui_page_control_selector);
            } else {
                imageView.setImageResource(R.drawable.ui_page_control_unselector);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i != 0) {
                params.leftMargin = 20;
            }
            uiPageControl.addView(imageView, params);
        }

        showWatchViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int childCount = uiPageControl.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    ImageView im = (ImageView) uiPageControl.getChildAt(i);
                    if(position == i){
                        im.setImageResource(R.drawable.ui_page_control_selector);
                    }else{
                        im.setImageResource(R.drawable.ui_page_control_unselector);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_goal_menu:
                popupStepsGoalDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void popupStepsGoalDialog() {
        if (!getModel().isWatchConnected()) {
            ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_no_watch, false);
            return;
        }
        final List<Goal> goalList = getModel().getAllGoal();
        List<String> stringList = new ArrayList<>();
        final List<Goal> goalEnableList = new ArrayList<>();

        for (Goal goal : goalList) {
            if (goal.isStatus()) {
                stringList.add(goal.toString());
                goalEnableList.add(goal);
            }
        }
        CharSequence[] cs = stringList.toArray(new CharSequence[stringList.size()]);

        if (goalList.size() != 0) {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.goal).itemsColor(getResources().getColor(R.color.edit_alarm_item_text_color))
                    .items(cs)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            if (which >= 0) {
                                getModel().setGoal(goalEnableList.get(which));
                                Preferences.savePreset(getContext(), goalEnableList.get(which));
                                showSyncGoal = true;
                                ((MainActivity) getActivity()).showStateString(R.string.goal_syncing_message, false);
                            }
                            return true;
                        }
                    })
                    .positiveText(R.string.goal_ok)
                    .negativeText(R.string.goal_cancel).contentColorRes(R.color.left_menu_item_text_color)
                    .show();
        } else {
            ((MainActivity) getActivity()).showStateString(R.string.in_app_notification_no_goal, false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        //NOTICE: if do full big sync, that will consume more battery power and more time (MAX 7 days data),so only big sync today's data
        if (Common.removeTimeFromDate(new Date()).getTime() == Common.removeTimeFromDate(userSelectDate).getTime()) {
            getModel().getSyncController().getDailyTrackerInfo(false);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(RequestResponseEvent event) {
        //if this response comes from syncController init, ignore it, only for user set a new goal.
        if (showSyncGoal) {
            showSyncGoal = false;
            int id = event.isSuccess() ? R.string.goal_synced : R.string.goal_error_sync;
            ((MainActivity) getActivity()).showStateString(id, false);
        }
    }
    @Subscribe
    public void onEvent(GetWatchInfoChangedEvent event) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                initUiControl();
                adapter = new LunarMainFragmentAdapter(getChildFragmentManager(), MainFragment.this);
                showWatchViewPage.setAdapter(adapter);
            }
        });
    }
}

