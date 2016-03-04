package com.medcorp.nevo.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.event.ConnectionStateChangedEvent;
import com.medcorp.nevo.event.OnSyncEndEvent;
import com.medcorp.nevo.event.OnSyncStartEvent;
import com.medcorp.nevo.event.SearchEvent;
import com.medcorp.nevo.fragment.AlarmFragment;
import com.medcorp.nevo.fragment.SettingsFragment;
import com.medcorp.nevo.fragment.SleepFragment;
import com.medcorp.nevo.fragment.StepsFragment;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;

import net.medcorp.library.ble.util.Optional;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Karl on 12/10/15.
 */
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener,NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener
{

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind(R.id.activity_main_drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.activity_main_navigation_view)
    NavigationView navigationView;

    private View rootView;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private MenuItem selectedMenuItem;
    private Optional<BaseObservableFragment> activeFragment;
    private FragmentManager fragmentManager;
    private Snackbar snackbar=null;
    private boolean bigSyncStart =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activeFragment =  new Optional<>();
        rootView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout.setDrawerListener(this);
        MenuItem firstItem = navigationView.getMenu().getItem(0);
        onNavigationItemSelected(firstItem);
        firstItem.setChecked(true);
        setTitle(selectedMenuItem.getTitle());
        BaseObservableFragment fragment = StepsFragment.instantiate(MainActivity.this, StepsFragment.class.getName());
        activeFragment.set(fragment);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        if(fragmentManager.getBackStackEntryCount() == 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_main_frame_layout, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!getModel().isWatchConnected())
        {
            getModel().startConnectToWatch(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showStateString(int id,boolean dismiss)
    {
        if(snackbar != null)
        {
            if(snackbar.isShown()) {
                snackbar.dismiss();
            }
        }

        snackbar = Snackbar.make(rootView,"",Snackbar.LENGTH_LONG);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        tv.setText(getString(id));
        snackbar.show();

        if(dismiss)
        {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (bigSyncStart == false) {
                        snackbar.dismiss();
                    }
                }
            }, 2000);
        }
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {}

    @Override
    public void onDrawerOpened(View drawerView) {}

    @Override
    public void onDrawerClosed(View drawerView) {
        setFragment(selectedMenuItem);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setFragment(MenuItem item){
        setTitle(item.getTitle());
        BaseObservableFragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_steps_fragment:
                if(fragmentManager.getBackStackEntryCount() >= 1) {
                    fragmentManager.popBackStack();
                    fragment = (BaseObservableFragment) fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1);
                    activeFragment.set(fragment);
                }
                return;
            case R.id.nav_alarm_fragment:
                fragment = AlarmFragment.instantiate(MainActivity.this,AlarmFragment.class.getName());
                break;
            case R.id.nav_sleep_fragment:
                fragment = SleepFragment.instantiate(MainActivity.this, SleepFragment.class.getName());
                break;
            case R.id.nav_settings_fragment:
                fragment = SettingsFragment.instantiate(MainActivity.this, SettingsFragment.class.getName());
                break;
        }
        if(activeFragment.get().getClass().getName().equals(fragment.getClass().getName())){
            return;
        }
        activeFragment.set(fragment);
        {
            if(android.os.Build.VERSION.SDK_INT >= 19) {
                fragment.setEnterTransition(new Fade().setDuration(300));
            }
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout, fragment);
            if (fragmentManager.getBackStackEntryCount() == 0) {
                        fragmentTransaction.addToBackStack(fragment.getClass().getName());
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectedMenuItem = item;
        drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else if(fragmentManager.getBackStackEntryCount() >= 1) {
            fragmentManager.popBackStack();
            MenuItem item = navigationView.getMenu().getItem(0);
            selectedMenuItem = item;
            item.setChecked(true);
            activeFragment.set((BaseObservableFragment) fragmentManager.getFragments().get(0));
        }else if(fragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }else{
             super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        getMenuInflater().inflate(R.menu.menu_choose_goal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackStackChanged() {
        Log.w("Karl", "On backstack changed. current =  " + fragmentManager.getBackStackEntryCount());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(activeFragment.notEmpty())
        {
            activeFragment.get().onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w("Karl", "Eventbus initiated");
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        Log.w("Karl", "Eventbus stopped");
        super.onStop();
    }

    @Subscribe
    public void onEvent(OnSyncEndEvent event){
        bigSyncStart = false;
        Log.w("Karl", "eventbus Syncing data finished");
        showStateString(R.string.in_app_notification_synced, true);
    }

    @Subscribe
    public void onEvent(OnSyncStartEvent event){
        bigSyncStart = true;
        Log.w("Karl","eventbus Syncing data");
        showStateString(R.string.in_app_notification_syncing,false);
    }

    @Subscribe
    public void onEvent(ConnectionStateChangedEvent event){
        if (event.isConnected()){
            showStateString(R.string.in_app_notification_found_nevo, false);
        }else{
            showStateString(R.string.in_app_notification_nevo_disconnected, false);
        }
    }



    @Subscribe
    public void onEvent(SearchEvent event) {
        Log.w("Karl","eventbus onsearchevent");
        if (!getModel().isBluetoothOn()) {
            showStateString(R.string.in_app_notification_bluetooth_disabled, false);
            return;
        }
        switch (event.getStatus()) {
            case SEARCHING:
                showStateString(R.string.in_app_notification_searching, false);
                break;
        }
    }
}