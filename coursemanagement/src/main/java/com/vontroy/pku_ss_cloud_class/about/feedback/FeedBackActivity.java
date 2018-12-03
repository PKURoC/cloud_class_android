package com.vontroy.pku_ss_cloud_class.about.feedback;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

/**
 * Created by vontroy on 2017/2/16.
 */

public class FeedBackActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle("提交反馈");

        FeedBackFragment feedBackFragment = (FeedBackFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (feedBackFragment == null) {
            feedBackFragment = FeedBackFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    feedBackFragment, R.id.contentFrame);
        }

        Bundle bundle = this.getIntent().getExtras();
        feedBackFragment.setArguments(bundle);

        // Create the presenter
        new FeedBackPresenter(TAG, ServerImp.getInstance(), feedBackFragment, SchedulerProvider.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
