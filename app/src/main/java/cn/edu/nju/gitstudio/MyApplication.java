package cn.edu.nju.gitstudio;

import android.app.Application;
import android.util.Base64;

import cn.edu.nju.gitstudio.pojo.User;

/**
 * Created by mrzero on 17-6-8.
 */

public class MyApplication extends Application {
    private User currentUser;
    private String authToken;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        if (authToken != null) {
            this.authToken = authToken.trim();
        }
        else {
            this.authToken = null;
        }
    }
}
