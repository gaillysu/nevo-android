package com.nevowatch.nevo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.nevowatch.nevo.Fragment.AlarmFragment;
import com.nevowatch.nevo.Fragment.AlertDialogFragment;
import com.nevowatch.nevo.Fragment.ConnectAnimationFragment;
import com.nevowatch.nevo.Fragment.GoalFragment;
import com.nevowatch.nevo.Fragment.NavigationDrawerFragment;
import com.nevowatch.nevo.Fragment.StepPickerFragment;
import com.nevowatch.nevo.Fragment.TimePickerFragment;
import com.nevowatch.nevo.Fragment.WelcomeFragment;
import com.nevowatch.nevo.Function.Optional;
import com.nevowatch.nevo.Function.SaveData;
import com.nevowatch.nevo.Service.GetDataService;
import com.nevowatch.nevo.Service.MyService;
import com.nevowatch.nevo.ble.controller.OnSyncControllerListener;
import com.nevowatch.nevo.ble.controller.SyncController;
import com.nevowatch.nevo.ble.model.packet.NevoPacket;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GoalFragment.GoalFragmentCallbacks,
        AlarmFragment.AlarmFragmentCallbacks,
        WelcomeFragment.WelcomeFragmentCallbacks,
        TimePickerFragment.TimePickerFragmentCallbacks,
        StepPickerFragment.StepPickerFragmentCallbacks,
        ConnectAnimationFragment.ConnectAnimationFragmentCallbacks,
        OnSyncControllerListener {

    private static final int SETCLOCKTIME = 1;
    private static final int SETSTEPGOAL = 2;
    private static final int SETSTEPMODE = 3;
    private static final int SETDEGREE = 4;
    private static final String MYSERVICE = "com.nevowatch.nevo.MyService";

    private static int mPosition;
    private static String mTag;
    private MyReciver mReciver;
    private GetDataService mService;
    private SyncController mSyncController;

    /**
     * Interactions between Activity and Service
     */
    public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (GetDataService) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /**
     * BroadCast from MyService
     */
    private void saveDegreeToPref(Intent intent){
        SaveData.saveHourDegreeToPreference(MainActivity.this, intent.getFloatExtra("HourDegree", 0));
        SaveData.saveMinDegreeToPreference(MainActivity.this, intent.getFloatExtra("MinDegree", 0));
    }
    public class MyReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            WelcomeFragment fragment = (WelcomeFragment)getSupportFragmentManager().findFragmentByTag("WelcomeFragment");
            if (intent.getAction().equals(MYSERVICE)) {
                if(fragment != null){
                    Message msg = new Message();
                    msg.what = SETDEGREE;
                    Bundle bundle = new Bundle();
                    bundle.putFloat("HourDegree", intent.getFloatExtra("HourDegree", 0));
                    bundle.putFloat("MinDegree", intent.getFloatExtra("MinDegree", 0));
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    saveDegreeToPref(intent);
                }else{
                    saveDegreeToPref(intent);
                }
            }
        }
    }

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
                case SETDEGREE:
                    WelcomeFragment welcomefragment = (WelcomeFragment)getSupportFragmentManager().findFragmentByTag("WelcomeFragment");
                    welcomefragment.setHour(msg.getData().getFloat("HourDegree"));
                    welcomefragment.setMin(msg.getData().getFloat("MinDegree"));
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
 /*       mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));*/

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //Start Service
        Intent intent = new Intent(this, MyService.class);
        this.bindService(intent, mConnection, BIND_AUTO_CREATE);

        //Initialize BroadCast
        mReciver = new MyReciver();
        IntentFilter intentFilter = new IntentFilter(MYSERVICE);
        this.registerReceiver(mReciver, intentFilter);

        //mSyncController = SyncController.Factory.newInstance(this);
        //mSyncController.startConnect(true,this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Optional<String> tag = new Optional<String>(null);
        switch (position+1){
            case 1:
                tag.set("WelcomeFragment");
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
        if(SaveData.getBleConnectFromPreference(getApplicationContext()) == false){
            Log.d("MainActivity", "DisConnect");
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

    @Override
    public void replaceFragment(final int position, final String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1), tag)
                .commit();
    }

    @Override
    public void showWarning() {
        showAlertDialog();
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
            default:
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

    public void showAlertDialog(){
        DialogFragment newFragment = new AlertDialogFragment();
        newFragment.show(getSupportFragmentManager(), "warning");
    }

    @Override

    public void packetReceived(NevoPacket packet) {

    }

    @Override
    public void connectionStateChanged(boolean isConnected) {
        if (isConnected) {
            Toast.makeText(this, "Nevo Connected!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Nevo Disconnect!", Toast.LENGTH_LONG).show();
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(mConnection);
        this.unregisterReceiver(mReciver);
        SaveData.saveBleConnectToPreference(getApplicationContext(), false);
        Log.d("MainActivity", "onDestory");
    }
}
