package com.nevowatch.nevo.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.nevowatch.nevo.R;
import com.nevowatch.nevo.View.DrawerIcon;

import java.util.ArrayList;
import java.util.List;

/**
 * NavigationDrawerFragment is responsible for showing slide menu when users swipe right and click the Top-Left icon,
 * and redirect the related page once you press the icon in the slide menu.
 * */

public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private boolean isDrawerOpen = false;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private List<DrawerIcon> mIconList;
    private DrawerIcon mDrawerIconArray[] = new DrawerIcon[]{
            new DrawerIcon(R.drawable.icon_home_button_selected),
            new DrawerIcon(R.drawable.icon_goal_button_selected),
            new DrawerIcon(R.drawable.icon_alarm_button_selected),
            new DrawerIcon(R.drawable.icon_notification_button_selected),
            new DrawerIcon(R.drawable.icon_ota_button_selected)
    };
    private ImageView mDrawerIconImageArray[] = new ImageView[mDrawerIconArray.length];

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        mUserLearnedDrawer = getUserLearnFromPreference(getActivity());

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    private void initDrawerIcon(List<DrawerIcon> list){
        for(int i=0; i<mDrawerIconArray.length; i++)
            list.add(mDrawerIconArray[i]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.navigation_drawer_fragment, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mIconList = new ArrayList<DrawerIcon>();
        initDrawerIcon(mIconList);
        NavigationDrawerAdapter navigationDrawerAdapter = new NavigationDrawerAdapter(getActivity(), R.layout.navigation_drawer_list_item, mIconList);

        mDrawerListView.setAdapter(navigationDrawerAdapter);
        //mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                toolbar,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    saveUserLearnToPreference(getActivity(), mUserLearnedDrawer);
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if(slideOffset > .55 && !isDrawerOpen){
                    getDrawerIcon();
                    setDrawerIconLight(mCurrentSelectedPosition);
                    onDrawerOpened(drawerView);
                    isDrawerOpen = true;
                } else if(slideOffset < .45 && isDrawerOpen) {
                    onDrawerClosed(drawerView);
                    isDrawerOpen = false;
                }
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void getDrawerIcon(){
        for (int i=0; i<mDrawerIconArray.length; i++){
            LinearLayout linearLayout = (LinearLayout) mDrawerListView.getChildAt(i - mDrawerListView.getFirstVisiblePosition());
            mDrawerIconImageArray[i] = (ImageView)linearLayout.findViewById(R.id.drawer_icon_imageView);
        }
    }

    private void setDrawerIconLight(int position){

        switch (position){
            case 0:
                mDrawerIconImageArray[0].setSelected(true);
                mDrawerIconImageArray[1].setSelected(false);
                mDrawerIconImageArray[2].setSelected(false);
                mDrawerIconImageArray[3].setSelected(false);
                mDrawerIconImageArray[4].setSelected(false);
                break;
            case 1:
                mDrawerIconImageArray[0].setSelected(false);
                mDrawerIconImageArray[1].setSelected(true);
                mDrawerIconImageArray[2].setSelected(false);
                mDrawerIconImageArray[3].setSelected(false);
                mDrawerIconImageArray[4].setSelected(false);
                break;
            case 2:
                mDrawerIconImageArray[0].setSelected(false);
                mDrawerIconImageArray[1].setSelected(false);
                mDrawerIconImageArray[2].setSelected(true);
                mDrawerIconImageArray[3].setSelected(false);
                mDrawerIconImageArray[4].setSelected(false);
                break;
            case 3:
                mDrawerIconImageArray[0].setSelected(false);
                mDrawerIconImageArray[1].setSelected(false);
                mDrawerIconImageArray[2].setSelected(false);
                mDrawerIconImageArray[3].setSelected(true);
                mDrawerIconImageArray[4].setSelected(false);
                break;
            case 4:
                mDrawerIconImageArray[0].setSelected(false);
                mDrawerIconImageArray[1].setSelected(false);
                mDrawerIconImageArray[2].setSelected(false);
                mDrawerIconImageArray[3].setSelected(false);
                mDrawerIconImageArray[4].setSelected(true);
                break;
            default:
                break;
        }
    }
    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

       /* if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public void saveUserLearnToPreference(Context context, boolean value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(PREF_USER_LEARNED_DRAWER, value).apply();
    }

    public Boolean getUserLearnFromPreference(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }
}
