package com.vontroy.pku_ss_cloud_class.course.group.detail;

import android.os.Bundle;

import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

/**
 * Created by vontroy on 2016-12-28.
 */

public class GroupDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        GroupDetailFragment groupDetailFragment = (GroupDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (groupDetailFragment == null) {
            groupDetailFragment = GroupDetailFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    groupDetailFragment, R.id.contentFrame);
        }

        Bundle bundle = this.getIntent().getExtras();
        groupDetailFragment.setArguments(bundle);

        // Create the presenter
        new GroupDetailPresenter(TAG, ServerImp.getInstance(), groupDetailFragment, SchedulerProvider.getInstance());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
