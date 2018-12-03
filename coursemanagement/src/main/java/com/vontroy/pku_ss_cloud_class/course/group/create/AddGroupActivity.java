package com.vontroy.pku_ss_cloud_class.course.group.create;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

public class AddGroupActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle("创建小组");

        AddGroupFragment addGroupFragment = (AddGroupFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (addGroupFragment == null) {
            addGroupFragment = AddGroupFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addGroupFragment, R.id.contentFrame);
        }

        Bundle bundle = this.getIntent().getExtras();
        addGroupFragment.setArguments(bundle);

        // Create the presenter
        new AddGroupPresenter(TAG, ServerImp.getInstance(), addGroupFragment, SchedulerProvider.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
