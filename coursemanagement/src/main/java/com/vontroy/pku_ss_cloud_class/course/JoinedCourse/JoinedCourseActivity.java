package com.vontroy.pku_ss_cloud_class.course.JoinedCourse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

/**
 * Created by vontroy on 16-11-18.
 */

public class JoinedCourseActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("我的课程");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        JoinedCourseFragment joinedCourseFragment = (JoinedCourseFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (joinedCourseFragment == null) {
            joinedCourseFragment = JoinedCourseFragment.newInstance().newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    joinedCourseFragment, R.id.contentFrame);
        }

        // Create the presenter
        new JoinedCoursePresenter(TAG, ServerImp.getInstance(), joinedCourseFragment, SchedulerProvider.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
