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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nju.gitstudio.pojo.TestCaseResult;
import cn.edu.nju.gitstudio.util.MyRecyclerView;
import cn.edu.nju.gitstudio.util.NetworkCallback;
import cn.edu.nju.gitstudio.util.NetworkHelper;

public class TestCaseActivity extends AppCompatActivity {
    private static final String TAG = "TestCaseActivity";
    private static final String EXTRA_ASSIGNMENT_ID = "TestCaseActivity.assignmentId";
    private static final String EXTRA_ASSIGNMENT_TITLE = "ScoreActivity.assignmentTitle";
    private static final String BUNDLE_TestCase_RESULTS = "TestCaseActivity.mTestCaseResults";

    public static Intent newIntent(Context packageContext, int assignmentId, String assignmentTitle){
        Intent intent = new Intent(packageContext, TestCaseActivity.class);
        intent.putExtra(EXTRA_ASSIGNMENT_ID, assignmentId);
        intent.putExtra(EXTRA_ASSIGNMENT_TITLE, assignmentTitle);
        return intent;
    }

    @BindView(R.id.activity_test_case_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_test_case_recycler_view) MyRecyclerView mRecyclerView;
    @BindView(R.id.activity_test_case_empty_view) TextView emptyView;

    private TestCaseResult[] mTestCaseResults = new TestCaseResult[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_case);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        String toolbarTitle = getIntent().getStringExtra(EXTRA_ASSIGNMENT_TITLE) + "的结果分析";
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setEmptyView(emptyView);

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_TestCase_RESULTS)){
            Log.d(TAG, "onCreate & do not has previous test case results");
            updateUI();
            //network request
            int assignmentId = getIntent().getIntExtra(EXTRA_ASSIGNMENT_ID, 1);
            int studentId = ((MyApplication) getApplication()).getCurrentUser().getId();
            NetworkHelper.getInstance().asyncGetTestCaseResult(this, assignmentId, studentId, new NetworkCallback<TestCaseResult>() {
                @Override
                public void onGetSuccess(TestCaseResult[] resultList) {
                    mTestCaseResults = resultList;
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
                            Toast.makeText(TestCaseActivity.this, R.string.error_network_fail, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            });
        } else {
            Log.d(TAG, "onCreate & has previous test case results");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putSerializable(BUNDLE_TestCase_RESULTS, mTestCaseResults);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        mTestCaseResults = (TestCaseResult[]) savedInstanceState.getSerializable(BUNDLE_TestCase_RESULTS);
        updateUI();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateUI(){
        mRecyclerView.setAdapter(new TestCaseResultAdapter(mTestCaseResults));
    }

    class TestCaseResultHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.test_case_item_question_title) TextView questionTitle;
        @BindView(R.id.value_compile) TextView compileValue;
        @BindView(R.id.value_tested) TextView testedValue;
        @BindView(R.id.value_score) TextView scoreValue;
        @BindView(R.id.test_case_detail_button) Button detailButton;

        private TestCaseResult testResult;

        public TestCaseResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindTestCaseResult(TestCaseResult result){
            this.testResult = result;
            questionTitle.setText(result.getQuestionTitle());
            compileValue.setText(result.isCompile_succeeded()?"通过":"未通过");
            testedValue.setText(result.isTested()?"是":"否");
            scoreValue.setText(String.valueOf(result.getScore()));
            detailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = TestCaseDetailActivity.newIntent(TestCaseActivity.this, testResult.getQuestionTitle(), testResult.getTestCases());
                    startActivity(intent);
                }
            });
        }
    }

    private class TestCaseResultAdapter extends RecyclerView.Adapter<TestCaseResultHolder>{
        private TestCaseResult[] mResults;

        TestCaseResultAdapter(TestCaseResult[] results){
            this.mResults = results;
        }

        @Override
        public TestCaseResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(TestCaseActivity.this);
            View view = inflater.inflate(R.layout.activity_test_case_item, parent, false);
            return new TestCaseResultHolder(view);
        }

        @Override
        public void onBindViewHolder(TestCaseResultHolder holder, int position) {
            holder.bindTestCaseResult(mResults[position]);
        }

        @Override
        public int getItemCount() {
            return mResults.length;
        }
    }
}
