package com.medcorp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medcorp.fragment.SleepHistoryFragment;
import com.medcorp.fragment.SleepTodayFragment;
import com.medcorp.R;

/**
 * Created by Karl on 12/10/15.
 */
public class SleepFragmentPagerAdapter extends FragmentPagerAdapter{

    private Context context;
    private String[] todayHistoryArray;

    public SleepFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        todayHistoryArray = context.getResources().getStringArray(R.array.today_history_array);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return SleepTodayFragment.instantiate(context, SleepTodayFragment.class.getName());
            case 1:
                return SleepHistoryFragment.instantiate(context, SleepHistoryFragment.class.getName());
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return todayHistoryArray.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return todayHistoryArray[position];
    }
}