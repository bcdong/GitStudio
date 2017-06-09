package cn.edu.nju.gitstudio.util;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.edu.nju.gitstudio.pojo.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 处理网络请求
 */

public class NetworkHelper {
    private static final String TAG = "NetworkHelper";
    private static final String baseUrl = "http://115.29.184.56:8090/api";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static NetworkHelper instance;

    private final OkHttpClient client;
    private final Gson gson;

    public static NetworkHelper getInstance() {
        if (instance == null) {
            instance = new NetworkHelper();
        }
        return instance;
    }

    private NetworkHelper() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return 登录成功则返回User对象，失败则返回null
     * @throws IOException
     */
    public User login(String username, String password) throws IOException {
        String identity = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
        Log.i(TAG, "Login for user: " + username);
        String res = postRequest("/user/auth", identity);

        //用户名或密码错误，返回空字符串
        if (res == null || res.trim().isEmpty()) {
            return null;
        }
        return gson.fromJson(res, User.class);
    }

    private String getRequest(String path, String authToken) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl+path);

        if (authToken != null && !authToken.isEmpty()) {
            //add authentication information to head
            builder.header("Authorization", "Basic "+authToken);
        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    private String postRequest(String path, String requestBody) throws IOException {
        RequestBody body = RequestBody.create(JSON, requestBody);
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

}
