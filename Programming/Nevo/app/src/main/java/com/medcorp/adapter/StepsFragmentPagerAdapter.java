package com.medcorp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medcorp.fragment.StepsHistoryFragment;
import com.medcorp.R;
import com.medcorp.fragment.StepsFragment;
import com.medcorp.fragment.StepsTodayFragment;

/**
 * Created by Karl on 12/10/15.
 */
public class StepsFragmentPagerAdapter extends FragmentPagerAdapter{

    private StepsFragment stepsFragment;
    private String[] todayHistoryArray;
    private Context context;

    public StepsFragmentPagerAdapter(FragmentManager fm, StepsFragment fragment) {
        super(fm);
        this.stepsFragment = fragment;
        context = fragment.getContext();
        todayHistoryArray = fragment.getResources().getStringArray(R.array.today_history_array);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                StepsTodayFragment stepsTodayFragment = (StepsTodayFragment) StepsTodayFragment.instantiate(context,StepsTodayFragment.class.getName());
                return stepsTodayFragment;
            case 1:
                return StepsHistoryFragment.instantiate(context, StepsHistoryFragment.class.getName());
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


