package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.nevo.R;
import com.medcorp.nevo.adapter.SleepFragmentPagerAdapter;
import com.medcorp.nevo.event.OnSyncEvent;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.googlefit.GoogleFitStepsDataHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Karl on 12/10/15.
 */
public class SleepFragment extends BaseObservableFragment{

    @Bind(R.id.fragment_sleep_view_pager)
    ViewPager viewPager;

    @Bind(R.id.fragment_sleep_tab_layout)
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);
        ButterKnife.bind(this, view);
        viewPager.setAdapter(new SleepFragmentPagerAdapter(getChildFragmentManager(),
                getActivity()));
        tabLayout.setupWithViewPager(viewPager);
        setHasOptionsMenu(true);
        GoogleFitStepsDataHandler dataHandler = new GoogleFitStepsDataHandler(getModel().getAllSteps(),getContext());
        dataHandler.getCaloriesDataSet();
        dataHandler.getDistanceDataSet();
        dataHandler.getStepsDataSet();
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
        menu.findItem(R.id.add_menu).setVisible(false);
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
    public void onEvent(OnSyncEvent event){
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED) {
            int currentItem = viewPager.getCurrentItem();
            viewPager.setAdapter(new SleepFragmentPagerAdapter(getChildFragmentManager(),
                    getActivity()));
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(currentItem);
        }
    }
}
