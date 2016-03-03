package com.medcorp.nevo.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.ConnectToOtherAppsActivity;
import com.medcorp.nevo.activity.GoalsActivity;
import com.medcorp.nevo.activity.MyNevoActivity;
import com.medcorp.nevo.activity.SettingNotificationActivity;
import com.medcorp.nevo.activity.tutorial.TutorialPage1Activity;
import com.medcorp.nevo.adapter.SettingMenuAdapter;
import com.medcorp.nevo.fragment.base.BaseObservableFragment;
import com.medcorp.nevo.listener.OnCheckedChangeInListListener;
import com.medcorp.nevo.model.Battery;
import com.medcorp.nevo.model.SettingsMenuItem;
import com.medcorp.nevo.util.Preferences;
import com.medcorp.nevo.view.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 14/12/15.
 */
public class SettingsFragment extends BaseObservableFragment implements AdapterView.OnItemClickListener, OnCheckedChangeInListListener{

    @Bind(R.id.fragment_setting_list_view)
    ListView settingListView;

    private List<SettingsMenuItem> listMenu;

    private SettingMenuAdapter settingAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        listMenu = new ArrayList<SettingsMenuItem>();
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_link_loss_notification),R.drawable.setting_linkloss,Preferences.getLinklossNotification(getActivity())));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_notifications),R.drawable.setting_notfications));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_my_nevo),R.drawable.setting_mynevo));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_find_my_watch),R.drawable.setting_findmywatch));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_goals),R.drawable.setting_goals));
        //TODO change Icon
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_other_apps),R.drawable.setting_linkloss));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_support),R.drawable.setting_support));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_forget_watch),R.drawable.setting_forget));
        settingAdapter = new SettingMenuAdapter(getContext(),listMenu, this);
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
        if(position == 1) {
            startActivity(SettingNotificationActivity.class);
        } else if(position == 2) {
            if(getModel().isWatchConnected()) {
                startActivity(MyNevoActivity.class);
            }else{
                ToastHelper.showShortToast(getContext(),R.string.in_app_notification_no_watch);
            }
        } else if(position == 3) {
            if(getModel().isWatchConnected()) {
                getModel().blinkWatch();
            }else{
                ToastHelper.showShortToast(getContext(),R.string.in_app_notification_no_watch);
            }
        } else if(position == 4) {
            startActivity(GoalsActivity.class);
        } else if(position == 5){
            startActivity(ConnectToOtherAppsActivity.class);
        } else if(position == 6) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_url)));
            getActivity().startActivity(intent);
        } else if(position == 7) {
            new MaterialDialog.Builder(getContext())
                    .content(R.string.settings_sure)
                    .negativeText(android.R.string.no)
                    .positiveText(android.R.string.yes)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            getModel().forgetDevice();
                            startActivity(TutorialPage1Activity.class);
                            SettingsFragment.this.getActivity().finish();
                        }
                    })
                    .cancelable(false)
                    .show();
        }
    }

    @Override
    public void onCheckedChange(CompoundButton buttonView, boolean isChecked, int position) {
        if (position == 0) {
            Preferences.saveLinklossNotification(getActivity(), isChecked);
        }
    }
}
