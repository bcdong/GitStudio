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
import cn.edu.nju.gitstudio.pojo.QuestionScoreInterval;
import cn.edu.nju.gitstudio.util.NetworkCallback;
import cn.edu.nju.gitstudio.util.NetworkHelper;

public class ScoreActivity extends AppCompatActivity {
    private static final String TAG = "ScoreActivity";
    private static final String EXTRA_ASSIGNMENT_ID = "ScoreActivity.assignmentId";
    private static final String EXTRA_ASSIGNMENT_TITLE = "ScoreActivity.assignmentTitle";
    private static final String BUNDLE_SCORE_RESULTS = "ScoreActivity.mScoreResults";

    public static Intent newIntent(Context packageContext, int assignmentId, String assignmentTitle){
        Intent intent = new Intent(packageContext, ScoreActivity.class);
        intent.putExtra(EXTRA_ASSIGNMENT_ID, assignmentId);
        intent.putExtra(EXTRA_ASSIGNMENT_TITLE, assignmentTitle);
        return intent;
    }

    @BindView(R.id.activity_score_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_score_recycler_view) RecyclerView mRecyclerView;

    private QuestionScoreInterval[] mScoreResults = new QuestionScoreInterval[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        String toolbarTitle = getIntent().getStringExtra(EXTRA_ASSIGNMENT_TITLE) + "的结果分析";
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        if (savedInstanceState == null || !savedInstanceState.containsKey(BUNDLE_SCORE_RESULTS)) {
            Log.d(TAG, "onCreate & do not has previous score results");
            updateUI();
            //发起网络请求
            int assignmentId = getIntent().getIntExtra(EXTRA_ASSIGNMENT_ID, 1);
            NetworkHelper.getInstance().asyncGetScoreResult(this, assignmentId, new NetworkCallback<QuestionScoreInterval>() {
                @Override
                public void onGetSuccess(QuestionScoreInterval[] resultList) {
                    mScoreResults = resultList;
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
                            Toast.makeText(ScoreActivity.this, R.string.error_network_fail, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            });
        } else {
            Log.d(TAG, "onCreate & has savedInstance");
        }
    }

    private void updateUI(){
        Log.d(TAG, "updateUI --> mScoreResults length: " + mScoreResults.length);
        mRecyclerView.setAdapter(new ScoreAdapter(mScoreResults));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putSerializable(BUNDLE_SCORE_RESULTS, mScoreResults);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        mScoreResults = (QuestionScoreInterval[]) savedInstanceState.getSerializable(BUNDLE_SCORE_RESULTS);
        updateUI();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class ScoreHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.score_item_question_title) TextView questionTitleTextView;
        @BindView(R.id.score_item_0_59) TextView people_0_59;
        @BindView(R.id.score_item_60_79) TextView people_60_79;
        @BindView(R.id.score_item_80_89) TextView people_80_89;
        @BindView(R.id.score_item_90_100) TextView people_90_100;

        public ScoreHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindScore(QuestionScoreInterval scoreResult){
            questionTitleTextView.setText(scoreResult.getTitle());
            int[] peopleCount = scoreResult.getPeopleCount();
            if (peopleCount.length >= 4){
                people_0_59.setText(String.valueOf(peopleCount[0]));
                people_60_79.setText(String.valueOf(peopleCount[1]));
                people_80_89.setText(String.valueOf(peopleCount[2]));
                people_90_100.setText(String.valueOf(peopleCount[3]));
            }
        }
    }

    private class ScoreAdapter extends RecyclerView.Adapter<ScoreHolder>{
        private QuestionScoreInterval[] mPeopleCount;

        ScoreAdapter(QuestionScoreInterval[] peopleCount) {
            mPeopleCount = peopleCount;
        }

        @Override
        public ScoreHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ScoreActivity.this);
            View view = inflater.inflate(R.layout.activity_score_item, parent, false);
            return new ScoreHolder(view);
        }

        @Override
        public void onBindViewHolder(ScoreHolder holder, int position) {
            holder.bindScore(mPeopleCount[position]);
        }

        @Override
        public int getItemCount() {
            return mPeopleCount.length;
        }
    }
}
