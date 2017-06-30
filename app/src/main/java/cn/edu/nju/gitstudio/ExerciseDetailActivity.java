package cn.edu.nju.gitstudio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nju.gitstudio.pojo.Exercise;
import cn.edu.nju.gitstudio.pojo.User;

public class ExerciseDetailActivity extends AppCompatActivity {
    private static final String TAG = "ExerciseDetailActivity";
    private static final String EXTRA_EXERCISE = "ExerciseDetailActivity.mExercise";

    public static Intent newIntent(Context packageContext, Exercise exercise) {
        Intent intent = new Intent(packageContext, ExerciseDetailActivity.class);
        intent.putExtra(EXTRA_EXERCISE, exercise);
        return intent;
    }

    private Exercise mExercise;
    @BindView(R.id.activity_exercise_detail_toolbar) Toolbar mToolbar;
    @BindView(R.id.activity_exercise_detail_description_text) TextView mDescriptionTextView;
    @BindView(R.id.activity_exercise_detail_time_text) TextView mTimeTextView;
    @BindView(R.id.activity_exercise_detail_status_text) TextView mStatusTextView;
    @BindView(R.id.activity_exercise_detail_question_button) Button mQuestionButton;
    @BindView(R.id.activity_exercise_detail_analyze_button) Button mAnalyzeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);
        ButterKnife.bind(this);

        mExercise = (Exercise) getIntent().getSerializableExtra(EXTRA_EXERCISE);

        updateUI();
        mQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = QuestionListActivity.newIntent(ExerciseDetailActivity.this, mExercise.getId(), mExercise.getTitle(), mExercise.getQuestions());
                startActivity(intent);
            }
        });
        setupAnalyzeButton();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     *  根据是老师还是学生设置按钮内容和监听不同
     */
    private void setupAnalyzeButton() {

        MyApplication myApplication = (MyApplication) getApplication();
        User currentUser = myApplication.getCurrentUser();
        if (currentUser.getType().equals(getString(R.string.user_type_teacher))) {  //老师
            Log.i(TAG, "set analyse button for teacher");
            mAnalyzeButton.setText(R.string.check_exercise_score);
            mAnalyzeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ScoreActivity.newIntent(ExerciseDetailActivity.this, mExercise.getId(), mExercise.getTitle());
                    startActivity(intent);
                }
            });

        } else {        //学生
            Log.i(TAG, "set analyse button for student");
            mAnalyzeButton.setText(R.string.check_exercise_analyze);
            mAnalyzeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = TestCaseActivity.newIntent(ExerciseDetailActivity.this, mExercise.getId(), mExercise.getTitle());
                    startActivity(intent);
                }
            });
        }

        if (!mExercise.getStatus().equals("analyzingFinish")) {
            mAnalyzeButton.setEnabled(false);
        }
    }

    private void updateUI() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mExercise.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDescriptionTextView.setText(mExercise.getDescription());
        String startTime = mExercise.getStartAt();
        String endTime = mExercise.getEndAt();
        String time = startTime.substring(0, startTime.length()-2) + " ~ " + endTime.substring(0, endTime.length()-2);
        mTimeTextView.setText(time);
        mStatusTextView.setText(parseExerciseStatus(mExercise.getStatus()));
    }

    /**
     "newly"|          //新建态
     "initing"|        //正在初始化
     "initFail"|       //初始化失败
     "initSuccess"|    //初始化成功
     "ongoing"|        //考试正在进行
     "timeup"|         //考试时间到
     "analyzing"|      //正在分析结果
     "analyzingFinish" //结果分析完毕
     * @param statusStr
     * @return
     */
    private int parseExerciseStatus(String statusStr) {
        int resId = 0;
        if (statusStr.equals("newly")) {
            resId = R.string.exercise_status_newly;
        } else if (statusStr.equals("initing")) {
            resId = R.string.exercise_status_initing;
        } else if (statusStr.equals("initFail")) {
            resId = R.string.exercise_status_initFail;
        } else if (statusStr.equals("initSuccess")) {
            resId = R.string.exercise_status_initSuccess;
        } else if (statusStr.equals("ongoing")) {
            resId = R.string.exercise_status_ongoing;
        } else if (statusStr.equals("timeup")) {
            resId = R.string.exercise_status_timeup;
        } else if (statusStr.equals("analyzing")) {
            resId = R.string.exercise_status_analyzing;
        } else {
            resId = R.string.exercise_status_analyzingFinish;
        }
        return resId;
    }
}
