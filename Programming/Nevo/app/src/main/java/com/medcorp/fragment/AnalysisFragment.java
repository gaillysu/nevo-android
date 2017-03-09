package com.medcorp.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.R;
import com.medcorp.adapter.AnalysisFragmentPagerAdapter;
import com.medcorp.event.bluetooth.OnSyncEvent;
import com.medcorp.fragment.base.BaseObservableFragment;

import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/14.
 */
public class AnalysisFragment extends BaseObservableFragment {

    @Bind(R.id.analysis_fragment_indicator_tab)
    TabLayout analysisTable;
    @Bind(R.id.analysis_fragment_content_view_pager)
    ViewPager analysisViewpager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.analysis_fragment_layout,container,false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        AnalysisFragmentPagerAdapter adapter = new AnalysisFragmentPagerAdapter(getChildFragmentManager(),this);
        analysisViewpager.setAdapter(adapter);
        analysisTable.setupWithViewPager(analysisViewpager);
        return view;
    }

    @Subscribe
    public void onEvent(OnSyncEvent event) {
        if (event.getStatus() == OnSyncEvent.SYNC_EVENT.STOPPED) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    int currentItem = analysisViewpager.getCurrentItem();
                    AnalysisFragmentPagerAdapter adapter = new AnalysisFragmentPagerAdapter(getChildFragmentManager(), AnalysisFragment.this);
                    analysisViewpager.setAdapter(adapter);
                    analysisTable.setupWithViewPager(analysisViewpager);
                    analysisViewpager.setCurrentItem(currentItem);
                }
            });
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
        menu.findItem(R.id.add_menu).setVisible(false);
    }
}



