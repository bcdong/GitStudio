package cn.edu.nju.gitstudio;

import android.app.Application;

import cn.edu.nju.gitstudio.pojo.User;

/**
 * 存放全局变量的作用域
 */

public class MyApplication extends Application {
    private User currentUser;
    private String authToken;
    //软工一课程ID默认为1
    private int courseId;

    @Override
    public void onCreate() {
        super.onCreate();
        courseId = 1;
    }

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

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}
