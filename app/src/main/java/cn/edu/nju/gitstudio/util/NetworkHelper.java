package cn.edu.nju.gitstudio.util;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.edu.nju.gitstudio.MyApplication;
import cn.edu.nju.gitstudio.pojo.Exercise;
import cn.edu.nju.gitstudio.pojo.MyClass;
import cn.edu.nju.gitstudio.pojo.QuestionScoreInterval;
import cn.edu.nju.gitstudio.pojo.TestCase;
import cn.edu.nju.gitstudio.pojo.TestCaseResult;
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
                    callback.onGetFail(new IOException("asyncGetClass Error: " + response));
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
                    callback.onGetFail(new IOException("asyncGetStudent Error: " + response));
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
                    callback.onGetFail(new IOException("asyncGetExercise Error: " + response));
                } else {
                    String responseJson = response.body().string();
                    Exercise[] exercises = gson.fromJson(responseJson, Exercise[].class);
                    callback.onGetSuccess(exercises);
                }
            }
        });

    }

    public void asyncGetScoreResult(final Activity activity, int assignmentId, final NetworkCallback<QuestionScoreInterval> callback) {
        String authToken = getAuthToken(activity);
        String path = "/assignment/"+assignmentId+"/score";
        Request request = buildGetRequest(path, authToken);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onGetFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()){
                    callback.onGetFail(new IOException("asyncGetScoreResult Error: " + response));
                } else {
                    QuestionScoreInterval[] scoreIntervals = new QuestionScoreInterval[0];
                    //获取完考试结果后需要对不同分数段的人数进行统计
                    String responseJson = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseJson);
                        JSONArray questions = jsonObject.getJSONArray("questions");
                        scoreIntervals = new QuestionScoreInterval[questions.length()];
                        for (int i=0; i<questions.length();++i){
                            JSONObject question = questions.getJSONObject(i);
                            String questionTitle = question.getJSONObject("questionInfo").getString("title");
                            int[] peopleCount = {0, 0, 0, 0};
                            JSONArray students = question.getJSONArray("students");
                            for (int j=0; j<students.length(); ++j){
                                int score = students.getJSONObject(j).getInt("score");
                                if (score < 60){
                                    ++ peopleCount[0];
                                } else if (score < 80){
                                    ++ peopleCount[1];
                                } else if (score < 90){
                                    ++ peopleCount[2];
                                } else {
                                    ++ peopleCount[3];
                                }
                            }
                            scoreIntervals[i] = new QuestionScoreInterval(questionTitle, peopleCount);
                        }
                        Log.d(TAG, "The count of questions is: " + scoreIntervals.length);
                    } catch (JSONException e) {
                        //json解析错误
                        e.printStackTrace();
                    }
                    callback.onGetSuccess(scoreIntervals);
                }
            }
        });
    }

    public void asyncGetTestCaseResult(Activity activity, int assignmentId, int studentId, final NetworkCallback<TestCaseResult> callback){
        String authToken = getAuthToken(activity);

        // TODO: 17-6-29 由于目前接口只有229号学生有数据，而登录者nanguangtailang是64号，为了测试先用229号显示数据
        studentId = 229;
        
        String path = "/assignment/"+assignmentId+"/student/"+studentId+"/analysis";
        Request request = buildGetRequest(path, authToken);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onGetFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()){
                    callback.onGetFail(new IOException("asyncGetTestCaseResult Error: "+response));
                } else {
                    TestCaseResult[] results = new TestCaseResult[0];
                    String responseJson = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseJson);
                        JSONArray questionResults = jsonObject.getJSONArray("questionResults");
                        results = new TestCaseResult[questionResults.length()];
                        for (int i=0; i<questionResults.length(); ++i){
                            TestCaseResult result = new TestCaseResult();
                            JSONObject questionObj = questionResults.getJSONObject(i);
                            result.setQuestionId(questionObj.getInt("questionId"));
                            result.setQuestionTitle(questionObj.getString("questionTitle"));

                            JSONObject scoreResult = questionObj.getJSONObject("scoreResult");
                            result.setScore(scoreResult.getInt("score"));

                            JSONObject testResult = questionObj.getJSONObject("testResult");
                            result.setCompile_succeeded(testResult.getBoolean("compile_succeeded"));
                            result.setTested(testResult.getBoolean("tested"));
                            TestCase[] testCaseList;
                            if (!testResult.isNull("testcases")){
                                // TODO: 17-6-29 接口再此处不完善，这里可能抛异常，注意检查此处
                                JSONArray testcases = testResult.getJSONArray("testcases");
                                testCaseList = new TestCase[testcases.length()];
                                for (int j=0; j<testcases.length(); ++j){
                                    JSONObject testcase = testcases.getJSONObject(j);
                                    String name =testcase.getString("name");
                                    boolean passed = testcase.getBoolean("passed");
                                    testCaseList[j] = new TestCase(name, passed);
                                }
                            } else {
                                testCaseList = new TestCase[0];
                            }
                            result.setTestCases(testCaseList);

                            results[i] = result;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callback.onGetSuccess(results);
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
