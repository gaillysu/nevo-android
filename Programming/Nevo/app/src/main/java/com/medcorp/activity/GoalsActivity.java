package com.medcorp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.ApplicationFlag;
import com.medcorp.base.BaseActivity;
import com.medcorp.adapter.PresetArrayAdapter;
import com.medcorp.R;
import com.medcorp.model.Goal;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/23.
 */
public class GoalsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_goals_list_view)
    ListView presetListView;

    List<Goal> goalList;
    PresetArrayAdapter presetArrayAdapter;
    Goal goal;

    private boolean showSyncGoal;
    private int[] stepsGoalArray = {4500, 6000, 8000, 10000};
    private int stepsGoalNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }
        setContentView(R.layout.activity_goals);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.steps_goal_title);
        presetListView.setVisibility(View.VISIBLE);
        goalList = getModel().getAllGoal();
        presetArrayAdapter = new PresetArrayAdapter(this, getModel(), goalList);
        presetListView.setAdapter(presetArrayAdapter);
        presetListView.setOnItemClickListener(this);
        goal = new Goal("", false, 7000);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, EditGoalsActivity.class);
        goal = goalList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.key_preset_id), goal.getId());
        bundle.putString(getString(R.string.key_preset_label), goal.getLabel());
        bundle.putInt(getString(R.string.key_preset_steps), goal.getSteps());
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //delete or update the goal, refresh list
        if (resultCode != 0) {
            goalList = getModel().getAllGoal();
            presetArrayAdapter.setDataset(goalList);
            presetArrayAdapter.notifyDataSetChanged();
        }
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
                if (ApplicationFlag.FLAG == ApplicationFlag.Flag.LUNAR) {
                    ejectStepsGoalDialog();
                } else {

                    new MaterialDialog.Builder(GoalsActivity.this)
                            .title(R.string.goal_add)
                            .content(R.string.goal_input)
                            .inputType(InputType.TYPE_CLASS_NUMBER)
                            .input(getString(R.string.goal_step_goal), "",
                                    new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                            if (input.length() == 0)
                                                return;

                                            goal.setSteps(Integer.parseInt(input.toString()));
                                            new MaterialDialog.Builder(GoalsActivity.this)
                                                    .title(R.string.goal_add)
                                                    .content(R.string.goal_label_goal)
                                                    .inputType(InputType.TYPE_CLASS_TEXT)
                                                    .input(getString(R.string.goal_name_goal), "",
                                                            new MaterialDialog.InputCallback() {
                                                                @Override
                                                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                                                    if (input.length() == 0)
                                                                        return;
                                                                    goal.setLabel(input.toString());
                                                                    getModel().addGoal(goal);
                                                                    goalList = getModel().getAllGoal();
                                                                    presetArrayAdapter.setDataset(goalList);
                                                                    presetArrayAdapter.notifyDataSetChanged();
                                                                }
                                                            }).negativeText(R.string.goal_cancel)
                                                    .show();
                                        }
                                    }).negativeText(R.string.goal_cancel)
                            .show();
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void ejectStepsGoalDialog() {
        final Dialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        Window window = dialog.getWindow();
        View stepsGoalView = View.inflate(this, R.layout.steps_fragment_dialog_layout, null);
        window.setContentView(stepsGoalView);

        final RadioGroup stepsGoalGroup = (RadioGroup) stepsGoalView.findViewById(R.id.steps_fragment_dialog_group);
        stepsGoalView.findViewById(R.id.steps_fragment_cancel_steps_setting_button).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        stepsGoalView.findViewById(R.id.steps_fragment_setting_steps_goal_ok_button).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        stepsGoalGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId) {
                                    case R.id.radio_button_one:
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
                            }

                        });
                        upDataUserStepsGoal(stepsGoalNumber);
                    }
                });
    }

    public void upDataUserStepsGoal(int stepsGoal) {
        goal.setSteps(stepsGoal);
        new MaterialDialog.Builder(GoalsActivity.this)
                .title(R.string.goal_add)
                .content(R.string.goal_label_goal)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(getString(R.string.goal_name_goal), "",
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.length() == 0)
                                    return;
                                goal.setLabel(input.toString());
                                getModel().addGoal(goal);
                                goalList = getModel().getAllGoal();
                                presetArrayAdapter.setDataset(goalList);
                                presetArrayAdapter.notifyDataSetChanged();
                            }
                        }).negativeText(R.string.goal_cancel)
                .show();

    }

}
