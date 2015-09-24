package com.nevowatch.nevo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.nevowatch.nevo.Fragment.AlarmFragment;
import com.nevowatch.nevo.Fragment.ConnectAnimationFragment;
import com.nevowatch.nevo.Fragment.GoalFragment;
import com.nevowatch.nevo.Fragment.HistoryFragment;
import com.nevowatch.nevo.Fragment.NavigationDrawerFragment;
import com.nevowatch.nevo.Fragment.NotificationFragment;
import com.nevowatch.nevo.Fragment.MyNevoFragment;
import com.nevowatch.nevo.Fragment.WelcomeFragment;
import com.nevowatch.nevo.History.database.DatabaseHelper;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.OtaController;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;
import com.nevowatch.nevo.ble.util.Constants;
import com.nevowatch.nevo.ble.util.Optional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private GoogleFitManager mGfManager;
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
		
        mGfManager = GoogleFitManager.getInstance(MainActivity.this, this);

        final String google_services_framework ="com.google.android.gsf";
        final String google_play_services ="com.google.android.gms";
        final String google_fitness ="com.google.android.apps.fitness";
        final String google_account = "com.google.android.gsf.login";

        final PackageManager pm = getPackageManager();
        boolean isInstalled_gsf = false;
        boolean isInstalled_gps = false;
        boolean isInstalled_gf = false;
        boolean isInstalled_gam = false;

        final List<PackageInfo> appList  = pm.getInstalledPackages(0);
        for (PackageInfo app:appList)
        {
            if(app.packageName.equals(google_services_framework))
            {
                Log.i(MainActivity.class.getSimpleName(),app.packageName + ",version:"+app.versionName);
                isInstalled_gsf = true;
            }
            else if(app.packageName.equals(google_play_services) /*&& app.versionName.contains("7.")*/)
            {
                Log.i(MainActivity.class.getSimpleName(),app.packageName + ",version:"+app.versionName);
                isInstalled_gps = true;
            }
            else if(app.packageName.equals(google_fitness) /*&& app.versionName.contains("1.5")*/)
            {
                Log.i(MainActivity.class.getSimpleName(),app.packageName + ",version:"+app.versionName);
                isInstalled_gf = true;
            }
            else if(app.packageName.equals(google_account))
            {
                Log.i(MainActivity.class.getSimpleName(),app.packageName + ",version:"+app.versionName);
                isInstalled_gam = true;
            }
        }
        if(isInstalled_gsf && isInstalled_gps && isInstalled_gf && isInstalled_gam) {
            Log.i("GoogleFitManager", "Connecting...");
            mGfManager.getmClient().connect();
        }
        else
        {
            //some android ROM image has disable the alertDialog feature, such as xiaomi
            //SyncController.Singleton.getInstance(this).setVisible(true);
            //SyncController.Singleton.getInstance(this).showMessage(R.string.install_google_app_title,R.string.install_google_app_content);
            /**
            new AlertDialog.Builder(MainActivity.this,AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle(R.string.install_google_app_title)
                    .setMessage(R.string.install_google_app_content)
                    .setPositiveButton(android.R.string.cancel,null)
                    .setNegativeButton(R.string.ok_button,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String able= getResources().getConfiguration().locale.getCountry();
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.fitness&hl="+able);
                            Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(it);
                        }
                    })
                    .show();
             */
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //if (mGfManager.getmClient().isConnected()) {
        //    mGfManager.getmClient().disconnect();
        //}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGfManager.dealActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mGfManager.dealSaveInstanceState(outState);
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
            case MyNevoFragment.MYNEVOPOSITION+1:
                tag.set(MyNevoFragment.MYNEVOFRAGMENT);
                mPosition = MyNevoFragment.MYNEVOPOSITION;
                mTag = MyNevoFragment.MYNEVOFRAGMENT;
                mTitle = getString(R.string.title_section5);
                break;
            case HistoryFragment.HISTORYPOSITION+1:
                tag.set(HistoryFragment.HISTORYFRAGMENT);
                mPosition = HistoryFragment.HISTORYPOSITION;
                mTag = HistoryFragment.HISTORYFRAGMENT;
                mTitle = getString(R.string.title_section6);
                break;
            default:
                break;
        }

        if(SyncController.Singleton.getInstance(this)!=null && !SyncController.Singleton.getInstance(this).isConnected()){
            replaceFragment(ConnectAnimationFragment.CONNECTPOSITION, ConnectAnimationFragment.CONNECTFRAGMENT);
        }else{
            Log.d("MainActivity", "Connect");
            replaceFragment(position, tag.get());
            if(position !=OTAActivity.OTAPOSITION && OtaController.Singleton.getInstance(this,false).getState() == Constants.DFUControllerState.INIT)
            {
                OtaController.Singleton.getInstance(this,false).switch2SyncController();
            }
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
           // getMenuInflater().inflate(R.menu.main, menu);
           // if(SyncController.Singleton.getInstance(this).getFirmwareVersion() != null)
           //     menu.findItem(R.id.firmware_version).setTitle("Firmware Version: " + SyncController.Singleton.getInstance(this).getFirmwareVersion() + ", " + SyncController.Singleton.getInstance(this).getSoftwareVersion());
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.firmware_version:
                if(SyncController.Singleton.getInstance(this).getFirmwareVersion() != null)
                    item.setTitle("Firmware Version: " + SyncController.Singleton.getInstance(this).getFirmwareVersion() + ", " + SyncController.Singleton.getInstance(this).getSoftwareVersion());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
        }else if(tag.equals(MyNevoFragment.MYNEVOFRAGMENT)){
            MyNevoFragment mynevoFragment = (MyNevoFragment) getSupportFragmentManager().findFragmentByTag(MyNevoFragment.MYNEVOFRAGMENT);
            return mynevoFragment;
        }else if(tag.equals(HistoryFragment.HISTORYFRAGMENT)){
            HistoryFragment historyFragment = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(HistoryFragment.HISTORYFRAGMENT);
            return historyFragment;
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
            }else if(fragment instanceof MyNevoFragment){
                ((MyNevoFragment)fragment).packetReceived(packet);
            }else if(fragment instanceof HistoryFragment){
                ((HistoryFragment)fragment).packetReceived(packet);
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
                    }else if(fragment instanceof MyNevoFragment){
                        ((MyNevoFragment)fragment).connectionStateChanged(isConnected);
                    }else if(fragment instanceof HistoryFragment){
                        ((HistoryFragment)fragment).connectionStateChanged(isConnected);
                    }
                }
            }
        });
    }

    @Override
    public void firmwareVersionReceived(final Constants.DfuFirmwareTypes whichfirmware, final String version) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();

                for(Fragment fragment :  fragments) {
                    if(fragment instanceof MyNevoFragment){
                        ((MyNevoFragment)fragment).firmwareVersionReceived(whichfirmware,version);
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
                case MyNevoFragment.MYNEVOPOSITION+1:
                    fragment.set(new MyNevoFragment());
                    break;
                case HistoryFragment.HISTORYPOSITION+1:
                    fragment.set(new HistoryFragment());
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
        SyncController.Singleton.getInstance(this).setVisible(true);
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
        SyncController.Singleton.getInstance(this).setVisible(false);
        mIsVisible = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPosition = -1;
    }
}
