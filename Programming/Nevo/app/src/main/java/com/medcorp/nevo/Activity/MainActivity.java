package com.medcorp.nevo.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.medcorp.nevo.activity.base.BaseActionBarActivity;
import com.medcorp.nevo.fragment.AlarmFragment;
import com.medcorp.nevo.fragment.ConnectAnimationFragment;
import com.medcorp.nevo.fragment.GoalFragment;
import com.medcorp.nevo.fragment.HistoryFragment;
import com.medcorp.nevo.fragment.MyNevoFragment;
import com.medcorp.nevo.fragment.NavigationDrawerFragment;
import com.medcorp.nevo.fragment.NotificationFragment;
import com.medcorp.nevo.fragment.SleepHistoryFragment;
import com.medcorp.nevo.fragment.WelcomeFragment;
import com.medcorp.nevo.R;
import com.medcorp.nevo.ble.controller.OtaController;
import com.medcorp.nevo.ble.controller.SyncController;
import com.medcorp.nevo.ble.listener.OnSyncControllerListener;
import com.medcorp.nevo.ble.model.packet.NevoPacket;
import com.medcorp.nevo.ble.util.Constants;
import com.medcorp.nevo.ble.util.Optional;

import java.util.List;


/**
 * MainActivity is a controller, which works for updating UI and connect Nevo Watch by bluetooth
 *
 *  /!\/!\/!\Backbone Class : Modify with care/!\/!\/!\
 *  /giphy danger !
 *
 * */
public class MainActivity extends BaseActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,OnSyncControllerListener {
    private static int position = -1;
    private static String tag;
    private Boolean isVisible = true;
//    private IGoogleFit googleFitManager;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;
    private DrawerLayout drawerLayout;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        getModel().setActiveActivity(this);
        //disenable navigation drawer shadow

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();
        // Set a toolbar which will replace the action bar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set up the drawer.
        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        getModel().getSyncController().startConnect(false, this);

//        googleFitManager = GoogleFitManager.getInstance(MainActivity.this, this);

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
//            googleFitManager.getClient().connect();
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
        //if (googleFitManager.getClient().isConnected()) {
//            googleFitManager.getClient().disconnect();
        //}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        googleFitManager.dealActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
//        googleFitManager.dealSaveInstanceState(outState);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Optional<String> tag = new Optional<String>(null);
        if(position == MainActivity.position){
            return;
        }
        switch (position+1){
            case WelcomeFragment.WELPOSITION+1:
                tag.set(WelcomeFragment.WELCOMEFRAGMENT);
                MainActivity.position = WelcomeFragment.WELPOSITION;
                MainActivity.tag = WelcomeFragment.WELCOMEFRAGMENT;
                title = getString(R.string.title_section1);
                break;
            case GoalFragment.GOALPOSITION+1:
                tag.set(GoalFragment.GOALFRAGMENT);
                MainActivity.position = GoalFragment.GOALPOSITION;
                MainActivity.tag = GoalFragment.GOALFRAGMENT;
                title = getString(R.string.title_section2);
                break;
            case AlarmFragment.ALARMPOSITION+1:
                tag.set(AlarmFragment.ALARMFRAGMENT);
                MainActivity.position = AlarmFragment.ALARMPOSITION;
                MainActivity.tag = AlarmFragment.ALARMFRAGMENT;
                title = getString(R.string.title_section3);
                break;
            case NotificationFragment.NOTIPOSITION+1:
                tag.set(NotificationFragment.NOTIFICATIONFRAGMENT);
                MainActivity.position = NotificationFragment.NOTIPOSITION;
                MainActivity.tag = NotificationFragment.NOTIFICATIONFRAGMENT;
                title = getString(R.string.title_section4);
                break;
            case MyNevoFragment.MYNEVOPOSITION+1:
                tag.set(MyNevoFragment.MYNEVOFRAGMENT);
                MainActivity.position = MyNevoFragment.MYNEVOPOSITION;
                MainActivity.tag = MyNevoFragment.MYNEVOFRAGMENT;
                title = getString(R.string.title_section5);
                break;
            case SleepHistoryFragment.SLEEPHISTORYPOSITION+1:
                tag.set(SleepHistoryFragment.SLEEPHISTORYFRAGMENT);
                MainActivity.position = SleepHistoryFragment.SLEEPHISTORYPOSITION;
                MainActivity.tag  = SleepHistoryFragment.SLEEPHISTORYFRAGMENT;
                title = getString(R.string.title_section6);
                break;
            default:
                break;
        }

        if(getModel().getSyncController()!=null && !getModel().getSyncController().isConnected()){
            replaceFragment(ConnectAnimationFragment.CONNECTPOSITION, ConnectAnimationFragment.CONNECTFRAGMENT);
        }else{
            Log.d("MainActivity", "Connect");
            replaceFragment(position, tag.get());
            if(position != com.medcorp.nevo.activity.OTAActivity.OTAPOSITION && getModel().getOtaController().getState() == Constants.DFUControllerState.INIT)
            {
                getModel().getOtaController().switch2SyncController();
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

        if (!navigationDrawerFragment.isDrawerOpen()) {
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

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
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
        }else if(tag.equals(SleepHistoryFragment.SLEEPHISTORYFRAGMENT)){
            SleepHistoryFragment historyFragment = (SleepHistoryFragment) getSupportFragmentManager().findFragmentByTag(SleepHistoryFragment.SLEEPHISTORYFRAGMENT);
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
            }else if(fragment instanceof SleepHistoryFragment){
                ((SleepHistoryFragment)fragment).packetReceived(packet);
            }
        }
    }

    @Override
    public void connectionStateChanged(final boolean isConnected) {
        if(!isVisible) return;

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
                    }else if(fragment instanceof SleepHistoryFragment){
                        ((SleepHistoryFragment)fragment).connectionStateChanged(isConnected);
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
                //case HistoryFragment.HISTORYPOSITION+1:
                //    fragment.set(new HistoryFragment());
                //    break;
                case SleepHistoryFragment.SLEEPHISTORYPOSITION+1:
                    fragment.set(new SleepHistoryFragment());
                    break;
                case ConnectAnimationFragment.CONNECTPOSITION+1:
                    fragment.set(new ConnectAnimationFragment());
                    Bundle args = new Bundle();
                    args.putInt(POSTITION, position);
                    args.putString(TAG, tag);
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
        getModel().getSyncController().setSyncControllerListenser(this);
        getModel().getSyncController().setVisible(true);
        if(!isVisible){
            if(getModel().getSyncController().isConnected()){
                replaceFragment(position, tag);
            }else {
                replaceFragment(ConnectAnimationFragment.CONNECTPOSITION, ConnectAnimationFragment.CONNECTFRAGMENT);
            }
        }
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        getModel().getSyncController().setVisible(false);
        isVisible = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        position = -1;
    }
}
