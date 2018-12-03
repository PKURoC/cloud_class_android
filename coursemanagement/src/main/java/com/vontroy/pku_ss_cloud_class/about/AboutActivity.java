package com.vontroy.pku_ss_cloud_class.about;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.about.feedback.FeedBackActivity;
import com.vontroy.pku_ss_cloud_class.adapter.AboutItemAdapter;
import com.vontroy.pku_ss_cloud_class.entry.AboutInfo;
import com.vontroy.pku_ss_cloud_class.update.CheckVersionInfo;

import java.util.ArrayList;

/**
 * Created by vontroy on 16-11-17.
 */

public class AboutActivity extends BaseActivity {
    private ListView about_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("关于软件");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        about_list = (ListView) findViewById(R.id.about_list);

        final ArrayList<AboutInfo> aboutInfos = new ArrayList<>();

        aboutInfos.add(new AboutInfo("功能介绍"));
        aboutInfos.add(new AboutInfo("系统通知"));
        aboutInfos.add(new AboutInfo("反馈bug"));
        aboutInfos.add(new AboutInfo("检查更新"));

        AboutItemAdapter adapter = new AboutItemAdapter(this, aboutInfos);

        TextView versionName = (TextView) findViewById(R.id.app_info_version_name);

        versionName.setText(getCurrentVersionName());

        about_list.setAdapter(adapter);
        about_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                switch (i) {
                    case 0: {
                        intent = new Intent(AboutActivity.this, FunctionIntroductionActivity.class);
                        startActivity(intent);
                        break;
                    }

                    case 1: {
                        intent = new Intent(AboutActivity.this, SystemNotificationActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 2: {
                        intent = new Intent(AboutActivity.this, FeedBackActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 3: {
                        new CheckVersionInfo(AboutActivity.this, true).execute();
                        break;
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public String getCurrentVersionName() {
        try {
            return this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return "";
    }
}
