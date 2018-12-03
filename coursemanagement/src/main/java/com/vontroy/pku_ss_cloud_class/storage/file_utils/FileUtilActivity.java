package com.vontroy.pku_ss_cloud_class.storage.file_utils;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

/**
 * Created by vontroy on 2017/11/15.
 */

public class FileUtilActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_utils);

        FileUtilFragment fileUtilFragment = FileUtilFragment.newInstance();

        Bundle bundle = getIntent().getExtras();
        fileUtilFragment.setArguments(bundle);

        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                fileUtilFragment, R.id.frag_container);

        // Create the presenter
        new FileUtilPresenter(TAG, ServerImp.getInstance(), fileUtilFragment, SchedulerProvider.getInstance());

    }
}
