package cn.edu.nju.gitstudio;

import android.app.Application;

import cn.edu.nju.gitstudio.pojo.User;

/**
 * Created by mrzero on 17-6-8.
 */

public class MyApplication extends Application {
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
