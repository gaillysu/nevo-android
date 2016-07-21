package com.medcorp.nevo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/21.
 */
public class AnalysisStepsChartAdapter extends FragmentPagerAdapter{

    private Context context;
    private ArrayList<Fragment> list;

    public AnalysisStepsChartAdapter(FragmentManager fm , ArrayList<Fragment> fragmentList) {
        super(fm);
        this.list = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
