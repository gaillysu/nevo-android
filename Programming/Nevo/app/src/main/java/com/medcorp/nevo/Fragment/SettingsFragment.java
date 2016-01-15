package com.medcorp.nevo.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.GoalsActivity;
import com.medcorp.nevo.activity.MyNevoActivity;
import com.medcorp.nevo.activity.SettingAboutActivity;
import com.medcorp.nevo.activity.SettingNotificationActivity;
import com.medcorp.nevo.adapter.SettingMenuAdapter;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.fragment.base.BaseFragment;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.model.SettingMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 14/12/15.
 */
public class SettingsFragment extends BaseObservableFragment implements AdapterView.OnItemClickListener{

    @Bind(R.id.fragment_setting_list_view)
    ListView settingListView;

    private List<SettingMenu> listMenu;

    private SettingMenuAdapter settingAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        listMenu = new ArrayList<SettingMenu>();
        listMenu.add(new SettingMenu("Link loss notification",R.drawable.setting_linkloss,true));
        listMenu.add(new SettingMenu("Notifications",R.drawable.setting_notfications,false));
        listMenu.add(new SettingMenu("My nevo",R.drawable.setting_mynevo,false));
        listMenu.add(new SettingMenu("Find my watch",R.drawable.setting_findmywatch,false));
        listMenu.add(new SettingMenu("Goals",R.drawable.setting_goals,false));
        listMenu.add(new SettingMenu("Support",R.drawable.setting_support,false));
        listMenu.add(new SettingMenu("About",R.drawable.setting_about,false));
        listMenu.add(new SettingMenu("Forget watch",R.drawable.setting_forget,false));
        settingAdapter = new SettingMenuAdapter(getContext(),listMenu);
        settingListView.setAdapter(settingAdapter);
        settingListView.setOnItemClickListener(this);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void notifyDatasetChanged() {

    }

    @Override
    public void notifyOnConnected() {

    }

    @Override
    public void notifyOnDisconnected() {

    }

    @Override
    public void batteryInfoReceived(Battery battery) {

    }

    @Override
    public void findWatchSuccess() {

    }

    @Override
    public void onSearching() {

    }

    @Override
    public void onSearchSuccess() {

    }

    @Override
    public void onSearchFailure() {

    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncEnd() {

    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 1)
        {
            startActivity(SettingNotificationActivity.class);
        }
        else if(position == 2)
        {
            startActivity(MyNevoActivity.class);
        }
        else if(position == 3)
        {
            getModel().blinkWatch();
        }
        else if(position == 4)
        {
            startActivity(GoalsActivity.class);
        }
        else if(position == 5)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://support.nevowatch.com/support/home"));
            getActivity().startActivity(intent);
        }
        else if(position == 6)
        {
            startActivity(SettingAboutActivity.class);
        }
        else if(position == 7)
        {
           getModel().forgetDevice();
        }
    }
}
