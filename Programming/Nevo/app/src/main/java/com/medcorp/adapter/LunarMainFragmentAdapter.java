package com.medcorp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medcorp.R;
import com.medcorp.fragment.ClockFragment;
import com.medcorp.fragment.LunarMainFragment;
import com.medcorp.fragment.LunarMainSleepFragment;
import com.medcorp.fragment.LunarMainStepsFragment;
import com.medcorp.fragment.LunarMainSolarFragment;

/**
 * Created by Administrator on 2016/7/19.
 */
public class LunarMainFragmentAdapter extends FragmentPagerAdapter {

    private LunarMainFragment mainFragment;
    private Context context;
    private String[] fragmentAdapterArray;

    public LunarMainFragmentAdapter(FragmentManager fm, LunarMainFragment fragment) {
        super(fm);
        this.mainFragment = fragment;
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
                LunarMainStepsFragment lunarMainStepsFragment = (LunarMainStepsFragment) LunarMainStepsFragment.instantiate(context,LunarMainStepsFragment.class.getName());
                return lunarMainStepsFragment;
            case 2:
                LunarMainSleepFragment mainSleepFragment= (LunarMainSleepFragment) LunarMainSleepFragment.instantiate(context,LunarMainSleepFragment.class.getName());
                return mainSleepFragment;
            case 3:
                LunarMainSolarFragment solarFragment = (LunarMainSolarFragment) LunarMainSolarFragment.instantiate(context , LunarMainSolarFragment.class.getName());
                return solarFragment;
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
