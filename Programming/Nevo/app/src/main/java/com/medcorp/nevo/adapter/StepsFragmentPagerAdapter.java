package com.medcorp.nevo.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.medcorp.nevo.R;
import com.medcorp.nevo.fragment.StepsHistoryFragment;
import com.medcorp.nevo.fragment.StepsTodayFragment;

/**
 * Created by Karl on 12/10/15.
 */
public class StepsFragmentPagerAdapter extends FragmentPagerAdapter{

    private Context context;
    private String[] stepsTitleArray;

    public StepsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        stepsTitleArray = context.getResources().getStringArray(R.array.steps_tab);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return StepsTodayFragment.instantiate(context,StepsTodayFragment.class.getName());
            case 1:
                return StepsHistoryFragment.instantiate(context,StepsHistoryFragment.class.getName());
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return stepsTitleArray.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return stepsTitleArray[position];
    }
}


