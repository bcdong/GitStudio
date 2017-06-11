package cn.edu.nju.gitstudio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nju.gitstudio.pojo.User;
import cn.edu.nju.gitstudio.type.ExerciseType;
import cn.edu.nju.gitstudio.type.LoginResult;
import cn.edu.nju.gitstudio.type.ResultStatus;
import cn.edu.nju.gitstudio.util.NetworkHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOGIN_CODE = 0;

    @BindView(R.id.activity_main_toolbar) Toolbar mToolbar;

    private AccountHeader mAccountHeader;
    private Drawer mDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        initDrawer(savedInstanceState);

        //登录
        SharedPreferences sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        if (!sp.contains("username") || !sp.contains("password")) {
            //User do not login, go to login form
            Intent intent = LoginActivity.newIntent(this);
            startActivityForResult(intent, REQUEST_LOGIN_CODE);
        } else {
            MyApplication myApplication = (MyApplication) getApplication();
            User user = myApplication.getCurrentUser();
            if (user == null) {         //APP刚刚启动，需要联网登录
                String username = sp.getString("username", null);
                String password = sp.getString("password", null);
                new UserLoginTask().execute(username, password);
            } else {                    //用户已经登录，可能时屏幕旋转或者按home放入后台后又回来
                // 在initDrawer中已经处理好了，此处不需要处理
                // do nothing
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN_CODE) {
            if (resultCode == RESULT_OK) {
                setupMainView();
            }
        }
    }

    /**
     * 登录完成后布局主界面
     */
    private void setupMainView() {
        Log.i(TAG, "Draw main view after login success");
        MyApplication myApplication = (MyApplication) getApplication();
        User user = myApplication.getCurrentUser();
        //有可能是先退出登录再登录进来的，需要先移除原来的item
        mAccountHeader.removeProfileByIdentifier(0);
        mDrawer.removeAllItems();

        IProfile profile = new ProfileDrawerItem()
                .withName(user.getName())
                .withEmail(user.getEmail())
                .withIcon(R.drawable.avatar)
                .withIdentifier(0);
        mAccountHeader.addProfiles(profile);

        if (user.getType().equals(getString(R.string.user_type_teacher))) {
            mDrawer.addItems(
                    new PrimaryDrawerItem().withName(R.string.drawer_teacher_all_classes).withIdentifier(100),
                    new PrimaryDrawerItem().withName(R.string.drawer_teacher_homework).withIdentifier(101),
                    new PrimaryDrawerItem().withName(R.string.drawer_teacher_exercise).withIdentifier(102),
                    new PrimaryDrawerItem().withName(R.string.drawer_teacher_exam).withIdentifier(103),
                    new SectionDrawerItem().withName(R.string.drawer_about_account),
                    new SecondaryDrawerItem().withName(R.string.drawer_logout).withIdentifier(300).withSelectable(false)
            );
            mDrawer.setSelection(100, false);

        } else if (user.getType().equals(getString(R.string.user_type_student))) {
            mDrawer.addItems(
                    new PrimaryDrawerItem().withName(R.string.drawer_student_homework).withIdentifier(200),
                    new PrimaryDrawerItem().withName(R.string.drawer_student_exercise).withIdentifier(201),
                    new PrimaryDrawerItem().withName(R.string.drawer_student_exam).withIdentifier(202),
                    new SectionDrawerItem().withName(R.string.drawer_about_account),
                    new PrimaryDrawerItem().withName(R.string.drawer_logout).withIdentifier(300).withSelectable(false)
            );
            mDrawer.setSelection(200, false);
        }
        //设置默认fragment
        setDefaultFragment(user);

    }

    /**
     * 初始化左侧抽屉界面
     */
    private void initDrawer(Bundle savedInstanceState) {
        Log.d(TAG, "Enter method initDrawer");
        Log.d(TAG, "savedInstance == null ? :" + (savedInstanceState == null));
        //如果已经登录则实例化drawer中的item，否则不创建item，等待登录线程登录成功后再修改drawer
        MyApplication myApplication = (MyApplication) getApplication();
        User user = myApplication.getCurrentUser();

        AccountHeaderBuilder headerBuilder = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState);
        if (user != null) {
            IProfile profile = new ProfileDrawerItem()
                    .withName(user.getName())
                    .withEmail(user.getEmail())
                    .withIcon(R.drawable.avatar)
                    .withIdentifier(0);
            headerBuilder.addProfiles(profile);
        }
        mAccountHeader = headerBuilder.build();

        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withTranslucentStatusBar(true)
                .withHasStableIds(true)
                .withAccountHeader(mAccountHeader)
                .withSavedInstance(savedInstanceState);

        if (user != null) {
            Log.d(TAG, "initDrawer: user != null");

            if (user.getType().equals(getString(R.string.user_type_teacher))) {
                drawerBuilder.addDrawerItems(
                    new PrimaryDrawerItem().withName(R.string.drawer_teacher_all_classes).withIdentifier(100),
                    new PrimaryDrawerItem().withName(R.string.drawer_teacher_homework).withIdentifier(101),
                    new PrimaryDrawerItem().withName(R.string.drawer_teacher_exercise).withIdentifier(102),
                    new PrimaryDrawerItem().withName(R.string.drawer_teacher_exam).withIdentifier(103),
                    new SectionDrawerItem().withName(R.string.drawer_about_account),
                    new SecondaryDrawerItem().withName(R.string.drawer_logout).withIdentifier(300).withSelectable(false)
                );
                //此处不要setSelection，因为前面有.withSavedInstance(savedInstanceState)，旋转屏幕时
                //会通过savedInstance设置保存下来的选项
            } else if (user.getType().equals(getString(R.string.user_type_student))) {
                drawerBuilder.addDrawerItems(
                    new PrimaryDrawerItem().withName(R.string.drawer_student_homework).withIdentifier(200),
                    new PrimaryDrawerItem().withName(R.string.drawer_student_exercise).withIdentifier(201),
                    new PrimaryDrawerItem().withName(R.string.drawer_student_exam).withIdentifier(202),
                    new SectionDrawerItem().withName(R.string.drawer_about_account),
                    new PrimaryDrawerItem().withName(R.string.drawer_logout).withIdentifier(300).withSelectable(false)
                );
            }
        } else {
            Log.d(TAG, "initDrawer: user == null");
        }

        drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                if (drawerItem == null) {
                    return false;
                }

                // TODO: 17-6-9 delete
                Toast.makeText(getApplication(), ((Nameable)drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();

                Fragment fragment = null;
                int fragmentTitleId = -1;
                if (drawerItem.getIdentifier() == 100) {            //老师-所有班级
                    fragmentTitleId = R.string.drawer_teacher_all_classes;
                    fragment = MyClassFragment.newInstance();
                } else if (drawerItem.getIdentifier() == 101) {     //老师-作业
                    fragmentTitleId = R.string.drawer_teacher_homework;
                    fragment = ExerciseListFragment.newInstance(((MyApplication)getApplication()).getCourseId(), ExerciseType.HOMEWORK);
                } else if (drawerItem.getIdentifier() == 102) {     //老师-练习
                    fragmentTitleId = R.string.drawer_teacher_exercise;
                    fragment = ExerciseListFragment.newInstance(((MyApplication)getApplication()).getCourseId(), ExerciseType.EXERCISE);
                } else if (drawerItem.getIdentifier() == 103) {     //老师-考试
                    fragmentTitleId = R.string.drawer_teacher_exam;
                    fragment = ExerciseListFragment.newInstance(((MyApplication)getApplication()).getCourseId(), ExerciseType.EXAM);
                } else if (drawerItem.getIdentifier() == 200) {     //学生-作业
                    fragmentTitleId = R.string.drawer_student_homework;
                    fragment = ExerciseListFragment.newInstance(((MyApplication)getApplication()).getCourseId(), ExerciseType.HOMEWORK);
                } else if (drawerItem.getIdentifier() == 201) {     //学生-练习
                    fragmentTitleId = R.string.drawer_student_exercise;
                    fragment = ExerciseListFragment.newInstance(((MyApplication)getApplication()).getCourseId(), ExerciseType.EXERCISE);
                } else if (drawerItem.getIdentifier() == 202) {     //学生-考试
                    fragmentTitleId = R.string.drawer_student_exam;
                    fragment = ExerciseListFragment.newInstance(((MyApplication)getApplication()).getCourseId(), ExerciseType.EXAM);
                } else if (drawerItem.getIdentifier() == 300) {     //退出登录
                    logout();
                }

                if (fragment != null) {
                    Log.d(TAG, "Replace fragment to :" + fragment);
                    //change toolbar text
                    mToolbar.setTitle(fragmentTitleId);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.activity_main_frame_container, fragment)
                            .commit();
                }
                mDrawer.closeDrawer();
                return true;
            }
        });
        mDrawer = drawerBuilder.build();
        //fragment已销毁，需要重建
        if (user != null && savedInstanceState == null) {
            setDefaultFragment(user);
        }
    }

    private void setDefaultFragment(User user) {
        Fragment fragment = null;
        int fragmentTitleId = -1;
        if (user.getType().equals(getString(R.string.user_type_teacher))) {
            Log.d(TAG, "set default fragment for teacher");
            fragment = MyClassFragment.newInstance();
            fragmentTitleId = R.string.drawer_teacher_all_classes;
        } else if (user.getType().equals(getString(R.string.user_type_student))) {
            Log.d(TAG, "set default fragment for student");
            int courseId = ((MyApplication) getApplication()).getCourseId();
            fragment = ExerciseListFragment.newInstance(courseId, ExerciseType.HOMEWORK);
            fragmentTitleId = R.string.drawer_student_homework;
        }

        if (fragment != null) {
            mToolbar.setTitle(fragmentTitleId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_container, fragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "Enter onSaveInstanceState");
        if (mDrawer != null && mAccountHeader != null){
            outState = mDrawer.saveInstanceState(outState);
            outState = mAccountHeader.saveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        }
        super.onBackPressed();
    }

    private void logout() {
        Log.i(TAG, "Logout");
        mDrawer.closeDrawer();
        SharedPreferences sp = getSharedPreferences("userInfo", Activity.MODE_PRIVATE);
        sp.edit().remove("username")
                .remove("password")
                .apply();
        MyApplication myApplication = (MyApplication) getApplication();
        myApplication.setCurrentUser(null);
        myApplication.setAuthToken(null);

        Intent intent = LoginActivity.newIntent(this);
        startActivityForResult(intent, REQUEST_LOGIN_CODE);
    }

    private class UserLoginTask extends AsyncTask<String, Void, LoginResult> {

        /**
         * 登录参数
         * @param params params[0] ==> username,
         *               params[1] ==> password
         * @return
         */
        @Override
        protected LoginResult doInBackground(String... params) {
            LoginResult result = new LoginResult();
            if (params.length < 2) {
                Log.e(TAG, "Username or password missing");
                result.setStatus(ResultStatus.AUTH_ERROR);
                return result;
            }
            String username = params[0];
            String password = params[1];
            NetworkHelper networkHelper = NetworkHelper.getInstance();
            try {
                User user = networkHelper.login(username, password);
                //login failed
                if (user == null) {
                    Log.i(TAG, "Username and password do not match");
                    result.setStatus(ResultStatus.AUTH_ERROR);
                } else {
                    Log.i(TAG, "Login success");
                    result.setStatus(ResultStatus.SUCCESS);
                    result.setUser(user);
                    //password should be stored
                    result.setPassword(password);
                }
            } catch (IOException e) {
                Log.e(TAG, "Login failed due to network error", e);
                result.setStatus(ResultStatus.NETWORK_ERROR);
            }
            return result;
        }

        @Override
        protected void onPostExecute(LoginResult loginResult) {

            if (loginResult.getStatus() == ResultStatus.SUCCESS) {
                //set user info to application context
                User user = loginResult.getUser();
                MyApplication application = (MyApplication) getApplication();
                application.setCurrentUser(user);
                String username = user.getUsername();
                String password = loginResult.getPassword();
                application.setAuthToken(Base64.encodeToString((username+":"+password).getBytes(), Base64.DEFAULT));

                setupMainView();

            } else if (loginResult.getStatus() == ResultStatus.NETWORK_ERROR){
                Toast.makeText(getApplication(), R.string.error_network_fail, Toast.LENGTH_SHORT)
                        .show();
                Intent intent = LoginActivity.newIntent(MainActivity.this);
                startActivityForResult(intent, REQUEST_LOGIN_CODE);
            } else if (loginResult.getStatus() == ResultStatus.AUTH_ERROR) {
                Toast.makeText(getApplication(), R.string.error_incorrect_password, Toast.LENGTH_SHORT)
                        .show();
                Intent intent = LoginActivity.newIntent(MainActivity.this);
                startActivityForResult(intent, REQUEST_LOGIN_CODE);
            }
        }
    }

}
