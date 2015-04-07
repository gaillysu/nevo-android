package com.nevowatch.nevo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * TutorialViewPagerAdapter supplies ViewPager with custom tutorial fragments.
 */
public class TutorialViewPagerAdapter extends FragmentPagerAdapter {

    private int[] mDrawable;

    public TutorialViewPagerAdapter(FragmentManager fm, int[] drawable){
        super(fm);
        this.mDrawable = drawable;
    }

    @Override
    public Fragment getItem(int position) {

        Log.d("Adapter", "Position:"+position);
        return TutorialActivity.newInstance(position);
    }

    @Override
    public int getCount() {
        return mDrawable.length;
    }
}
