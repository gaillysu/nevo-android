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
import com.medcorp.nevo.fragment.base.BaseFragment;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.util.GoogleFitStepsDataHandler;

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
        dataHandler.getSteps();
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
        menu.findItem(R.id.add_menu).setVisible(false);
    }


    @Override
    public void notifyDatasetChanged() {

    }

    @Override
    public void notifyOnConnected() {

    }

    @Override
    public void notifyOnDisconnected() {

    }

    @Override
    public void batteryInfoReceived(Battery battery) {

    }

    @Override
    public void findWatchSuccess() {

    }

    @Override
    public void onSearching() {

    }

    @Override
    public void onSearchSuccess() {

    }

    @Override
    public void onSearchFailure() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncEnd() {
        //calculate sleep result, need waiting big sync done, so here catch it and redraw all fragments
        int currentItem = viewPager.getCurrentItem();
        viewPager.setAdapter(new SleepFragmentPagerAdapter(getChildFragmentManager(),
                getActivity()));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(currentItem);
    }

    @Override
    public void onRequestResponse(boolean success) {

    }
}
