package cn.edu.nju.gitstudio.util;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.edu.nju.gitstudio.MyApplication;
import cn.edu.nju.gitstudio.pojo.Exercise;
import cn.edu.nju.gitstudio.pojo.MyClass;
import cn.edu.nju.gitstudio.pojo.User;
import cn.edu.nju.gitstudio.type.ExerciseType;
import okhttp3.Call;
import okhttp3.Callback;
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

    public void asyncGetClass(final Activity activity, final NetworkCallback<MyClass> callback) {
        String authToken = getAuthToken(activity);
        String path = "/group";
        Request request = buildGetRequest(path, authToken);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onGetFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected Code: " + response);
                    onFailure(call, new IOException("Unexpected Code: " + response));
                } else {
                    String responseJson = response.body().string();
                    MyClass[] myClasses = gson.fromJson(responseJson, MyClass[].class);
                    callback.onGetSuccess(myClasses);
                }
            }
        });
    }

    public void asyncGetStudent(final Activity activity, int groupId, final NetworkCallback<User> callback) {
        String authToken = getAuthToken(activity);
        String path = "/group/" + groupId + "/students";
        Request request = buildGetRequest(path, authToken);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onGetFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    onFailure(call, new IOException("Unexpected Code: " + response));
                } else {
                    String responseJson = response.body().string();
                    User[] students = gson.fromJson(responseJson, User[].class);
                    callback.onGetSuccess(students);
                }
            }
        });
    }

    public void asyncGetExercise(final Activity activity, int courseId, ExerciseType type, final NetworkCallback<Exercise> callback) {
        String authToken = getAuthToken(activity);
        String path = "/course/" + courseId;
        if (type == ExerciseType.HOMEWORK) {
            path += "/homework";
        } else if (type == ExerciseType.EXERCISE) {
            path += "/exercise";
        } else {
            path += "/exam";
        }
        Request request = buildGetRequest(path, authToken);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onGetFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected Code: " + response);
                    onFailure(call, new IOException("Unexpected Code: " + response));
                } else {
                    String responseJson = response.body().string();
                    Exercise[] exercises = gson.fromJson(responseJson, Exercise[].class);
                    callback.onGetSuccess(exercises);
                }
            }
        });

    }

    private Request buildGetRequest(String path, String authToken) {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl+path);

        if (authToken != null && !authToken.isEmpty()) {
            //add authentication information to head
            builder.header("Authorization", "Basic "+authToken);
        }
        return builder.build();
    }

    private String getAuthToken(final Activity activity) {
        MyApplication myApplication = (MyApplication) activity.getApplication();
        return myApplication.getAuthToken();
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
