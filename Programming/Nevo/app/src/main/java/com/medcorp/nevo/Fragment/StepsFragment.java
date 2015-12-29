package com.medcorp.nevo.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.adapter.StepsFragmentPagerAdapter;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.fragment.listener.OnStateListener;
import com.medcorp.nevo.fragment.listener.OnStepsListener;
import com.medcorp.nevo.fragment.base.BaseFragment;
import com.medcorp.nevo.model.Alarm;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.model.Preset;
import com.medcorp.nevo.util.Preferences;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Karl on 12/10/15.
 */
public class StepsFragment extends BaseObservableFragment{

    @Bind(R.id.fragment_steps_view_pager)
    ViewPager viewPager;

    @Bind(R.id.fragment_steps_tab_layout)
    TabLayout tabLayout;

    private OnStepsListener onStepsListener;
    private OnStateListener onStateListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_steps, container, false);
        ButterKnife.bind(this, view);
        StepsFragmentPagerAdapter adapter = new StepsFragmentPagerAdapter(getChildFragmentManager(),this);
        setHasOptionsMenu(true);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    public void setOnStepsListener(OnStepsListener onStepsListener) {
        this.onStepsListener = onStepsListener;
    }
    public void setOnStateListener(OnStateListener onStateListener) {
        this.onStateListener = onStateListener;
    }

    @Override
    public void notifyDatasetChanged() {
        if(onStepsListener != null) {
            onStepsListener.OnStepsChanged();
        }
    }

    @Override
    public void notifyOnConnected() {
        if(onStateListener !=null)
        {
            onStateListener.onStateChanged(OnStateListener.STATE.STATE_CONNECTED);
        }
    }

    @Override
    public void notifyOnDisconnected() {
        if(onStateListener !=null)
        {
            onStateListener.onStateChanged(OnStateListener.STATE.STATE_DISCONNECT);
        }
    }

    @Override
    public void batteryInfoReceived(Battery battery) {

    }

    @Override
    public void findWatchSuccess() {

    }

    @Override
    public void onSearching() {
        if(onStateListener !=null)
        {
            onStateListener.onStateChanged(OnStateListener.STATE.STATE_SEARCHING);
        }
    }

    @Override
    public void onSearchSuccess() {
        if(onStateListener !=null)
        {
            onStateListener.onStateChanged(OnStateListener.STATE.STATE_SEARCH_SUCCESS);
        }
    }

    @Override
    public void onSearchFailure() {
        if(onStateListener !=null)
        {
            onStateListener.onStateChanged(OnStateListener.STATE.STATE_SEARCH_FAILURE);
        }
    }

    @Override
    public void onConnecting() {
        if(onStateListener !=null)
        {
            onStateListener.onStateChanged(OnStateListener.STATE.STATE_CONNECTING);
        }
    }

    @Override
    public void onSyncStart() {
        if(onStateListener !=null)
        {
            onStateListener.onStateChanged(OnStateListener.STATE.STATE_SYNC_START);
        }
    }

    @Override
    public void onSyncEnd() {
        if(onStateListener !=null)
        {
            onStateListener.onStateChanged(OnStateListener.STATE.STATE_SYNC_END);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(true);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.choose_goal_menu:
                final List<Preset> presetList = getModel().getAllPreset();
                List<String> stringList = new ArrayList<>();

                for (Preset preset : presetList){
                    if(preset.isStatus())
                    stringList.add(preset.toString());
                }
                CharSequence[] cs = stringList.toArray(new CharSequence[stringList.size()]);

                new MaterialDialog.Builder(getContext())
                        .title("Goal")
                        .items(cs)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                //TODO sync the chosen steps with watch, please do Gailly.
                                Preferences.savePreset(getContext(),presetList.get(which));
                                return true;
                            }
                        })
                        .positiveText("Ok")
                        .negativeText("Cancel")
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // TODO: on switch selected check if 3 alarms are already on, if so, show dialog/toast if not sync with the watch

}
