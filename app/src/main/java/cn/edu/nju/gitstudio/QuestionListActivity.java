package cn.edu.nju.gitstudio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nju.gitstudio.pojo.Question;
import cn.edu.nju.gitstudio.util.MyRecyclerView;

public class QuestionListActivity extends AppCompatActivity {
    private static final String TAG = "QuestionListActivity";
    private static final String EXTRA_EXERCISE_ID = "EXTRA_QuestionListActivity.exerciseId";
    private static final String EXTRA_EXERCISE_TITLE = "EXTRA_QuestionListActivity.exerciseTitle";
    private static final String EXTRA_QUESTION_LIST = "EXTRA_QuestionListActivity.questionList";

    public static Intent newIntent(Context packageContext, int exerciseId, String exerciseTitle, Question[] questions){
        Intent intent = new Intent(packageContext, QuestionListActivity.class);
        intent.putExtra(EXTRA_EXERCISE_ID, exerciseId);
        intent.putExtra(EXTRA_EXERCISE_TITLE, exerciseTitle);
        intent.putExtra(EXTRA_QUESTION_LIST, questions);
        return intent;
    }

    @BindView(R.id.activity_question_list_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_question_list_recycler_view) MyRecyclerView mRecyclerView;
    @BindView(R.id.activity_question_list_empty_view) TextView emptyView;

    private Question[] mQuestions;
    private int exerciseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        String toolbarTitle = getIntent().getStringExtra(EXTRA_EXERCISE_TITLE) + "的问题列表";
        getSupportActionBar().setTitle(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mQuestions = (Question[]) getIntent().getSerializableExtra(EXTRA_QUESTION_LIST);
        exerciseId = getIntent().getIntExtra(EXTRA_EXERCISE_ID, 1);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(new QuestionListAdapter(mQuestions));
        mRecyclerView.setEmptyView(emptyView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    class QuestionHolder extends RecyclerView.ViewHolder {
        private Question mQuestion;
        @BindView(android.R.id.text1) TextView mQuestionTitle;
        @BindView(android.R.id.text2) TextView mQuestionDes;

        QuestionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看readme文件

                }
            });
        }

        void bindQuestion(Question question) {
            mQuestion = question;
            mQuestionTitle.setText(mQuestion.getTitle());
            mQuestionDes.setText(mQuestion.getDescription());
        }
    }

    private class QuestionListAdapter extends RecyclerView.Adapter<QuestionHolder>{
        private Question[] mAdapterQuestions;

        public QuestionListAdapter(Question[] questions) {
            mAdapterQuestions = questions;
        }

        @Override
        public QuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(QuestionListActivity.this);
            View view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            return new QuestionHolder(view);
        }

        @Override
        public void onBindViewHolder(QuestionHolder holder, int position) {
            holder.bindQuestion(mAdapterQuestions[position]);
        }

        @Override
        public int getItemCount() {
            return mAdapterQuestions.length;
        }
    }
}
