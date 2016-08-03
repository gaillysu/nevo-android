package com.medcorp.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.medcorp.ApplicationFlage;
import com.medcorp.activity.UserInfoActivity;
import com.medcorp.base.BaseActivity;
import com.medcorp.event.SignUpEvent;
import com.medcorp.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends BaseActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.input_password_confirm)
    EditText _passwordConfirmText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.register_title)
    RelativeLayout titleRegister;

    private EditText editTextFirstName;
    private EditText editLastName;


    private String firstName;
    private String lastName;
    private ProgressDialog progressDialog;
    private CheckBox checkIsAgreeBt;
    private String email;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        if (ApplicationFlage.FLAGE == ApplicationFlage.Flage.LUNAR) {
            checkIsAgreeBt = (CheckBox) findViewById(R.id.sign_up_check_user_is_agree_terms_radio_bt);
            editTextFirstName = (EditText) findViewById(R.id.register_account_activity_edit_first_name);
            editLastName = (EditText) findViewById(R.id.register_account_activity_edit_last_name);
            checkIsAgreeBt.setChecked(false);
        } else {
            titleRegister.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.register_title_back_image_button)
    public void backClick() {
        finish();
        startActivity(LoginActivity.class);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && ApplicationFlage.FLAGE == ApplicationFlage.Flage.LUNAR) {
            backClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.link_login)
    public void loginLink() {
        finish();
    }

    @OnClick(R.id.btn_signup)
    public void signUpAction() {
        if (!validate()) {
            onSignupFailed();
            return;
        }
        if (ApplicationFlage.FLAGE == ApplicationFlage.Flage.NEVO) {
            _signupButton.setEnabled(false);
            progressDialog = new ProgressDialog(SignupActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.register_popup_message));
            progressDialog.show();
            email = _emailText.getText().toString();
            password = _passwordText.getText().toString();
            getModel().nevoUserRegister(email, password);
        } else {
            Intent intent = new Intent(this, UserInfoActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            startActivity(intent);
            finish();
        }
    }

    @Subscribe
    public void onEvent(SignUpEvent event) {
        switch (event.getSignUpStatus()) {
            case FAILED:
                onSignupFailed();
                break;
            case SUCCESS:
                Toast.makeText(getBaseContext(), R.string.register_success, Toast.LENGTH_SHORT).show();
                _signupButton.setEnabled(true);
                getModel().getNevoUser().setNevoUserEmail(_emailText.getText().toString());
                getModel().saveNevoUser(getModel().getNevoUser());
                setResult(RESULT_OK, null);
                finish();
                break;
        }
        progressDialog.dismiss();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.register_failed, Toast.LENGTH_SHORT).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();
        String passwordConfirm = _passwordConfirmText.getText().toString();

        if (ApplicationFlage.FLAGE == ApplicationFlage.Flage.LUNAR) {
            firstName = editTextFirstName.getText().toString();
            lastName = editLastName.getText().toString();
            if (firstName.isEmpty()) {
                valid = false;
                editTextFirstName.setError(getString(R.string.register_input_first_is_empty));
            } else {
                editTextFirstName.setError(null);
            }

            if (!checkIsAgreeBt.isChecked()) {
                valid = false;
            }
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getString(R.string.register_email_error));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError(getString(R.string.register_password_error));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!passwordConfirm.equals(password)) {
            _passwordText.setError(getString(R.string.register_password_confirm_error));
            valid = false;
        }

        return valid;
    }
}