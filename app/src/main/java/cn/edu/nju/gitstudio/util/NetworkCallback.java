package cn.edu.nju.gitstudio.util;

/**
 * Created by mrzero on 17-6-10.
 */

public interface NetworkCallback<T> {
    void onGetSuccess(T[] resultList);
    void onGetFail(Exception ex);
}
