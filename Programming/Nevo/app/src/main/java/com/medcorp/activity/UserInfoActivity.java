package com.medcorp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.medcorp.R;
import com.medcorp.activity.login.LoginActivity;
import com.medcorp.activity.login.SignupActivity;
import com.medcorp.base.BaseActivity;
import com.medcorp.event.SignUpEvent;
import com.medcorp.network.med.model.CreateUser;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.medcorp.R.style.AppTheme_Dark_Dialog;

/**
 * Created by Administrator on 2016/7/1.
 */
public class UserInfoActivity extends BaseActivity {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Snackbar snackbar;
    private int viewType = -1;
    private ProgressDialog progressDialog;
    @Bind(R.id.user_info_activity_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.register_account_activity_edit_birthday)
    TextView tv_userBirth;
    @Bind(R.id.register_account_activity_edit_height)
    TextView tv_userHeight;
    @Bind(R.id.register_account_activity_edit_weight)
    TextView tv_userWeight;

    @Bind(R.id.user_info_sex_male_tv)
    TextView maleTextView;
    @Bind(R.id.user_info_sex_female_tv)
    TextView famaleTextView;

    private int gender = 1; //0:female, 1: male

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_layout);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        password = intent.getStringExtra("password");

    }

    @OnClick(R.id.user_info_title_back_ll)
    public void backClick() {
        startActivity(SignupActivity.class);
        finish();
    }

    @OnClick(R.id.register_info_activity_next_tv)
    public void nextClick() {
        String userBirthday = tv_userBirth.getText().toString();
        String userHeight = tv_userHeight.getText().toString();
        String userWeight = tv_userWeight.getText().toString();
        if (!TextUtils.isEmpty(userBirthday) || !TextUtils.isEmpty(userHeight) || !TextUtils.isEmpty(userWeight)) {
            try {
                CreateUser userInfo = new CreateUser();
                userInfo.setFirst_name(firstName);
                userInfo.setBirthday(userBirthday);
                userInfo.setEmail(email);
                userInfo.setLast_name(lastName);
                userInfo.setLength(new Integer(userHeight.replace(getString(R.string.info_company_height), "")).intValue());
                userInfo.setWeight(Double.parseDouble(userWeight.replace(getString(R.string.info_company_weight), "")));
                userInfo.setPassword(password);
                userInfo.setSex(gender);

                progressDialog = new ProgressDialog(UserInfoActivity.this, AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.network_wait_text));
                progressDialog.show();

                getModel().getCloudSyncManager().createUser(userInfo);
            } catch (NumberFormatException e) {
                showSnackbar(R.string.user_no_select_profile_info);
            }
        } else {
            if (userBirthday.isEmpty()) {
                tv_userBirth.setError(getString(R.string.user_info_user_birthday_error));
            } else {
                tv_userBirth.setError(null);
            }
            if (userHeight.isEmpty()) {
                tv_userHeight.setError(getString(R.string.user_info_user_height_error));
            } else {
                tv_userHeight.setError(null);
            }
            if (userWeight.isEmpty()) {
                tv_userWeight.setError(getString(R.string.user_info_user_weight_error));
            } else {
                tv_userWeight.setError(null);

            }

        }


    }

    public void showSnackbar(int id) {
        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        tv.setText(getString(id));
        Snackbar.SnackbarLayout ve = (Snackbar.SnackbarLayout) snackbar.getView();
        ve.setBackgroundColor(getResources().getColor(R.color.snackbar_bg_color));
        snackbar.show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, 1800);
    }

    @Subscribe
    public void onEvent(SignUpEvent event) {
        progressDialog.dismiss();
        switch (event.getSignUpStatus()) {
            case FAILED:
                showSnackbar(R.string.register_failed);
                break;
            case SUCCESS:
                showSnackbar(R.string.register_success);
                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                intent.putExtra("isTutorialPage", true);
                startActivity(intent);
                finish();
                break;

        }
    }

    @OnClick(R.id.user_info_sex_male_tv)
    public void selectMale() {
        gender = 1;
        famaleTextView.setTextColor(getResources().getColor(R.color.user_sex_text_normal_color));
        famaleTextView.setBackground(getResources().getDrawable(R.drawable.shape_button_select_normal));
        maleTextView.setTextColor(getResources().getColor(R.color.user_info_text_select_color));
        maleTextView.setBackground(getResources().getDrawable(R.drawable.shape_login_button_bg));

    }

    @OnClick(R.id.user_info_sex_female_tv)
    public void selectFamale() {
        gender = 0;
        maleTextView.setTextColor(getResources().getColor(R.color.user_sex_text_normal_color));
        maleTextView.setBackground(getResources().getDrawable(R.drawable.shape_button_select_normal));
        famaleTextView.setTextColor(getResources().getColor(R.color.user_info_text_select_color));
        famaleTextView.setBackground(getResources().getDrawable(R.drawable.shape_login_button_bg));
    }

    @OnClick(R.id.register_account_activity_edit_birthday)
    public void setUserBIrthday() {
        viewType = 1;
        final Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formatDate = format.format(date);
        DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(UserInfoActivity.this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date userSelectDate = dateFormat.parse(dateDesc);
                            tv_userBirth.setText(day + "-" + new SimpleDateFormat("MMM").format(userSelectDate) + "-" + year);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).viewStyle(viewType)
                .viewTextSize(25) // pick view text size
                .minYear(Integer.valueOf(formatDate.split("-")[0]) - 100) //min year in loop
                .maxYear(Integer.valueOf(formatDate.split("-")[0])) // max year in loop
                .dateChose((Integer.valueOf(formatDate.split("-")[0]) - 30)
                        + "-" + formatDate.split("-")[1] + "-" + formatDate.split("-")[2]) // date chose when init popwindow
                .build();
        pickerPopWin.showPopWin(UserInfoActivity.this);
    }

    @OnClick(R.id.register_account_activity_edit_height)
    public void setUserHeight() {
        viewType = 2;
        DatePickerPopWin pickerPopWin2 = new DatePickerPopWin.Builder(UserInfoActivity.this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        tv_userHeight.setText(dateDesc + getString(R.string.info_company_height));
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose("170")
                .build();
        pickerPopWin2.showPopWin(UserInfoActivity.this);
    }


    @OnClick(R.id.register_account_activity_edit_weight)
    public void setUserWeight() {
        viewType = 3;
        DatePickerPopWin pickerPopWin3 = new DatePickerPopWin.Builder(UserInfoActivity.this,
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month,
                                                    int day, String dateDesc) {
                        tv_userWeight.setText(dateDesc + getString(R.string.info_company_weight));
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose("60")
                .build();
        pickerPopWin3.showPopWin(UserInfoActivity.this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
