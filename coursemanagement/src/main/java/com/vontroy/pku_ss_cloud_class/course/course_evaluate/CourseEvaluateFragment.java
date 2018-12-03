package com.vontroy.pku_ss_cloud_class.course.course_evaluate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.databinding.CourseEvaluateFragmentBinding;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 10/11/17.
 */

public class CourseEvaluateFragment extends Fragment implements CourseEvaluateContract.View {
    private CourseEvaluateFragmentBinding courseEvaluateFragmentBinding;
    private CourseEvaluateContract.Presenter mPresenter;

    public static CourseEvaluateFragment newInstance() {
        return new CourseEvaluateFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.course_evaluate_fragment, container, false);
        courseEvaluateFragmentBinding.courseEvaluateSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "评分已提交", Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    @Override
    public void setPresenter(@NonNull CourseEvaluateContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
