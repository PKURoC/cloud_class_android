package com.vontroy.pku_ss_cloud_class.course.homework.detail;

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
 * Created by vontroy on 2016-12-30.
 */

public class JobDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle("作业详情");

        Bundle bundle = getIntent().getExtras();

        JobDetailFragment jobDetailFragment = (JobDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);


        if (jobDetailFragment == null) {
            jobDetailFragment = JobDetailFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    jobDetailFragment, R.id.contentFrame);
        }

        jobDetailFragment.setArguments(bundle);
        // Create the presenter
        new JobDetailPresenter(TAG, ServerImp.getInstance(), jobDetailFragment, SchedulerProvider.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
