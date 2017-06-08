package cn.edu.nju.gitstudio.type;

import cn.edu.nju.gitstudio.pojo.User;

/**
 * 登录结果封装类
 */

public class LoginResult {
    private ResultStatus mStatus;
    private User mUser;
    private String password;

    public LoginResult() {
    }

    public LoginResult(ResultStatus status, User user) {
        mStatus = status;
        mUser = user;
    }

    public ResultStatus getStatus() {
        return mStatus;
    }

    public void setStatus(ResultStatus status) {
        mStatus = status;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
