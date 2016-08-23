package com.medcorp.activity.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.medcorp.R;
import com.medcorp.activity.ForgetPasswordActivity;
import com.medcorp.base.BaseActivity;
import com.medcorp.event.LoginEvent;
import com.medcorp.model.User;
import com.medcorp.network.med.model.LoginUser;
import com.medcorp.network.med.model.LoginUserModel;
import com.medcorp.network.med.model.LoginUserRequest;
import com.medcorp.network.med.model.UserWithLocation;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
    private int inputPasswordErrorSum = 0;

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

        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        LoginUser user = new LoginUser();
        user.setEmail(email);
        user.setPassword(password);
        getModel().getNetworkManage().execute(new LoginUserRequest(user, getModel().getNetworkManage().getAccessToken()),
                new RequestListener<LoginUserModel>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        spiceException.printStackTrace();
                        EventBus.getDefault().post(new LoginEvent(LoginEvent.status.FAILED));
                    }

                    @Override
                    public void onRequestSuccess(LoginUserModel loginUserModel) {
                        if (loginUserModel.getStatus() == 1) {
                            UserWithLocation user = loginUserModel.getUser();
                            User nevoUser = getModel().getNevoUser();
                            try {
                                nevoUser.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(user.getBirthday().getDate()).getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            nevoUser.setFirstName(user.getFirst_name());
                            nevoUser.setHeight(user.getLength());
                            nevoUser.setLastName(user.getLast_name());
                            nevoUser.setWeight(user.getWeight());
                            nevoUser.setId(user.getId());
                            nevoUser.setNevoUserEmail(user.getEmail());
                            nevoUser.setIsLogin(true);
                            getModel().saveNevoUser(nevoUser);
                            getModel().getSyncController().getDailyTrackerInfo(true);
                            getModel().getCloudSyncManager().launchSyncAll(nevoUser, getModel().getNeedSyncSteps(nevoUser.getNevoUserID()),
                                    getModel().getNeedSyncSleep(nevoUser.getNevoUserID()));
                            EventBus.getDefault().post(new LoginEvent(LoginEvent.status.SUCCESS));
                        } else {
                            EventBus.getDefault().post(new LoginEvent(LoginEvent.status.FAILED));
                            inputPasswordErrorSum++;
                            if (inputPasswordErrorSum % 3 == 0) {
                                promptUserChangePassword();
                            }
                        }
                    }

                });

    }

    public void promptUserChangePassword() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.open_forget_password_dialog_title))
                .content(getString(R.string.prompt_is_not_forget_password))
                .negativeText(android.R.string.no)
                .positiveText(android.R.string.yes).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                startActivity(ForgetPasswordActivity.class);
                finish();
            }
        }).show();
    }

    @Subscribe
    public void onEvent(LoginEvent event) {
        switch (event.getLoginStatus()) {
            case FAILED:

                onLoginFailed();
                break;
            case SUCCESS:
                onLoginSuccess();
                break;

        }
        progressDialog.dismiss();
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
