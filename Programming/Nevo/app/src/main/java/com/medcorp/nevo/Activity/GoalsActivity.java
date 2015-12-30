package com.medcorp.nevo.activity;

import android.content.Intent;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;

import com.medcorp.nevo.adapter.PresetArrayAdapter;
import com.medcorp.nevo.model.Preset;
import com.medcorp.nevo.view.StepPickerView;

import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * Created by gaillysu on 15/12/23.
 */
public class GoalsActivity extends BaseActivity  implements AdapterView.OnItemClickListener{

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_goals_list_view)
    ListView presetListView;

    List<Preset> presetList;
    PresetArrayAdapter presetArrayAdapter;
    Preset preset ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Goals");
        presetListView.setVisibility(View.VISIBLE);
        presetList = getModel().getAllPreset();
        presetArrayAdapter = new PresetArrayAdapter(this,getModel(),presetList);
        presetListView.setAdapter(presetArrayAdapter);
        presetListView.setOnItemClickListener(this);
        preset = new Preset("",false,7000);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,EditGoalsActivity.class);
        preset =  presetList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("Preset_ID", preset.getId());
        bundle.putString("Preset_Label", preset.getLabel());
        bundle.putInt("Preset_Steps", preset.getSteps());
        intent.putExtras(bundle);
        startActivityForResult(intent,0);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //delete or update the goal, refresh list
        if(resultCode!=0)
        {
            presetList = getModel().getAllPreset();
            presetArrayAdapter.setDataset(presetList);
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
                new MaterialDialog.Builder(GoalsActivity.this)
                        .title("Add Goal")
                        .content("Input your goal.")
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .input("step goal", "7000", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if(input.length()==0) return;
                                preset.setSteps(Integer.parseInt(input.toString()));
                                new MaterialDialog.Builder(GoalsActivity.this)
                                        .title("Add Goal")
                                        .content("Label your goal.")
                                        .inputType(InputType.TYPE_CLASS_TEXT)
                                        .input("goal name", "", new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                                if(input.length()==0) return;
                                                preset.setLabel(input.toString());
                                                //save to database and refresh listview
                                                getModel().addPreset(preset);
                                                presetList = getModel().getAllPreset();
                                                presetArrayAdapter.setDataset(presetList);
                                                presetArrayAdapter.notifyDataSetChanged();
                                            }
                                        }).negativeText("Cancel")
                                        .show();
                            }
                        }).negativeText("Cancel")
                        .show();
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
