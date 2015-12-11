package com.medcorp.nevo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medcorp.nevo.R;
import com.medcorp.nevo.adapter.SleepFragmentPagerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Karl on 12/10/15.
 */
public class SleepFragment extends Fragment{

    @Bind(R.id.fragment_sleep_view_pager)
    ViewPager viewPager;

    @Bind(R.id.fragment_sleep_tab_layout)
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);
        ButterKnife.bind(this,view);
        viewPager.setAdapter(new SleepFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                getActivity()));
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }
}
