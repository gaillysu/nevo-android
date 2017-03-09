package com.medcorp.fragment;

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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.R;
import com.medcorp.activity.ConnectToOtherAppsActivity;
import com.medcorp.activity.MoreSettingActivity;
import com.medcorp.activity.MyNevoActivity;
import com.medcorp.activity.SettingNotificationActivity;
import com.medcorp.activity.login.LoginActivity;
import com.medcorp.activity.tutorial.TutorialPage1Activity;
import com.medcorp.adapter.SettingMenuAdapter;
import com.medcorp.event.bluetooth.FindWatchEvent;
import com.medcorp.fragment.base.BaseObservableFragment;
import com.medcorp.listener.OnCheckedChangeInListListener;
import com.medcorp.model.SettingsMenuItem;
import com.medcorp.util.LinklossNotificationUtils;
import com.medcorp.util.Preferences;
import com.medcorp.view.ToastHelper;

import net.medcorp.library.ble.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        listMenu = new ArrayList<>();
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_link_loss_notification), R.drawable.setting_linkloss, Preferences.getLinklossNotification(getActivity())));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_notifications), R.drawable.setting_notfications));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_my_nevo), R.drawable.setting_mynevo));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_find_my_watch), R.drawable.setting_findmywatch));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_more), R.drawable.setting_goals));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_other_apps), R.drawable.setting_linkloss));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_support), R.drawable.setting_support));
        listMenu.add(new SettingsMenuItem(getString(R.string.settings_forget_watch), R.drawable.setting_forget));
        //listMenu.add(new SettingsMenuItem(getString(R.string.settings_login), R.drawable.setting_mynevo, getModel().getNevoUser().isLogin()));
        if (getModel().getNevoUser().isLogin()) {
            listMenu.add(new SettingsMenuItem(getString(R.string.google_fit_log_out), R.drawable.logout_icon));
        } else {

            listMenu.add(new SettingsMenuItem(getString(R.string.login_page_activity_title), R.drawable.ic_login_setting_page));
        }
        //        listMenu.add(new SettingsMenuItem(getString(R.string.settings_about), R.drawable.setting_about));

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
            startActivity(MoreSettingActivity.class);

        } else if (position == 5) {
            startActivity(ConnectToOtherAppsActivity.class);
        } else if (position == 6) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.support_url)));
            getActivity().startActivity(intent);

        } else if (position == 7) {

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

        } else if (position == 8) {
            if (!getModel().getNevoUser().isLogin()) {
                getModel().removeUser(getModel().getNevoUser());
                Intent intent = new Intent(SettingsFragment.this.getContext(), LoginActivity.class);
                intent.putExtra("isTutorialPage", false);
                SettingsFragment.this.getContext().getSharedPreferences(Constants.PREF_NAME, 0).edit().putBoolean(Constants.FIRST_FLAG, true);
                startActivity(intent);
            } else {
                Intent intent = new Intent(SettingsFragment.this.getContext(), LoginActivity.class);
                intent.putExtra("isTutorialPage", false);
                getModel().getNevoUser().setIsLogin(false);
                getModel().saveNevoUser(getModel().getNevoUser());
                startActivity(intent);
            }
            SettingsFragment.this.getActivity().finish();
        }
        //        } else if (position == 9) {
        //            Intent intent = new Intent(SettingsFragment.this.getContext(), SettingAboutActivity.class);
        //            startActivity(intent);
        //
        //        }
    }

    @Override
    public void onCheckedChange(CompoundButton buttonView, boolean isChecked, int position) {
        if (position == 0) {
            Preferences.saveLinklossNotification(getActivity(), isChecked);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onEvent(final FindWatchEvent event) {
        if (event.isSuccess()) {
            //when find watch, vibrate cell phone once that means finding out
            LinklossNotificationUtils.sendNotification(getActivity(), true);
        }
    }
}
