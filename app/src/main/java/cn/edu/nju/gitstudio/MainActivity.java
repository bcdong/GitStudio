package cn.edu.nju.gitstudio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.edu.nju.gitstudio.util.Consts;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MyApplication mMyApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMyApplication = (MyApplication) getApplication();
        SharedPreferences sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        if (!sp.contains("username") || !sp.contains("password") || !sp.contains("userType")) {
            //User do not login, go to login form
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            mMyApplication.setUsername(sp.getString("username", null));
            mMyApplication.setPassword(sp.getString("password", null));
            mMyApplication.setUserType(sp.getInt("userType", Consts.STUDENT_TYPE));
        }


    }
}
