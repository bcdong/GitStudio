package cn.edu.nju.gitstudio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nju.gitstudio.pojo.User;
import cn.edu.nju.gitstudio.util.NetworkCallback;
import cn.edu.nju.gitstudio.util.NetworkHelper;

public class StudentListActivity extends AppCompatActivity {
    private static final String TAG = "StudentListActivity";
    private static final String EXTRA_CLASS_NAME = "StudentListActivity.MyClassName";
    private static final String EXTRA_GROUP_ID = "StudentListActivity.GroupId";
    private static final String BUNDLE_STUDENTS = "StudentListActivity.mUsers";

    /**
     * 创建班级学生列表intent
     * @param packageContext
     * @param myClassName 班级名称，用于在toolbar上显示
     * @return
     */
    public static Intent newIntent(Context packageContext, String myClassName, int groupId) {
        Intent intent = new Intent(packageContext, StudentListActivity.class);
        intent.putExtra(EXTRA_CLASS_NAME, myClassName);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        return intent;
    }

    @BindView(R.id.activity_student_list_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_student_list_recycler_view) RecyclerView mRecyclerView;

    private User[] mStudents = new User[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        //设置toolbar标题
        String myClassName = getIntent().getStringExtra(EXTRA_CLASS_NAME);
        getSupportActionBar().setTitle(myClassName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_STUDENTS)) {
            Log.d(TAG, "onCreate & do not has previous students");
            updateUI();

            //发起网络请求
            int groupId = getIntent().getIntExtra(EXTRA_GROUP_ID, 1);
            NetworkHelper.getInstance().asyncGetStudent(this, groupId, new NetworkCallback<User>() {
                @Override
                public void onGetSuccess(User[] resultList) {
                    mStudents = resultList;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                }

                @Override
                public void onGetFail(Exception ex) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StudentListActivity.this, R.string.error_network_fail, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            });
        } else {
            Log.d(TAG, "onCreate & has savedInstance");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putSerializable(BUNDLE_STUDENTS, mStudents);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        mStudents = (User[]) savedInstanceState.getSerializable(BUNDLE_STUDENTS);
        updateUI();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateUI() {
        mRecyclerView.setAdapter(new StudentListAdapter(mStudents));
    }

    class StudentHolder extends RecyclerView.ViewHolder {
        private User mStudent;
        @BindView(android.R.id.text1) TextView mNameView;
        @BindView(android.R.id.text2) TextView mNumberView;

        StudentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindStudent(User user) {
            mStudent = user;
            mNameView.setText(user.getName());
            String numStr = "学号：" + mStudent.getNumber();
            mNumberView.setText(numStr);
        }
    }

    private class StudentListAdapter extends RecyclerView.Adapter<StudentHolder> {
        private User[] mUsers;

        StudentListAdapter(User[] users) {
            mUsers = users;
        }

        @Override
        public StudentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(StudentListActivity.this);
            View view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            return new StudentHolder(view);
        }

        @Override
        public void onBindViewHolder(StudentHolder holder, int position) {
            holder.bindStudent(mUsers[position]);
        }

        @Override
        public int getItemCount() {
            return mUsers.length;
        }
    }
}
