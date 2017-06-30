package cn.edu.nju.gitstudio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nju.gitstudio.util.NetworkCallback;
import cn.edu.nju.gitstudio.util.NetworkHelper;

public class ReadMeActivity extends AppCompatActivity {
    private static final String TAG = "ReadMeActivity";
    private static final String EXTRA_ASSIGNMENT_ID = "ReadMeActivity.assignmentId";
    private static final String EXTRA_STUDENT_ID = "ReadMeActivity.studentId";
    private static final String EXTRA_QUESTION_ID = "ReadMeActivity.questionId";
    private static final String EXTRA_QUESTION_TITLE = "ReadMeActivity.questionTitle";
    private static final String BUNDLE_READ_ME_TEXT = "ReadMeActivity.mReadMeText";

    public static Intent newIntent(Context packageContext, int assignmentId, int studentId, int questionId, String questionTitle){
        Intent intent = new Intent(packageContext, ReadMeActivity.class);
        intent.putExtra(EXTRA_ASSIGNMENT_ID, assignmentId);
        intent.putExtra(EXTRA_STUDENT_ID, studentId);
        intent.putExtra(EXTRA_QUESTION_ID, questionId);
        intent.putExtra(EXTRA_QUESTION_TITLE, questionTitle);
        return intent;
    }

    @BindView(R.id.activity_read_me_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_read_me_text) TextView mTextView;

    private String mReadMeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_me);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        String toolbarTitle = getIntent().getStringExtra(EXTRA_QUESTION_TITLE) + "的ReadMe文件";
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_READ_ME_TEXT)) {
            Log.d(TAG, "onCreate & do not has previous read me results");
            //发起网络请求
            int assignmentId = getIntent().getIntExtra(EXTRA_ASSIGNMENT_ID, 1);
            int studentId = getIntent().getIntExtra(EXTRA_STUDENT_ID, 1);
            int questionId = getIntent().getIntExtra(EXTRA_QUESTION_ID, 1);
            NetworkHelper.getInstance().asyncGetReadMe(this, assignmentId, studentId, questionId, new NetworkCallback<String>() {
                @Override
                public void onGetSuccess(String[] resultList) {
                    mReadMeText = resultList[0];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                }

                @Override
                public void onGetFail(Exception ex) {
                    Toast.makeText(ReadMeActivity.this, R.string.error_network_fail, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        } else {
            Log.d(TAG, "onCreate & has savedInstance");
        }
    }

    private void updateUI(){
        if (mReadMeText != null && !mReadMeText.isEmpty()){
            mTextView.setText(mReadMeText);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putString(BUNDLE_READ_ME_TEXT, mReadMeText);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        mReadMeText = savedInstanceState.getString(BUNDLE_READ_ME_TEXT);
        updateUI();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
