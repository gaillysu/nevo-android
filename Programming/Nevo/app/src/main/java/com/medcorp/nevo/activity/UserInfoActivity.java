package com.medcorp.nevo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/1.
 */
public class UserInfoActivity extends BaseActivity{

    private String email;
    private String firstName;
    private String lastName;
    private String password;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_layout);
        ButterKnife.bind(this);
        Intent intent =getIntent();
        email = intent.getStringExtra("email");
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        password = intent.getStringExtra("password");

    }

    @OnClick(R.id.user_info_title_back_ll)
    public void backClick(){

    }

    @OnClick(R.id.register_info_activity_next_tv)
    public void nextClick(){

    }
}
