package com.vontroy.pku_ss_cloud_class.user_account.reset_password;

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
 * Created by vontroy on 2017-02-24.
 */

public class ResetPwdActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("重置密码");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        ResetPwdFragment resetPwdFragment = (ResetPwdFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (resetPwdFragment == null) {
            resetPwdFragment = ResetPwdFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    resetPwdFragment, R.id.contentFrame);
        }

        // Create the presenter
        new ResetPwdPresenter(TAG, ServerImp.getInstance(), resetPwdFragment, SchedulerProvider.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
