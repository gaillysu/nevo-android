package com.nevowatch.nevo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.WindowManager;

import com.nevowatch.nevo.Fragment.AlarmFragment;
import com.nevowatch.nevo.Fragment.ConnectAnimationFragment;
import com.nevowatch.nevo.Fragment.GoalFragment;
import com.nevowatch.nevo.Fragment.NavigationDrawerFragment;
import com.nevowatch.nevo.Fragment.NotificationFragment;
import com.nevowatch.nevo.Fragment.WelcomeFragment;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.util.Optional;

import java.util.List;


/**
 * MainActivity is a controller, which works for updating UI and connect Nevo Watch by bluetooth
 * 
 *  /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 *  /giphy danger !
 *
 * */
public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,OnSyncControllerListener {
    private static int mPosition = -1;
    private static String mTag;
    private Boolean mIsVisible = true;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Toolbar mToolbar;

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
        // Set a toolbar which will replace the action bar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

        SyncController.Singleton.getInstance(this).startConnect(false, this);
       // NevoNotificationListener.getNotificationAccessPermission(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Optional<String> tag = new Optional<String>(null);
        if(position == mPosition){
            return;
        }
        switch (position+1){
            case WelcomeFragment.WELPOSITION+1:
                tag.set(WelcomeFragment.WELCOMEFRAGMENT);
                mPosition = WelcomeFragment.WELPOSITION;
                mTag = WelcomeFragment.WELCOMEFRAGMENT;
                mTitle = getString(R.string.title_section1);
                break;
            case GoalFragment.GOALPOSITION+1:
                tag.set(GoalFragment.GOALFRAGMENT);
                mPosition = GoalFragment.GOALPOSITION;
                mTag = GoalFragment.GOALFRAGMENT;
                mTitle = getString(R.string.title_section2);
                break;
            case AlarmFragment.ALARMPOSITION+1:
                tag.set(AlarmFragment.ALARMFRAGMENT);
                mPosition = AlarmFragment.ALARMPOSITION;
                mTag = AlarmFragment.ALARMFRAGMENT;
                mTitle = getString(R.string.title_section3);
                break;
            case NotificationFragment.NOTIPOSITION+1:
                tag.set(NotificationFragment.NOTIFICATIONFRAGMENT);
                mPosition = NotificationFragment.NOTIPOSITION;
                mTag = NotificationFragment.NOTIFICATIONFRAGMENT;
                mTitle = getString(R.string.title_section4);
                break;
            default:
                break;
        }

        if(SyncController.Singleton.getInstance(this)!=null && !SyncController.Singleton.getInstance(this).isConnected()){
            replaceFragment(ConnectAnimationFragment.CONNECTPOSITION, ConnectAnimationFragment.CONNECTFRAGMENT);
        }else{
            Log.d("MainActivity", "Connect");
            replaceFragment(position, tag.get());
        }
    }

    /**
     * Replace fragment in the MainActivity
     * */
    public void replaceFragment(final int position, final String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
               // .addToBackStack(null)
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1), tag)
                .commitAllowingStateLoss();
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

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public Fragment getFragment(String tag){
        if(tag.equals(AlarmFragment.ALARMFRAGMENT)){
            AlarmFragment alramfragment = (AlarmFragment)getSupportFragmentManager().findFragmentByTag(AlarmFragment.ALARMFRAGMENT);
            return alramfragment;
        }else if(tag.equals(GoalFragment.GOALFRAGMENT)){
            GoalFragment goalfragment = (GoalFragment)getSupportFragmentManager().findFragmentByTag(GoalFragment.GOALFRAGMENT);
            return goalfragment;
        }else if(tag.equals(WelcomeFragment.WELCOMEFRAGMENT)){
            WelcomeFragment welcomeFragment = (WelcomeFragment) getSupportFragmentManager().findFragmentByTag(WelcomeFragment.WELCOMEFRAGMENT);
            return welcomeFragment;
        }else if(tag.equals(NotificationFragment.NOTIFICATIONFRAGMENT)){
            NotificationFragment notificationFragment = (NotificationFragment) getSupportFragmentManager().findFragmentByTag(NotificationFragment.NOTIFICATIONFRAGMENT);
            return notificationFragment;
        }
        return null;
    }

    @Override
    public void packetReceived(NevoPacket packet) {

        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for(Fragment fragment :  fragments) {
            if(fragment instanceof WelcomeFragment)
            {
                ((WelcomeFragment)fragment).packetReceived(packet);
            }
            else if(fragment instanceof GoalFragment)
            {
                ((GoalFragment)fragment).packetReceived(packet);
            }
            else if(fragment instanceof AlarmFragment)
            {
                ((AlarmFragment)fragment).packetReceived(packet);
            }
            else if(fragment instanceof ConnectAnimationFragment)
            {
                ((ConnectAnimationFragment)fragment).packetReceived(packet);
            }else if(fragment instanceof NotificationFragment){
                ((NotificationFragment)fragment).packetReceived(packet);
            }
        }
    }

    @Override
    public void connectionStateChanged(final boolean isConnected) {
          if(!mIsVisible) return;

          runOnUiThread(new Runnable() {
            @Override
            public void run() {

                List<Fragment> fragments = getSupportFragmentManager().getFragments();

                for(Fragment fragment :  fragments) {
                    if(fragment instanceof WelcomeFragment)
                    {
                        ((WelcomeFragment)fragment).connectionStateChanged(isConnected);
                    }
                    else if(fragment instanceof GoalFragment)
                    {
                        ((GoalFragment)fragment).connectionStateChanged(isConnected);
                    }
                    else if(fragment instanceof AlarmFragment)
                    {
                        ((AlarmFragment)fragment).connectionStateChanged(isConnected);
                    }
                    else if(fragment instanceof ConnectAnimationFragment)
                    {
                        ((ConnectAnimationFragment)fragment).connectionStateChanged(isConnected);
                    }else if(fragment instanceof NotificationFragment){
                        ((NotificationFragment)fragment).connectionStateChanged(isConnected);
                    }
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

            switch (sectionNumber) {
                case WelcomeFragment.WELPOSITION+1:
                    fragment.set(new WelcomeFragment());
                    break;
                case GoalFragment.GOALPOSITION+1:
                    fragment.set(new GoalFragment());
                    break;
                case AlarmFragment.ALARMPOSITION+1:
                    fragment.set(new AlarmFragment());
                    break;
                case NotificationFragment.NOTIPOSITION+1:
                    fragment.set(new NotificationFragment());
                    break;
                case ConnectAnimationFragment.CONNECTPOSITION+1:
                    fragment.set(new ConnectAnimationFragment());
                    Bundle args = new Bundle();
                    args.putInt(POSTITION, mPosition);
                    args.putString(TAG, mTag);
                    args.putInt(SECTION_NUMBER, sectionNumber);
                    fragment.get().setArguments(args);
                    break;
                default:
                    break;
            }
            return fragment.get();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SyncController.Singleton.getInstance(this).setSyncControllerListenser(this);
        if(!mIsVisible){
            if(SyncController.Singleton.getInstance(this).isConnected()){
                replaceFragment(mPosition, mTag);
            }else {
                replaceFragment(ConnectAnimationFragment.CONNECTPOSITION, ConnectAnimationFragment.CONNECTFRAGMENT);
            }
        }
        mIsVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsVisible = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPosition = -1;
    }
}
