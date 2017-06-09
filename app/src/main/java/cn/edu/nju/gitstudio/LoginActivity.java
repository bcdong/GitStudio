package cn.edu.nju.gitstudio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, LoginActivity.class);
    }

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

        //设置全屏显示
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        mProgressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Login...");

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //防止通过点击后退回到MainActivity
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
    }

    private void login() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplication(), R.string.error_empty_username_or_password, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        //如果登录任务还未完成，防止创建重复的登录任务
        if (mLoginTask != null) {
            return;
        }

        //点击登录按钮后应禁用按钮防止多次点击
        mLoginButton.setEnabled(false);
        mProgressDialog.show();

        mLoginTask = new UserLoginTask();
        mLoginTask.execute(username, password);
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
                String username = user.getUsername();
                String password = loginResult.getPassword();

                MyApplication application = (MyApplication) getApplication();
                application.setCurrentUser(user);
                application.setAuthToken(Base64.encodeToString((username+":"+password).getBytes(), Base64.DEFAULT));

                // store username and password to shared preferences
                SharedPreferences sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
                sp.edit().putString("username", username)
                        .putString("password", password)
                        .apply();

                setResult(Activity.RESULT_OK);
                finish();

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
