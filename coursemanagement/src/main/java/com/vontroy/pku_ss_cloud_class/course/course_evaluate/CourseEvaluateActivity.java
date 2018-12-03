package com.vontroy.pku_ss_cloud_class.course.course_evaluate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

/**
 * Created by vontroy on 10/11/17.
 */

public class CourseEvaluateActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Bundle bundle = getIntent().getExtras();

        CourseInfo courseInfo = (CourseInfo) bundle.getSerializable("course_info");

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle(courseInfo.getCourseName());

        CourseEvaluateFragment courseEvaluateFragment = (CourseEvaluateFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (courseEvaluateFragment == null) {
            courseEvaluateFragment = CourseEvaluateFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    courseEvaluateFragment, R.id.contentFrame);
        }

        courseEvaluateFragment.setArguments(bundle);

        // Create the presenter
        new CourseEvaluatePresenter(TAG, ServerImp.getInstance(), courseEvaluateFragment, SchedulerProvider.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
