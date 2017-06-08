package cn.edu.nju.gitstudio;

import android.app.Application;

/**
 * Created by mrzero on 17-6-8.
 */

public class MyApplication extends Application {
    private String mUsername;
    private String mPassword;
    private String mUserType;

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getUserType() {
        return mUserType;
    }

    public void setUserType(String userType) {
        mUserType = userType;
    }
}
