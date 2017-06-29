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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nju.gitstudio.pojo.TestCase;
import cn.edu.nju.gitstudio.util.MyRecyclerView;

public class TestCaseDetailActivity extends AppCompatActivity {
    private static final String TAG = "TestCaseDetailActivity";
    private static final String EXTRA_QUESTION_TITLE = "EXTRA_TestCaseDetailActivity.questionTitle";
    private static final String EXTRA_TESTCASES = "EXTRA_TestCaseDetailActivity.mTestCases";

    public static Intent newIntent(Context packageContext, String questionTitle, TestCase[] testCases){
        Intent intent = new Intent(packageContext, TestCaseDetailActivity.class);
        intent.putExtra(EXTRA_QUESTION_TITLE, questionTitle);
        intent.putExtra(EXTRA_TESTCASES, testCases);
        return intent;
    }

    @BindView(R.id.activity_test_case_detail_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_test_case_detail_recycler_view) MyRecyclerView mRecyclerView;
    @BindView(R.id.activity_test_case_detail_empty_view) TextView emptyView;

    private TestCase[] mTestCases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_case_detail);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        String toolbarTitle = getIntent().getStringExtra(EXTRA_QUESTION_TITLE) + "的测试用例";
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mTestCases = (TestCase[]) getIntent().getSerializableExtra(EXTRA_TESTCASES);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TestCaseAdapter(mTestCases));
        mRecyclerView.setEmptyView(emptyView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class TestCaseHolder extends RecyclerView.ViewHolder{
        private TextView testCaseTextView;

        public TestCaseHolder(View itemView) {
            super(itemView);
            testCaseTextView = (TextView) itemView;
        }

        void bindTestCase(TestCase testCase){
            String s = testCase.getName() + "  -->  ";
            if (testCase.isPassed()){
                s += "通过";
            } else {
                s += "未通过";
            }
            testCaseTextView.setText(s);
        }
    }

    private class TestCaseAdapter extends RecyclerView.Adapter<TestCaseHolder>{
        private TestCase[] mTestCases;

        public TestCaseAdapter(TestCase[] testCases) {
            mTestCases = testCases;
        }

        @Override
        public TestCaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(TestCaseDetailActivity.this);
            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new TestCaseHolder(view);
        }

        @Override
        public void onBindViewHolder(TestCaseHolder holder, int position) {
            holder.bindTestCase(mTestCases[position]);
        }

        @Override
        public int getItemCount() {
            return mTestCases.length;
        }
    }
}
