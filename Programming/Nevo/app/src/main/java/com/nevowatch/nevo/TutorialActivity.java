package com.nevowatch.nevo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.nevowatch.nevo.Fragment.TutorialFragment;
import com.nevowatch.nevo.Function.SaveData;

/**
 * TutorialActivity is a container, which is filled with tutorial fragments. Additionally, users can swipe left and right to change pages.
 */
public class TutorialActivity extends FragmentActivity
        implements TutorialFragment.TutorialFragmentCallbcaks,ViewPager.OnPageChangeListener{

    private ViewPager mViewPager;
    private TutorialViewPagerAdapter mPagerAdapter;

    private static final int[] mDrawable = new int[]{
            R.drawable.icon_alarm_selected,
            R.drawable.icon_goal_selected,
            R.drawable.icon_home_selected
    };

    public static Fragment newInstance(int position){
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt("drawableID", mDrawable[position]);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SaveData.getFirstLaunchFromPreference(getApplicationContext())){
            startMainActivity();
        }else{
            setContentView(R.layout.tutorial_activity);
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new TutorialViewPagerAdapter(getSupportFragmentManager(), mDrawable);
            mViewPager.setAdapter(mPagerAdapter);
        }
    }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

    /**
     * Transfer from TutorialActivity to MainActivity
     * */
    @Override
    public void startMainActivity() {
        Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
        this.startActivity(intent);
        finish();
        SaveData.saveFirstLaunchtToPreference(getApplicationContext(), true);
    }
}
