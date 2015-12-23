package com.medcorp.nevo.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
public class MainActivity extends BaseActivity implements ActivityObservable, FragmentManager.OnBackStackChangedListener{

    @Bind(R.id.main_toolbar)
    Toolbar toolbar;

    @Bind((R.id.activity_main_drawer_layout))
    DrawerLayout drawerLayout;

    @Bind(R.id.activity_main_navigation_view)
    NavigationView navigationView;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private MainMenuNavigationSelectListener mainMenuNavigationSelectListener;
    private MenuItem selectedMenuItem;
    private Optional<BaseObservableFragment> activeFragment;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        activeFragment =  new Optional<>();
        getModel().observableActivity(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
        mainMenuNavigationSelectListener = new MainMenuNavigationSelectListener();
        navigationView.setNavigationItemSelectedListener(mainMenuNavigationSelectListener);
        drawerLayout.setDrawerListener(new MainMenuDrawerListener());
        MenuItem firstItem = navigationView.getMenu().getItem(0);
        mainMenuNavigationSelectListener.onNavigationItemSelected(firstItem);
        firstItem.setChecked(true);
        setTitle(selectedMenuItem.getTitle());
        BaseObservableFragment fragment = StepsFragment.instantiate(MainActivity.this, StepsFragment.class.getName());
        activeFragment.set(fragment);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        fragmentManager.beginTransaction()
                .add(R.id.activity_main_frame_layout, fragment)
                .addToBackStack(StepsFragment.class.getName())
                .commit();

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
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
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
        if(activeFragment.notEmpty())
        {
            activeFragment.get().notifyOnConnected();
        }
    }

    @Override
    public void notifyOnDisconnected() {
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
    public void onBackStackChanged() {
    }

    private class MainMenuDrawerListener implements DrawerLayout.DrawerListener {

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {

        }

        @Override
        public void onDrawerClosed(View drawerView) {
            setFragment(selectedMenuItem);
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
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
        boolean chooseStepFragment = false;
        setTitle(item.getTitle());
        BaseObservableFragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_steps_fragment:
                fragment = StepsFragment.instantiate(MainActivity.this, StepsFragment.class.getName());
                chooseStepFragment = true;
                break;
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
        activeFragment.set(fragment);
        {
            fragment.setEnterTransition(new Fade().setDuration(300));
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_main_frame_layout, fragment)
                    .commit();
        }
    }

    private class MainMenuNavigationSelectListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            selectedMenuItem = item;
            drawerLayout.closeDrawers();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else if(fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        }else if(fragmentManager.getBackStackEntryCount() == 1) {
            super.onBackPressed();
            finish();
        }else{
             super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }
}