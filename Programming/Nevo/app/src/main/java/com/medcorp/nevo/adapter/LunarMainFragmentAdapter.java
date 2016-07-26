package com.medcorp.nevo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.ChartFragment;
import com.medcorp.nevo.fragment.ClockFragment;
import com.medcorp.nevo.fragment.LunarMainFragment;
import com.medcorp.nevo.fragment.LunarMainSleepFragment;

/**
 * Created by Administrator on 2016/7/19.
 */
public class LunarMainFragmentAdapter extends FragmentPagerAdapter {

    private LunarMainFragment clockFragment;
    private Context context;
    private String[] fragmentAdapterArray;
    public LunarMainFragmentAdapter(FragmentManager fm, LunarMainFragment fragment) {
        super(fm);
        this.clockFragment = fragment;
        context = fragment.getContext();
        fragmentAdapterArray = context.getResources().getStringArray(R.array.lunar_main_adapter_fragment);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                ClockFragment clockFragment = (ClockFragment) ClockFragment.instantiate(context,ClockFragment.class.getName());
                return clockFragment;
            case 1:
                ChartFragment chartFragment = (ChartFragment) ChartFragment.instantiate(context,ChartFragment.class.getName());
                return chartFragment;
            case 2:
                LunarMainSleepFragment mainSleepFragment= (LunarMainSleepFragment) LunarMainSleepFragment.instantiate(context,LunarMainSleepFragment.class.getName());
                return mainSleepFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return fragmentAdapterArray.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentAdapterArray[position];
    }
}
