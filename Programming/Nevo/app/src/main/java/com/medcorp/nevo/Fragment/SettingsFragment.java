package com.medcorp.nevo.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.GoalsActivity;
import com.medcorp.nevo.activity.MyNevoActivity;
import com.medcorp.nevo.activity.SettingAboutActivity;
import com.medcorp.nevo.activity.SettingNotificationActivity;
import com.medcorp.nevo.activity.tutorial.TutorialPage1Activity;
import com.medcorp.nevo.adapter.SettingMenuAdapter;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.model.SettingMenu;
import com.medcorp.nevo.view.ToastHelper;

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
        //TODO put into Strings.xml
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
    public void onRequestResponse(boolean success) {

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
            if(getModel().isWatchConnected()) {
                startActivity(MyNevoActivity.class);
            }else{
                //TODO put into Strings.xml
                ToastHelper.showShortToast(getContext(),"No watch connected.");
            }
        }
        else if(position == 3)
        {
            if(getModel().isWatchConnected()) {
                getModel().blinkWatch();
            }else{
                //TODO put into Strings.xml
                ToastHelper.showShortToast(getContext(),"No watch connected.");
            }
        }
        else if(position == 4)
        {
            startActivity(GoalsActivity.class);
        }
        else if(position == 5)
        {
            //TODO put into config.xml
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://support.nevowatch.com/support/home"));
            getActivity().startActivity(intent);
        }
        else if(position == 6)
        {
            startActivity(SettingAboutActivity.class);
        }
        else if(position == 7)
        {
            new AlertDialog.Builder(this.getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                    //TODO put into Strings.xml
                    .setMessage("Are you sure?")
                    .setPositiveButton(android.R.string.no, null)
                    .setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getModel().forgetDevice();
                            startActivity(new Intent(SettingsFragment.this.getContext(), TutorialPage1Activity.class));
                            SettingsFragment.this.getActivity().finish();
                        }
                    }).setCancelable(false).show();
        }
    }
}
