package com.vontroy.pku_ss_cloud_class.user_account.register;

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
 * Created by vontroy on 16-11-14.
 */

public class RegActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("注册");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        RegFragment regFragment = (RegFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (regFragment == null) {
            regFragment = RegFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    regFragment, R.id.contentFrame);
        }

        // Create the presenter
        new RegPresenter(TAG, ServerImp.getInstance(), regFragment, SchedulerProvider.getInstance());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
