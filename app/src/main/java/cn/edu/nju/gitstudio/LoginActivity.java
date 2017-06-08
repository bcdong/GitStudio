package cn.edu.nju.gitstudio;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nju.gitstudio.pojo.User;
import cn.edu.nju.gitstudio.type.LoginResult;
import cn.edu.nju.gitstudio.type.ResultStatus;
import cn.edu.nju.gitstudio.util.NetworkHelper;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.activity_login_input_username) EditText mUsernameEditText;
    @BindView(R.id.activity_login_input_password) EditText mPasswordEditText;
    @BindView(R.id.activity_login_btn_login) Button mLoginButton;

    private ProgressDialog mProgressDialog;

    private UserLoginTask mLoginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Login...");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
    }

    private void login() {
        //如果登录任务还未完成，防止创建重复的登录任务
        if (mLoginTask != null) {
            return;
        }

        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        //点击登录按钮后应禁用按钮防止多次点击
        mLoginButton.setEnabled(false);
        mProgressDialog.show();


    }

    private class UserLoginTask extends AsyncTask<String, Void, LoginResult> {

        /**
         * 登录参数
         * @param params params[0] ==> username,
         *               params[1] ==> password
         * @return
         */
        @Override
        protected LoginResult doInBackground(String... params) {
            LoginResult result = new LoginResult();
            if (params.length < 2) {
                Log.e(TAG, "Username or password missing");
                result.setStatus(ResultStatus.AUTH_ERROR);
                return result;
            }
            String username = params[0];
            String password = params[1];
            NetworkHelper networkHelper = NetworkHelper.getInstance();
            try {
                User user = networkHelper.login(username, password);
                //login failed
                if (user == null) {
                    Log.i(TAG, "Username and password do not match");
                    result.setStatus(ResultStatus.AUTH_ERROR);
                } else {
                    Log.i(TAG, "Login success");
                    result.setStatus(ResultStatus.SUCCESS);
                    result.setUser(user);
                    //password should be stored
                    result.setPassword(password);
                }
            } catch (IOException e) {
                Log.e(TAG, "Login failed due to network error", e);
                result.setStatus(ResultStatus.NETWORK_ERROR);
            }
            return result;
        }

        @Override
        protected void onPostExecute(LoginResult loginResult) {
            mLoginTask = null;
            mProgressDialog.hide();

            if (loginResult.getStatus() == ResultStatus.SUCCESS) {
                //set user info to application context
                User user = loginResult.getUser();
                MyApplication application = (MyApplication) getApplication();
                application.setCurrentUser(user);

                //store username and password to shared preferences
                String username = user.getUsername();
                String password = loginResult.getPassword();
                // TODO: 17-6-8 store username and password to shared preferences


            } else if (loginResult.getStatus() == ResultStatus.NETWORK_ERROR){
                mLoginButton.setEnabled(true);
                Toast.makeText(getApplication(), R.string.error_network_fail, Toast.LENGTH_SHORT)
                        .show();
            } else if (loginResult.getStatus() == ResultStatus.AUTH_ERROR) {
                mLoginButton.setEnabled(true);
                Toast.makeText(getApplication(), R.string.error_incorrect_password, Toast.LENGTH_SHORT)
                        .show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mLoginTask = null;
            mProgressDialog.hide();
            mLoginButton.setEnabled(true);
        }
    }
}
