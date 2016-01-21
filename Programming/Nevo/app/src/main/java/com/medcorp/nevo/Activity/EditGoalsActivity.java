package com.medcorp.nevo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.adapter.AlarmEditAdapter;
import com.medcorp.nevo.adapter.PresetEditAdapter;
import com.medcorp.nevo.model.Preset;
import com.medcorp.nevo.view.ToastHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by gaillysu on 15/12/23.
 */
public class EditGoalsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_goals_list_view)
    ListView presetListView;

    private Preset  preset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //TODO put in Strings.xml
        setTitle("Edit Goals");
        Bundle bundle = getIntent().getExtras();
        //TODO put in Keys.xml
        preset = getModel().getPresetById(bundle.getInt("Preset_ID"));
        presetListView.setVisibility(View.VISIBLE);
        presetListView.setOnItemClickListener(this);
        presetListView.setAdapter(new PresetEditAdapter(this,preset));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0)
        {
            //TODO put in Strings.xml
            new MaterialDialog.Builder(EditGoalsActivity.this)
                    .title("Edit Goal")
                    .content("input your goal.")
                    .inputType(InputType.TYPE_CLASS_NUMBER)
                    .input("", ""+preset.getSteps(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if(input.length()==0)return;
                            preset.setSteps(Integer.parseInt(input.toString()));
                            presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this, preset));
                        }
                    }).negativeText("Cancel").show();
        }
        else if(position == 1)
        {
            //TODO put in Strings.xml
            new MaterialDialog.Builder(EditGoalsActivity.this)
                    .title("Edit Goal")
                    .content("Label your goal.")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("label name", preset.getLabel(), new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            if(input.length()==0) return;
                            preset.setLabel(input.toString());
                            presetListView.setAdapter(new PresetEditAdapter(EditGoalsActivity.this, preset));
                        }
                    }).negativeText("Cancel")
                    .show();
        }
        else if(position == 2)
        {
            if(!getModel().deleteAlarm(preset)){
                //TODO put in Strings.xml
                ToastHelper.showShortToast(this, "Failed to delete goal");
            }else{
                //TODO put in Strings.xml
                ToastHelper.showShortToast(this, "Deleted goal!");
            }
            setResult(-1);
            finish();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_menu:
                if(getModel().updatePreset(preset)){
                    //TODO put in Strings.xml
                    ToastHelper.showShortToast(EditGoalsActivity.this, "Saved Goal!");
                    EditGoalsActivity.this.setResult(1);
                    EditGoalsActivity.this.finish();
                }else{
                    //TODO put in Strings.xml
                    ToastHelper.showShortToast(EditGoalsActivity.this,"Couldn't save the goal.!");
                }
                return true;
            case android.R.id.home:
                EditGoalsActivity.this.setResult(0);
                EditGoalsActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
