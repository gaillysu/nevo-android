package com.medcorp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medcorp.R;
import com.medcorp.fragment.MainClockFragment;
import com.medcorp.fragment.MainFragment;
import com.medcorp.fragment.MainSleepFragment;
import com.medcorp.fragment.MainSolarFragment;
import com.medcorp.fragment.MainStepsFragment;

/**
 * Created by Administrator on 2016/7/19.
 */
public class LunarMainFragmentAdapter extends FragmentPagerAdapter {

    private MainFragment mainFragment;
    private Context context;
    private String[] fragmentAdapterArray;

    public LunarMainFragmentAdapter(FragmentManager fm, MainFragment fragment) {
        super(fm);
        this.mainFragment = fragment;
        context = fragment.getContext();
        fragmentAdapterArray = context.getResources().getStringArray(R.array.lunar_main_adapter_fragment);
        if(mainFragment.getModel().getSyncController().getWatchInfomation().getWatchID()==1) {
            fragmentAdapterArray = context.getResources().getStringArray(R.array.nevo_main_adapter_fragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                MainClockFragment mainClockFragment = (MainClockFragment) MainClockFragment.instantiate(context,MainClockFragment.class.getName());
                return mainClockFragment;
            case 1:
                MainStepsFragment mainStepsFragment = (MainStepsFragment) com.medcorp.fragment.MainStepsFragment.instantiate(context,MainStepsFragment.class.getName());
                return mainStepsFragment;
            case 2:
                MainSleepFragment mainSleepFragment= (MainSleepFragment) MainSleepFragment.instantiate(context,MainSleepFragment.class.getName());
                return mainSleepFragment;
            case 3:
                MainSolarFragment solarFragment = (MainSolarFragment) MainSolarFragment.instantiate(context , MainSolarFragment.class.getName());
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
