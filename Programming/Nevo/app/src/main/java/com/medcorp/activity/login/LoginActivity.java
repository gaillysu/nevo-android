package com.medcorp.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.medcorp.R;
import com.medcorp.base.BaseActivity;
import com.medcorp.event.LoginEvent;
import com.medcorp.network.med.model.LoginUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGN_UP = 0;
    private ProgressDialog progressDialog;

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        if (getModel().getNevoUser().getNevoUserEmail() != null) {
            _emailText.setText(getModel().getNevoUser().getNevoUserEmail());
        }
    }

    @OnClick(R.id.link_signup)
    public void signUpAction() {
        startActivity(SignupActivity.class);
        finish();
    }

    @OnClick(R.id.login_skip_bt)
    public void skipLogin() {
        if (getIntent().getBooleanExtra("isTutorialPage", true)) {
            startActivity(TutorialPage1Activity.class);
        } else {
            startActivity(MainActivity.class);
        }
        finish();
    }

//    @OnClick(R.id.open_tutorial_page_video)
//    public void openTutorialPageVideo(){
//        Uri uri = Uri.parse(getString(R.string.video_url));
//        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
//        intent.setDataAndType(uri , "video/*");
//        startActivity(intent);
//    }
            startActivity(SignupActivity.class);
            finish();
    }

    @OnClick(R.id.btn_login)
    public void loginAction() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.log_in_popup_message));
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        LoginUser user = new LoginUser();
        user.setEmail(email);
        user.setPassword(password);
        getModel().getCloudSyncManager().userLogin(user);
    }

    @Subscribe
    public void onEvent(LoginEvent event) {
        progressDialog.dismiss();
        switch (event.getLoginStatus()) {
            case FAILED:

                onLoginFailed();
                break;
            case SUCCESS:
                onLoginSuccess();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGN_UP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, null);
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    public void onLoginSuccess() {
        Toast.makeText(getBaseContext(), R.string.log_in_success, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
        getModel().getNevoUser().setNevoUserEmail(_emailText.getText().toString());
        getModel().saveNevoUser(getModel().getNevoUser());
        setResult(RESULT_OK, null);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), R.string.log_in_failed, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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

        return valid;
    }
}
