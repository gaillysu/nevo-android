package com.medcorp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.medcorp.R;
import com.medcorp.activity.ConnectToOtherAppsActivity;
import com.medcorp.activity.GoalsActivity;
import com.medcorp.activity.MyNevoActivity;
import com.medcorp.activity.SettingAboutActivity;
import com.medcorp.activity.SettingNotificationActivity;
import com.medcorp.activity.login.LoginActivity;
import com.medcorp.adapter.SettingMenuAdapter;
import com.medcorp.fragment.base.BaseObservableFragment;
import com.medcorp.listener.OnCheckedChangeInListListener;
import com.medcorp.model.SettingsMenuItem;
import com.medcorp.util.Preferences;
import com.medcorp.view.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by karl-john on 14/12/15.
 */
public class SettingsFragment extends BaseObservableFragment implements AdapterView.OnItemClickListener, OnCheckedChangeInListListener {

    @Bind(R.id.fragment_setting_list_view)
    ListView settingListView;

    private List<SettingsMenuItem> listMenu;

    private SettingMenuAdapter settingAdapter;

    final private int REQUEST_LOGIN = 100;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        listMenu = new ArrayList<SettingsMenuItem>();
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_link_loss_notification), R.drawable.setting_linkloss, Preferences.getLinklossNotification(getActivity())));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_notifications), R.drawable.setting_notfications));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_my_nevo), R.drawable.setting_mynevo));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_find_my_watch), R.drawable.setting_findmywatch));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_goals), R.drawable.setting_goals));
        //TODO change Icon
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_other_apps), R.drawable.setting_linkloss));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_support), R.drawable.setting_support));

        //        if (ApplicationFlag.FLAG == ApplicationFlag.Flag.NEVO) {
        //            listMenu.add(new SettingsMenuItem(getString(R.string.settings_forget_watch), R.drawable.setting_forget));
        //            listMenu.add(new SettingsMenuItem(getString(R.string.settings_login), R.drawable.setting_mynevo, getModel().getNevoUser().isLogin()));
        //        } else if (ApplicationFlag.FLAG == ApplicationFlag.Flag.LUNAR) {
        //        }
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_about), R.drawable.setting_about));

        settingAdapter = new SettingMenuAdapter(getContext(), listMenu, this);
        settingListView.setAdapter(settingAdapter);
        settingListView.setOnItemClickListener(this);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.add_menu).setVisible(false);
        menu.findItem(R.id.choose_goal_menu).setVisible(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1) {
            startActivity(SettingNotificationActivity.class);
        } else if (position == 2) {
            if (getModel().isWatchConnected()) {
                startActivity(MyNevoActivity.class);
            } else {
                ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            }
        } else if (position == 3) {
            if (getModel().isWatchConnected()) {
                getModel().blinkWatch();
            } else {
                ToastHelper.showShortToast(getContext(), R.string.in_app_notification_no_watch);
            }
        } else if (position == 4) {
            startActivity(GoalsActivity.class);
        } else if (position == 5) {
            startActivity(ConnectToOtherAppsActivity.class);
        } else if (position == 6) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_url)));
            getActivity().startActivity(intent);
        } else if (position == 7) {
            //TODO
            Intent intent  = new Intent(SettingsFragment.this.getContext(), SettingAboutActivity.class);
            startActivity(intent);
            //her read about code
        }
    }

    @Override
    public void onCheckedChange(CompoundButton buttonView, boolean isChecked, final int position) {
        if (position == 0) {
            Preferences.saveLinklossNotification(getActivity(), isChecked);
        }
        if (position == 8) {
            if (isChecked) {
                getActivity().startActivityForResult(new Intent(getActivity(), LoginActivity.class), REQUEST_LOGIN);
            } else {
                getModel().getNevoUser().setIsLogin(false);
                getModel().saveNevoUser(getModel().getNevoUser());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                listMenu.set(8, new SettingsMenuItem(getString(R.string.settings_login), R.drawable.setting_mynevo, true));
                settingAdapter.notifyDataSetChanged();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                listMenu.set(8, new SettingsMenuItem(getString(R.string.settings_login), R.drawable.setting_mynevo, false));
                settingAdapter.notifyDataSetChanged();
            }
        }
    }
}
