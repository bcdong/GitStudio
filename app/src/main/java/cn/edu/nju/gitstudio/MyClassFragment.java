package cn.edu.nju.gitstudio;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.edu.nju.gitstudio.pojo.MyClass;
import cn.edu.nju.gitstudio.util.NetworkCallback;
import cn.edu.nju.gitstudio.util.NetworkHelper;

/**
 * 所有班级界面Fragment
 */

public class MyClassFragment extends Fragment {
    private static final String TAG = "MyClassFragment";

    public static MyClassFragment newInstance() {
        return new MyClassFragment();
    }

    @BindView(R.id.fragment_recycler_view) RecyclerView mRecyclerView;

    private MyClass[] mMyClasses = new MyClass[0];
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //转屏幕时保存实例变量，且不会再次调用oncreate（只会调用oncreateview），所以可以防止再次创建网络连接
        setRetainInstance(true);

        NetworkHelper.getInstance().asyncGetClass(getActivity(), new NetworkCallback<MyClass>() {
            @Override
            public void onGetSuccess(MyClass[] resultList) {
                Log.d(TAG, "onGetSuccess");
                mMyClasses = resultList;
                //更新视图必须在UI线程
                MyClassFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }

            @Override
            public void onGetFail(Exception ex) {
                Log.d(TAG, "onGetFail");
                //更新视图必须在UI线程
                MyClassFragment.this.getActivity().runOnUiThread(new Runnable() {
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
        Log.d(TAG, "onCreateView in MyClassFragment");
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        ButterKnife.bind(this, view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return view;
    }


    /**
     * 更新班级列表
     */
    private void updateUI() {
        if (isAdded()) {
            mRecyclerView.setAdapter(new MyClassAdapter(mMyClasses));
        }
    }

    class MyClassHolder extends RecyclerView.ViewHolder {
        private TextView mClassNameTextView;
        private MyClass mMyClass;

        public MyClassHolder(View itemView) {
            super(itemView);
            mClassNameTextView = (TextView) itemView;
            mClassNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 17-6-10 查看一个班级的所有学生，需要另一个activity
                }
            });
        }

        public void bindMyClass(MyClass myClass) {
            mMyClass = myClass;
            mClassNameTextView.setText(mMyClass.getName());
        }
    }

    class MyClassAdapter extends RecyclerView.Adapter<MyClassHolder> {
        private MyClass[] mMyClasses;

        public MyClassAdapter(MyClass[] myClasses) {
            mMyClasses = myClasses;
        }

        @Override
        public MyClassHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new MyClassHolder(view);
        }

        @Override
        public void onBindViewHolder(MyClassHolder holder, int position) {
            MyClass myClass = mMyClasses[position];
            holder.bindMyClass(myClass);
        }

        @Override
        public int getItemCount() {
            return mMyClasses.length;
        }

    }
}
