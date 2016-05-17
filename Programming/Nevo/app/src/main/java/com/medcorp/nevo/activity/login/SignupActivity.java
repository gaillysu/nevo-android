package com.medcorp.nevo.activity.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.medcorp.nevo.R;
import com.medcorp.nevo.activity.base.BaseActivity;
import com.medcorp.nevo.event.SignUpEvent;

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

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.link_login)
    public void loginLink(){
        finish();
    }

    @OnClick(R.id.btn_signup)
    public void signUpAction() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);
        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.register_popup_message));
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        getModel().nevoUserRegister(email, password);
    }

    @Subscribe
    public void onEvent(SignUpEvent event){
        switch (event.getSignUpStatus()){
            case FAILED:
                onSignupFailed();
                break;
            case SUCCESS:
                Toast.makeText(getBaseContext(), R.string.register_success, Toast.LENGTH_LONG).show();
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
        Toast.makeText(getBaseContext(), R.string.register_failed, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordConfirm = _passwordConfirmText.getText().toString();

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

        if(!passwordConfirm.equals(password)) {
            _passwordText.setError(getString(R.string.register_password_confirm_error));
            valid = false;
        }

        return valid;
    }
}