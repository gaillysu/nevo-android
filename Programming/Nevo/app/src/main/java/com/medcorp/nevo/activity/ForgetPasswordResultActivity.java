package com.medcorp.nevo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ForgetPasswordResultActivity extends BaseActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password_result_page_layout);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_page_image_button)
    public void closePageClick(){
        finish();
    }
}
