package com.vontroy.pku_ss_cloud_class.storage.file_utils;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.storage.file_utils.download.FileDownloadFragment;
import com.vontroy.pku_ss_cloud_class.storage.file_utils.download.FileDownloadPresenter;
import com.vontroy.pku_ss_cloud_class.storage.file_utils.upload.FileUploadFragment;
import com.vontroy.pku_ss_cloud_class.storage.file_utils.upload.FileUploadPresenter;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;


/**
 * Created by vontroy on 2017/11/1.
 */

public class FileUtilFragment extends Fragment implements FileUtilContract.View {

    private View mRootView;
    private FileUtilContract.Presenter mPresenter;
    private int currentFragment;

    public static FileUtilFragment newInstance() {

        return new FileUtilFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle bundle = new Bundle();

        if (mRootView == null) {
            Log.e("666", "StorageFragment");
            mRootView = inflater.inflate(R.layout.upload_frag, container, false);
        }

        setHasOptionsMenu(true);

        TabLayout tabLayout = (TabLayout) mRootView.findViewById(R.id.upload_tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int id = tab.getPosition();
                switchFragment(id);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        currentFragment = tabLayout.getSelectedTabPosition();
        switchFragment(currentFragment);

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    private void switchFragment(int id) {
        if (id == 0) {
            FileUploadFragment fileUploadFragment = FileUploadFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getChildFragmentManager(), fileUploadFragment, R.id.file_util_content);

            new FileUploadPresenter("StorageAllFragment", ServerImp.getInstance(), fileUploadFragment, SchedulerProvider.getInstance());
        } else if (id == 1) {
            FileDownloadFragment fileDownloadFragment = FileDownloadFragment.newInstance(null);

            Bundle bundle = getArguments();
            fileDownloadFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(getChildFragmentManager(), fileDownloadFragment, R.id.file_util_content);

            new FileDownloadPresenter("StorageDocFragment", ServerImp.getInstance(), fileDownloadFragment, SchedulerProvider.getInstance());
        }
    }

    @Override
    public void setPresenter(FileUtilContract.Presenter presenter) {

    }
}
