package com.medcorp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.medcorp.base.BaseActivity;
import com.medcorp.R;
import com.medcorp.activity.login.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/5.
 *
 */
public class ForgetPasswordResultActivity extends BaseActivity {

    @Bind(R.id.forget_password_result_show_email_tx)
    TextView showUserEmailAccountText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_result_page_layout);
        ButterKnife.bind(this);
       Intent intent = getIntent();
        showUserEmailAccountText.setText(intent.getStringExtra("email"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(LoginActivity.class);
                finish();
            }
        }, 1500);
    }

    @OnClick(R.id.back_page_image_button)
    public void closePageClick() {
        finish();
    }
}
