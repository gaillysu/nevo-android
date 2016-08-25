package com.medcorp.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.R;
import com.medcorp.activity.MainActivity;
import com.medcorp.adapter.LunarMainFragmentAdapter;
import com.medcorp.event.bluetooth.RequestResponseEvent;
import com.medcorp.fragment.base.BaseObservableFragment;
import com.medcorp.model.Goal;
import com.medcorp.util.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/19.
 */
public class LunarMainFragment extends BaseObservableFragment {


    @Bind(R.id.fragment_lunar_main_view_pager)
    ViewPager showWatchViewPage;
    private boolean showSyncGoal;
    private LunarMainFragmentAdapter adapter;

    private int stepsGoalNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lunar_main_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        adapter = new LunarMainFragmentAdapter(getChildFragmentManager(), this);
        showWatchViewPage.setAdapter(adapter);

        return view;
    }

    @OnClick(R.id.lunar_main_fragment_left_arrow)
    public void leftClick() {
        showWatchViewPage.setCurrentItem(showWatchViewPage.getCurrentItem() - 1);
    }

    @OnClick(R.id.lunar_main_fragment_right_arrow)
    public void rightClick() {
        showWatchViewPage.setCurrentItem(showWatchViewPage.getCurrentItem() + 1);
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

        if(goalList.size() != 0) {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.goal)
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
        }else{
            ejectStepsGoalDialog();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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


    private void ejectStepsGoalDialog() {

        final Dialog dialog = new AlertDialog.Builder(getContext()).create();
        dialog.show();
        Window window = dialog.getWindow();
        View stepsGoalView = View.inflate(getContext(), R.layout.steps_fragment_dialog_layout, null);
        window.setContentView(stepsGoalView);

        final RadioGroup stepsGoalGroup = (RadioGroup) stepsGoalView.findViewById(R.id.steps_fragment_dialog_group);
        stepsGoalView.findViewById(R.id.steps_fragment_cancel_steps_setting_button).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

//        stepsGoalView.findViewById(R.id.steps_fragment_setting_steps_goal_ok_button).
//                setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        stepsGoalGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                                switch (checkedId) {
//                                    case R.id.radio_button_one:
//                                        stepsGoalNumber = stepsGoalArray[0];
//                                        break;
//                                    case R.id.radio_button_two:
//                                        stepsGoalNumber = stepsGoalArray[1];
//                                        break;
//                                    case R.id.radio_button_three:
//                                        stepsGoalNumber = stepsGoalArray[2];
//                                        break;
//                                    case R.id.radio_button_four:
//                                        stepsGoalNumber = stepsGoalArray[3];
//                                        break;
//                                }
//                            }
//
//                        });
//                        upDataUserStepsGoal(stepsGoalNumber);
//                    }
//                });
    }

//    public void upDataUserStepsGoal(final int stepsGoal) {
//        new MaterialDialog.Builder(getContext())
//                .title(R.string.goal_add)
//                .content(R.string.goal_label_goal)
//                .inputType(InputType.TYPE_CLASS_TEXT)
//                .input(getString(R.string.goal_name_goal), "",
//                        new MaterialDialog.InputCallback() {
//                            @Override
//                            public void onInput(MaterialDialog dialog, CharSequence input) {
//                                if (input.length() == 0)
//                                    return;
//                                Goal goal = new Goal(input.toString(), false, stepsGoal);
//                                getModel().addGoal(goal);
//                            }
//                        }).negativeText(R.string.goal_cancel)
//                .show();
//
//    }
}

