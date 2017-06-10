package cn.edu.nju.gitstudio;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nju.gitstudio.pojo.Exercise;
import cn.edu.nju.gitstudio.type.ExerciseType;
import cn.edu.nju.gitstudio.util.NetworkCallback;
import cn.edu.nju.gitstudio.util.NetworkHelper;

/**
 * 作业、练习、考试列表
 */

public class ExerciseListFragment extends Fragment {
    private static final String TAG = "ExerciseListFragment";
    private static final String ARG_COURSE_ID = "course_id";
    private static final String ARG_EXERCISE_TYPE = "exercise_type";

    public static ExerciseListFragment newInstance(int courseId, ExerciseType exerciseType) {
        Bundle args = new Bundle();
        args.putInt(ARG_COURSE_ID, courseId);
        args.putSerializable(ARG_EXERCISE_TYPE, exerciseType);
        ExerciseListFragment fragment = new ExerciseListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.fragment_recycler_view) RecyclerView mRecyclerView;

    private Exercise[] mExercises = new Exercise[0];
    private int mCourseId;
    private ExerciseType mExerciseType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourseId = getArguments().getInt(ARG_COURSE_ID);
        mExerciseType = (ExerciseType) getArguments().getSerializable(ARG_EXERCISE_TYPE);
        setRetainInstance(true);

        NetworkHelper.getInstance().asyncGetExercise(getActivity(), mCourseId, mExerciseType, new NetworkCallback<Exercise>() {
            @Override
            public void onGetSuccess(Exercise[] resultList) {
                mExercises = resultList;
                ExerciseListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }

            @Override
            public void onGetFail(Exception ex) {
                ExerciseListFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.error_network_fail, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    private void updateUI() {
        if (isAdded()) {
            mRecyclerView.setAdapter(new ExerciseAdapter(mExercises));
        }
    }

    class ExerciseHolder extends RecyclerView.ViewHolder {
        private Exercise mExercise;
        @BindView(R.id.exercise_item_title) TextView mTitleView;
        @BindView(R.id.exercise_item_start_time) TextView mStartTimeView;
        @BindView(R.id.exercise_item_end_time) TextView mEndTimeView;

        public ExerciseHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindExercise(Exercise exercise) {
            mExercise = exercise;
            mTitleView.setText(exercise.getTitle());
            String startTime = mExercise.getStartAt();
            String endTime = mExercise.getEndAt();
            //网络获取的时间格式为： 2017-04-25 16:22:47.0，需要去掉最后的 .0
            mStartTimeView.setText(startTime.substring(0, startTime.length()-2));
            mEndTimeView.setText(endTime.substring(0, endTime.length()-2));
        }
    }

    class ExerciseAdapter extends RecyclerView.Adapter<ExerciseHolder> {
        private Exercise[] mExercises;

        public ExerciseAdapter(Exercise[] exercises) {
            mExercises = exercises;
        }

        @Override
        public ExerciseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.fragment_exercise_item, parent, false);
            return new ExerciseHolder(view);
        }

        @Override
        public void onBindViewHolder(ExerciseHolder holder, int position) {
            Exercise exercise = mExercises[position];
            holder.bindExercise(exercise);
        }

        @Override
        public int getItemCount() {
            return mExercises.length;
        }
    }
}
