package cn.edu.nju.gitstudio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MyApplication mMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyApplication = (MyApplication) getApplication();
        SharedPreferences sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        if (!sp.contains("username") || !sp.contains("password")) {
            //User do not login, go to login form
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            String username = sp.getString("username", null);
            String password = sp.getString("password", null);
            // TODO: 17-6-8 此处依然需要登录，需要创建一个异步线程，此时主界面显示logo界面即可。若登录失败则跳转至LoginActivity，成功则吧logo界面换成有内容的主界面。
        }


    }
}
