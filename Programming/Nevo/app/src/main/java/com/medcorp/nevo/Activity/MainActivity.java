package com.medcorp.nevo.activity;

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
import com.medcorp.nevo.activity.observer.ActivityObservable;
import com.medcorp.nevo.ble.util.Optional;
import com.medcorp.nevo.fragment.AlarmFragment;
import com.medcorp.nevo.fragment.SettingsFragment;
import com.medcorp.nevo.fragment.SleepFragment;
import com.medcorp.nevo.fragment.StepsFragment;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.model.Battery;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Karl on 12/10/15.
 */
public class MainActivity extends BaseActivity implements ActivityObservable, DrawerLayout.DrawerListener,NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener
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
        setContentView(R.layout.activity_main_new);
        activeFragment =  new Optional<>();
        rootView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);

        getModel().observableActivity(this);
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
        getModel().observableActivity(this);
        if(!getModel().isWatchConnected())
        {
            getModel().startConnectToWatch(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getModel().observableActivity(null);
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

    @Override
    public void notifyDatasetChanged() {
        if(activeFragment.notEmpty())
        {
            activeFragment.get().notifyDatasetChanged();
        }
    }

    @Override
    public void notifyOnConnected() {
        showStateString(R.string.in_app_notification_found_nevo, false);
        if(activeFragment.notEmpty())
        {
            activeFragment.get().notifyOnConnected();
        }
    }

    @Override
    public void notifyOnDisconnected() {
        if (!getModel().isBluetoothOn()){
            showStateString(R.string.in_app_notification_bluetooth_disabled,false);
            return;
        }
        showStateString(R.string.in_app_notification_nevo_disconnected,false);
        if(activeFragment.notEmpty())
        {
            activeFragment.get().notifyOnDisconnected();
        }
    }

    @Override
    public void batteryInfoReceived(Battery battery) {
        if(activeFragment.notEmpty())
        {
            activeFragment.get().batteryInfoReceived(battery);
        }
    }

    @Override
    public void findWatchSuccess() {
        if(activeFragment.notEmpty())
        {
            activeFragment.get().findWatchSuccess();
        }
    }

    @Override
    public void onSearching() {
        if (!getModel().isBluetoothOn()){
            showStateString(R.string.in_app_notification_bluetooth_disabled,false);
            return;
        }
        showStateString(R.string.in_app_notification_searching,false);
        if(activeFragment.notEmpty())
        {
            activeFragment.get().onSearching();
        }
    }

    @Override
    public void onSearchSuccess() {
        if(activeFragment.notEmpty())
        {
            activeFragment.get().onSearchSuccess();
        }
    }

    @Override
    public void onSearchFailure() {
        showStateString(R.string.in_app_notification_could_not_find,true);
        if(activeFragment.notEmpty())
        {
            activeFragment.get().onSearchFailure();
        }
    }

    @Override
    public void onConnecting() {
        if(activeFragment.notEmpty()) {
            activeFragment.get().onConnecting();
        }
    }

    @Override
    public void onSyncStart() {
        //big sync need about 7~8s
        bigSyncStart = true;
        showStateString(R.string.in_app_notification_syncing,false);
        if(activeFragment.notEmpty()) {
            activeFragment.get().onSyncStart();
        }
    }

    @Override
    public void onSyncEnd() {
        bigSyncStart = false;
        showStateString(R.string.in_app_notification_synced,true);
        if(activeFragment.notEmpty()) {
            activeFragment.get().onSyncEnd();
        }
    }

    @Override
    public void onInitializeStart() {

    }

    @Override
    public void onInitializeEnd() {

    }

    @Override
    public void onRequestResponse(boolean success) {
        if(activeFragment.notEmpty())
        {
            activeFragment.get().onRequestResponse(success);
        }
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
            fragment.setEnterTransition(new Fade().setDuration(300));
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
}