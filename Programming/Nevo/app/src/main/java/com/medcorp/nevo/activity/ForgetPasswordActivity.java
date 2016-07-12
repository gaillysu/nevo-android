package com.medcorp.nevo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.util.PublicUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/5.
 */
public class ForgetPasswordActivity extends BaseActivity {

    @Bind(R.id.forget_password_input_email_edit)
    EditText editEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_passwor_activity_layout);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.back_page_image_button)
    public void backPageClick() {
        finish();
    }

    @OnClick(R.id.forget_password_send_bt)
    public void forgetPasswordClick(){
        String email = editEmail.getText().toString();
        if(!TextUtils.isEmpty(email)){
            if(PublicUtils.checkEmail(email)){

                //TODO
                startActivity(ForgetPasswordResultActivity.class);
            }else {
             editEmail.setError(getString(R.string.email_format_error));
            }
        }else{
            editEmail.setError(getString(R.string.tips_user_account_password));
        }

    }
}
