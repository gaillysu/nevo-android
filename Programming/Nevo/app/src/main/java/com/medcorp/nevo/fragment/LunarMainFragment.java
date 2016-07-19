package com.medcorp.nevo.fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.adapter.LunarMainFragmentAdapter;
import com.medcorp.nevo.event.bluetooth.RequestResponseEvent;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.model.Steps;
import com.medcorp.nevo.model.User;

import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/19.
 */
public class LunarMainFragment extends BaseObservableFragment {


    @Bind(R.id.fragment_lunar_main_view_pager)
    ViewPager showWatchViewPage;
    @Bind(R.id.lunar_fragment_show_user_consume_calories)
    TextView showUserCosumeCalories;
    @Bind(R.id.lunar_fragment_show_user_steps_distance_tv)
    TextView showUserStepsDistance;
    @Bind(R.id.lunar_fragment_show_user_activity_time_tv)
    TextView showUserActivityTime;
    @Bind(R.id.lunar_fragment_show_user_steps_tv)
    TextView showUserSteps;

    private int[] stepsGoalArray = {4500, 6000, 8000, 10000};
    private int stepsGoalNumber;
    private boolean showSyncGoal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lunar_main_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        initData(new Date());

        LunarMainFragmentAdapter adapter = new LunarMainFragmentAdapter(getChildFragmentManager(), this);
        showWatchViewPage.setAdapter(adapter);

        return view;
    }

    private void initData(Date date) {
        User user = getModel().getNevoUser();
        Steps steps = getModel().getDailySteps(user.getNevoUserID(), date);
        showUserActivityTime.setText(steps.getWalkDuration() != 0 ? formatTime(steps.getWalkDuration()) : 0 + "");
        showUserStepsDistance.setText(steps.getWalkDistance() != 0 ? steps.getWalkDistance() + "km" : 0 + "");
        showUserSteps.setText(steps.getSteps() + "");
        showUserCosumeCalories.setText(steps.getCalories() + "");
    }

    private String formatTime(int walkDuration) {
        StringBuffer activityTime = new StringBuffer();
        if (walkDuration >= 60) {
            if (walkDuration % 60 > 0) {
                activityTime.append(walkDuration % 60 + "h");
                activityTime.append(walkDuration - (walkDuration % 60 * 60) + "m");
            }
        } else {
            activityTime.append(walkDuration + "m");
        }

        return activityTime.toString();
    }

    @OnClick(R.id.lunar_main_fragment_left_arrow)
    public void leftClick() {
        showWatchViewPage.setCurrentItem(showWatchViewPage.getCurrentItem()-1);
    }

    @OnClick(R.id.lunar_main_fragment_right_arrow)
    public void rightClick() {
        showWatchViewPage.setCurrentItem(showWatchViewPage.getCurrentItem()+1);
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
                ejectStepsGoalDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void ejectStepsGoalDialog() {
        final Dialog dialog = new AlertDialog.Builder(this.getActivity()).create();
        dialog.show();
        Window window = dialog.getWindow();
        View stepsGoalView = View.inflate(this.getActivity(), R.layout.steps_fragment_dialog_layout, null);
        window.setContentView(stepsGoalView);

        final RadioGroup stepsGoalGroup = (RadioGroup) stepsGoalView.findViewById(R.id.steps_fragment_dialog_group);
        stepsGoalView.findViewById(R.id.steps_fragment_cancel_steps_setting_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        stepsGoalView.findViewById(R.id.steps_fragment_setting_steps_goal_ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                stepsGoalGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.radio_button_one:
                                Toast.makeText(LunarMainFragment.this.getActivity(), "dsabfljha", Toast.LENGTH_SHORT).show();
                                stepsGoalNumber = stepsGoalArray[0];
                                break;
                            case R.id.radio_button_two:
                                stepsGoalNumber = stepsGoalArray[1];
                                break;
                            case R.id.radio_button_three:
                                stepsGoalNumber = stepsGoalArray[2];
                                break;
                            case R.id.radio_button_four:
                                stepsGoalNumber = stepsGoalArray[3];
                                break;
                        }
                        upDataUserStepsGoal(stepsGoalNumber);
                    }
                });
            }
        });
    }

    public void upDataUserStepsGoal(int stepsGoal) {
        Steps steps = getModel().getDailySteps(getModel().getNevoUser().getNevoUserID(), new Date());
        steps.setGoal(stepsGoal);
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
}
