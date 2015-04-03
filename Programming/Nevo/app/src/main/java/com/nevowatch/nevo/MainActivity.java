package com.nevowatch.nevo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.nevowatch.nevo.Fragment.AlarmFragment;
import com.nevowatch.nevo.Fragment.GoalFragment;
import com.nevowatch.nevo.Fragment.NavigationDrawerFragment;
import com.nevowatch.nevo.Fragment.StepPickerFragment;
import com.nevowatch.nevo.Fragment.TimePickerFragment;
import com.nevowatch.nevo.Fragment.WelcomeFragment;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GoalFragment.GoalFragmentCallbacks,
        AlarmFragment.AlarmFragmentCallbacks,
        WelcomeFragment.WelcomeFragmentCallbacks,
        TimePickerFragment.TimePickerFragmentCallbacks,
        StepPickerFragment.StepPickerFragmentCallbacks{

    private static final int SETCLOCKTIME = 1;
    private static final int SETSTEPGOAL = 2;
    private static final int SETSTEPMODE = 3;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SETCLOCKTIME:
                    AlarmFragment alramfragment = (AlarmFragment)getSupportFragmentManager().findFragmentByTag("AlarmFragment");
                    alramfragment.setClock(msg.getData().getString("Clock"));
                    break;
                case SETSTEPGOAL:
                case SETSTEPMODE:
                    GoalFragment goalfragment = (GoalFragment)getSupportFragmentManager().findFragmentByTag("GoalFragment");
                    if(msg.what == SETSTEPGOAL){
                        goalfragment.setStep(msg.getData().getString("Goal"));
                    }else if(msg.what == SETSTEPMODE){
                        switch (msg.getData().getInt("Mode")){
                            case 0:
                                goalfragment.setStep(new Integer(7000).toString());
                                break;
                            case 1:
                                goalfragment.setStep(new Integer(10000).toString());
                                break;
                            case 2:
                                goalfragment.setStep(new Integer(20000).toString());
                                break;
                            default:
                                break;
                       }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        String tag = null;
        switch (position+1){
            case 1:
                tag = "WelcomeFragment";
                break;
            case 2:
                tag = "GoalFragment";
                break;
            case 3:
                tag = "AlarmFragment";
            default:
                break;

        }
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
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    @Override
    public void showStep() {
        showStepPickerDialog();
    }

    @Override
    public void setStepMode(int mode) {
        Message msg = new Message();
        msg.what = SETSTEPMODE;
        Bundle bundle = new Bundle();
        bundle.putInt("Mode", mode);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    public void showTime() {
        showTimePickerDialog();
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
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setClockTime(String clockTime) {
        Message msg = new Message();
        msg.what = SETCLOCKTIME;
        Bundle bundle = new Bundle();
        bundle.putString("Clock", clockTime);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showStepPickerDialog(){
        DialogFragment newFragment = new StepPickerFragment();
        newFragment.show(getSupportFragmentManager(), "stepPicker");
    }

    @Override
    public void setStepGoal(String stepGoal) {
        Message msg = new Message();
        msg.what = SETSTEPGOAL;
        Bundle bundle = new Bundle();
        bundle.putString("Goal", stepGoal);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public static class PlaceholderFragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment = null;

            switch (sectionNumber){
                case 1:
                    fragment = new WelcomeFragment();
                    break;
                case 2:
                    fragment = new GoalFragment();
                    break;
                case 3:
                    fragment = new AlarmFragment();
                    break;
                default:
                    Bundle args = new Bundle();
                    args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                    fragment.setArguments(args);
                    break;
            }
            return fragment;
        }
    }

}
