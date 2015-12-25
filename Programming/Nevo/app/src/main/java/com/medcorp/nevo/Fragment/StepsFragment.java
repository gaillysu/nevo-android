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
import com.medcorp.nevo.adapter.StepsFragmentPagerAdapter;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.fragment.listener.OnStateListener;
import com.medcorp.nevo.fragment.listener.OnStepsListener;
import com.medcorp.nevo.fragment.base.BaseFragment;
import com.medcorp.nevo.model.Battery;

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
    }
}
