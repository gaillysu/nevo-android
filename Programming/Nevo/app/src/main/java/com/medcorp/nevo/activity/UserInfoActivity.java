package com.medcorp.nevo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.bruce.pickerview.popwindow.DatePickerPopWin;
import com.medcorp.lunar.modle.CreateUserInfo;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.activity.login.SignupActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/1.
 */
public class UserInfoActivity extends BaseActivity {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private int viewType = -1;
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
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        password = intent.getStringExtra("password");

    }

    @OnClick(R.id.user_info_title_back_ll)
    public void backClick() {
        finish();
        startActivity(SignupActivity.class);
    }

    @OnClick(R.id.register_info_activity_next_tv)
    public void nextClick() {
        String userBirthday = tv_userBirth.getText().toString();
        String userHeight = tv_userHeight.getText().toString();
        String userWeight = tv_userWeight.getText().toString();
        if (!TextUtils.isEmpty(userBirthday) || !TextUtils.isEmpty(userHeight) || !TextUtils.isEmpty(userWeight)) {
            CreateUserInfo userInfo = new CreateUserInfo();
            userInfo.setFirst_name(firstName);
            userInfo.setBirthday(userBirthday);
            userInfo.setEmail(email);
            userInfo.setLast_name(lastName);
            userInfo.setLength(new Integer(userHeight).intValue());
            userInfo.setWeight(Double.parseDouble(userWeight));
            userInfo.setPassword(password);
            userInfo.setSex(gender);

            //TODO
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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                        try {
                            Date date = dateFormat.parse(dateDesc);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        tv_userBirth.setText(new SimpleDateFormat("MMM", Locale.US).format(date) + "-" + day + "-" + year);
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
                        tv_userHeight.setText(dateDesc);
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
                        tv_userWeight.setText(dateDesc);
                    }
                }).viewStyle(viewType)
                .viewTextSize(25)
                .dateChose("60")
                .build();
        pickerPopWin3.showPopWin(UserInfoActivity.this);
    }
}
