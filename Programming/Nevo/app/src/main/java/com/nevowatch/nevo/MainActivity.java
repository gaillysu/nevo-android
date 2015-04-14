package com.nevowatch.nevo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import com.nevowatch.nevo.Fragment.AlarmFragment;
import com.nevowatch.nevo.Fragment.ConnectAnimationFragment;
import com.nevowatch.nevo.Fragment.GoalFragment;
import com.nevowatch.nevo.Fragment.NavigationDrawerFragment;
import com.nevowatch.nevo.Fragment.WelcomeFragment;
import com.nevowatch.nevo.View.StepPickerView;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.model.packet.DailyStepsNevoPacket;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.model.request.GetStepsGoalNevoRequest;
import com.nevowatch.nevo.ble.util.Optional;

/**
 * MainActivity is a controller, which works for updating UI and connect Nevo Watch by bluetooth
 * */
public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,GoalFragment.GoalFragmentCallbacks,AlarmFragment.AlarmFragmentCallbacks,WelcomeFragment.WelcomeFragmentCallbacks,ConnectAnimationFragment.ConnectAnimationFragmentCallbacks,OnSyncControllerListener {
    private static int mPosition;
    private static String mTag;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //disenable navigation drawer shadow
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        MyApplication.getSyncController().startConnect(false, this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Optional<String> tag = new Optional<String>(null);
        switch (position+1){
            case 1:
                tag.set("WelcomeFragment");
                mPosition = 0;
                mTag = "WelcomeFragment";
                break;
            case 2:
                tag.set("GoalFragment");
                mPosition = 1;
                mTag = "GoalFragment";
                break;
            case 3:
                tag.set("AlarmFragment");
                mPosition = 2;
                mTag = "AlarmFragment";
                break;
            default:
                break;
        }

        if(MyApplication.getSyncController()!=null && !MyApplication.getSyncController().isConnected()){
            if((position+1) == 1){
                replaceFragment(position, tag.get());
            }else{
                replaceFragment(3, "ConnectAnimationFragment");
            }
        }else{
            Log.d("MainActivity", "Connect");
            replaceFragment(position, tag.get());
        }
    }

    /**
     * Replace fragment in the MainActivity
     * */
    @Override
    public void replaceFragment(final int position, final String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1), tag)
                .commit();
    }

    @Override
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                MyApplication.getSyncController().getStepsAndGoal();
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            default:
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public Fragment getFragment(String tag){
        if(tag.equals("AlarmFragment")){
            AlarmFragment alramfragment = (AlarmFragment)getSupportFragmentManager().findFragmentByTag(MyApplication.ALARMFRAGMENT);
            return alramfragment;
        }else if(tag.equals("GoalFragment")){
            GoalFragment goalfragment = (GoalFragment)getSupportFragmentManager().findFragmentByTag(MyApplication.GOALFRAGMENT);
            return goalfragment;
        }else if(tag.equals("WelcomeFragment")){
            WelcomeFragment welcomeFragment = (WelcomeFragment) getSupportFragmentManager().findFragmentByTag(MyApplication.WELCOMEFRAGMENT);
            return welcomeFragment;
        }
        return null;
    }

    @Override

    public void packetReceived(NevoPacket packet) {
        if((byte) GetStepsGoalNevoRequest.HEADER == packet.getHeader())
        {
            DailyStepsNevoPacket steppacket = packet.newDailyStepsNevoPacket();
            WelcomeFragment welcomefragment = (WelcomeFragment)getSupportFragmentManager().findFragmentByTag("WelcomeFragment");
            int dailySteps = steppacket.getDailySteps();
            int dailyGoal = steppacket.getDailyStepsGoal();
            Log.i("MainActivity","dailySteps = " + dailySteps +",dailyGoal = " + dailyGoal );
            welcomefragment.setText(dailySteps+"/"+dailyGoal);
            welcomefragment.setProgressBar((int)(100.0*dailySteps/dailyGoal));
            StepPickerView.saveStepTextToPreference(MainActivity.this, ""+dailyGoal);
        }
    }

    @Override
    public void connectionStateChanged(final boolean isConnected) {
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isConnected )
                {
                    replaceFragment(mPosition, mTag);
                }
                else
                {
                    if (mPosition!=0)
                        replaceFragment(3, "ConnectAnimationFragment");
                }
            }
        });
    }

    public static class PlaceholderFragment {
        private static final String POSTITION = "position";
        private static final String TAG = "tag";
        private static final String SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            Optional<Fragment> fragment = new Optional<Fragment>(null);

            if(sectionNumber <4) {
                switch (sectionNumber) {
                    case 1:
                        fragment.set(new WelcomeFragment());
                        break;
                    case 2:
                        fragment.set(new GoalFragment());
                        break;
                    case 3:
                        fragment.set(new AlarmFragment());
                        break;
                    default:
                        break;
                }
            }else if(sectionNumber == 4){
                fragment.set(new ConnectAnimationFragment());
                Bundle args = new Bundle();
                args.putInt(POSTITION, mPosition);
                args.putString(TAG, mTag);
                args.putInt(SECTION_NUMBER, sectionNumber);
                fragment.get().setArguments(args);
            }
            return fragment.get();
        }
    }
}
