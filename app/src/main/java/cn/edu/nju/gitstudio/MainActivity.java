package cn.edu.nju.gitstudio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import cn.edu.nju.gitstudio.pojo.User;
import cn.edu.nju.gitstudio.type.LoginResult;
import cn.edu.nju.gitstudio.type.ResultStatus;
import cn.edu.nju.gitstudio.util.NetworkHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOGIN_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        if (!sp.contains("username") || !sp.contains("password")) {
            //User do not login, go to login form
            Intent intent = LoginActivity.newIntent(this);
            startActivityForResult(intent, REQUEST_LOGIN_CODE);
        } else {
            String username = sp.getString("username", null);
            String password = sp.getString("password", null);
            new UserLoginTask().execute(username, password);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN_CODE) {
            if (resultCode == RESULT_OK) {
                setupMainView();
            }
        }
    }

    /**
     * 登录完成后布局主界面
     */
    private void setupMainView() {
        Log.i(TAG, "Draw main view after login success");
        TextView textView = (TextView) findViewById(R.id.test);
        MyApplication application = (MyApplication) getApplication();
        textView.setText(application.getCurrentUser().getName());
        // TODO: 17-6-8 tbd
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

            if (loginResult.getStatus() == ResultStatus.SUCCESS) {
                //set user info to application context
                User user = loginResult.getUser();
                MyApplication application = (MyApplication) getApplication();
                application.setCurrentUser(user);

                // TODO: 17-6-8 此处正式进入主界面，需要重新布局
                setupMainView();

            } else if (loginResult.getStatus() == ResultStatus.NETWORK_ERROR){
                Toast.makeText(getApplication(), R.string.error_network_fail, Toast.LENGTH_SHORT)
                        .show();
                Intent intent = LoginActivity.newIntent(MainActivity.this);
                startActivityForResult(intent, REQUEST_LOGIN_CODE);
            } else if (loginResult.getStatus() == ResultStatus.AUTH_ERROR) {
                Toast.makeText(getApplication(), R.string.error_incorrect_password, Toast.LENGTH_SHORT)
                        .show();
                Intent intent = LoginActivity.newIntent(MainActivity.this);
                startActivityForResult(intent, REQUEST_LOGIN_CODE);
            }
        }
    }

}
