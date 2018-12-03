package com.vontroy.pku_ss_cloud_class.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.common.base.Strings;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.curriculum.CurriculumFragment;
import com.vontroy.pku_ss_cloud_class.guide.GuideActivity;
import com.vontroy.pku_ss_cloud_class.home.HomeFragment;
import com.vontroy.pku_ss_cloud_class.home.HomePresenter;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.profile.ProfileFragment;
import com.vontroy.pku_ss_cloud_class.profile.ProfilePresenter;
import com.vontroy.pku_ss_cloud_class.storage.StorageFragment;
import com.vontroy.pku_ss_cloud_class.storage.StoragePresenter;
import com.vontroy.pku_ss_cloud_class.update.CheckVersionInfo;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(this);
        String token = getToken.getString("token", "");
        Log.d(TAG, "onCreate: " + token);

        String isFirstOpen = getToken.getString("FIRST", "");
        if (Strings.isNullOrEmpty(isFirstOpen) || !"deleted".equals(isFirstOpen)) {
            SharedPreferences.Editor editor = getToken.edit();
            editor.putString("FIRST", "false");
            editor.commit();
            new CheckVersionInfo(MainActivity.this, true, true).execute();
        }

        String lastUpdateDate = getToken.getString("LastUpdate", "");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date checkoutUpdateDate = new Date();
        String currentDate = simpleDateFormat.format(checkoutUpdateDate);

        if (Strings.isNullOrEmpty(lastUpdateDate) || !currentDate.equals(lastUpdateDate)) {
            new CheckVersionInfo(MainActivity.this, true, "Daily").execute();
            SharedPreferences.Editor editor = getToken.edit();
            editor.putString("LastUpdate", currentDate);
            editor.commit();
        }

        boolean loginState = !Strings.isNullOrEmpty(token);
        if (!loginState) {
            Intent intent_test = new Intent(this, GuideActivity.class);
            startActivity(intent_test);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("首页");
        setSupportActionBar(toolbar);
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                // TODO: 16-11-15 xinyu 需要完善
                if (tabId == R.id.tab_home) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                    toolbar.setTitle("首页");
                    HomeFragment homeFragment = HomeFragment.newInstance();

                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                            homeFragment, R.id.contentFrame);

                    // Create the presenter
                    new HomePresenter(TAG, ServerImp.getInstance(), homeFragment, SchedulerProvider.getInstance());
                } else if (tabId == R.id.tab_curriculum) {

                    toolbar.setTitle("课表");

                    CurriculumFragment joinedCourseFragment = CurriculumFragment.newInstance();

                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), joinedCourseFragment, R.id.contentFrame);
                    // new CurriculumFragment(TAG, ServerImp.getInstance(), joinedCourseFragment, SchedulerProvider.getInstance());

                } else if (tabId == R.id.tab_storage) {
                    toolbar.setTitle("云盘");
                    StorageFragment storageFragment = StorageFragment.newInstance();

                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), storageFragment, R.id.contentFrame);
                    new StoragePresenter(TAG, ServerImp.getInstance(), storageFragment, SchedulerProvider.getInstance());
                } else if (tabId == R.id.tab_profile) {
                    toolbar.setTitle("我的");
                    ProfileFragment profileFragment = ProfileFragment.newInstance();

                    ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), profileFragment, R.id.contentFrame);
                    new ProfilePresenter(TAG, ServerImp.getInstance(), profileFragment, SchedulerProvider.getInstance());
                }
            }
        });

    }
}
