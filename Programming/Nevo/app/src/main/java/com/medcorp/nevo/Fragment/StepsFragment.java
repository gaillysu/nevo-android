package com.medcorp.nevo.fragment;

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
import com.medcorp.nevo.activity.MainActivity;
import com.medcorp.nevo.adapter.StepsFragmentPagerAdapter;
import com.medcorp.nevo.event.OnSyncEvent;
import com.medcorp.nevo.event.RequestResponseEvent;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.model.Goal;
import com.medcorp.nevo.util.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    private boolean showSyncGoal;

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

    @Override
    public void onResume() {
        super.onResume();
        getAppCompatActivity().setTitle(R.string.title_steps);
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
                if(!getModel().isWatchConnected())
                {
                    ((MainActivity)getActivity()).showStateString(R.string.in_app_notification_no_watch,false);
                    return false;
                }
                final List<Goal> goalList = getModel().getAllGoal();
                List<String> stringList = new ArrayList<>();
                final List<Goal> goalEnableList = new ArrayList<Goal>();

                for (Goal goal : goalList){
                    if(goal.isStatus())
                    {
                        stringList.add(goal.toString());
                        goalEnableList.add(goal);
                    }
                }
                CharSequence[] cs = stringList.toArray(new CharSequence[stringList.size()]);

                new MaterialDialog.Builder(getContext())
                        .title(R.string.goal)
                        .items(cs)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if(which>=0)
                                {
                                    getModel().setGoal(goalEnableList.get(which));
                                    Preferences.savePreset(getContext(), goalEnableList.get(which));
                                    showSyncGoal = true;
                                    ((MainActivity)getActivity()).showStateString(R.string.goal_syncing_message, false);
                                }
                                return true;
                            }
                        })
                        .positiveText(R.string.goal_ok)
                        .negativeText(R.string.goal_cancel)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public void onEvent(OnSyncEvent event) {
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED) {
            int currentItem = viewPager.getCurrentItem();
            StepsFragmentPagerAdapter adapter = new StepsFragmentPagerAdapter(getChildFragmentManager(), this);
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(currentItem);
        }
    }

    @Subscribe
    public void onEvent(RequestResponseEvent event) {
        //if this response comes from syncController init, ignore it, only for user set a new goal.
        if(showSyncGoal)
        {
            showSyncGoal = false;
            int id = event.isSuccess() ? R.string.goal_synced : R.string.goal_error_sync;
            ((MainActivity) getActivity()).showStateString(id, false);
        }
    }

}
