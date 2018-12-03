package com.vontroy.pku_ss_cloud_class.course;

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
 * Created by LinkedME06 on 16/11/9.
 */

public class CourseActivity extends BaseActivity {
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

        CourseFragment courseFragment = (CourseFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (courseFragment == null) {
            courseFragment = CourseFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    courseFragment, R.id.contentFrame);
        }

        courseFragment.setArguments(bundle);

        // Create the presenter
        new CoursePresenter(TAG, ServerImp.getInstance(), courseFragment, SchedulerProvider.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

