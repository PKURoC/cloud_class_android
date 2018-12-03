package com.vontroy.pku_ss_cloud_class.storage.crypto;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.storage.crypto.decrypt.DecryptFragment;
import com.vontroy.pku_ss_cloud_class.storage.crypto.decrypt.DecryptPresenter;
import com.vontroy.pku_ss_cloud_class.storage.crypto.encrypt.EncryptFragment;
import com.vontroy.pku_ss_cloud_class.storage.crypto.encrypt.EncryptPresenter;
import com.vontroy.pku_ss_cloud_class.storage.crypto.states.StatesFragment;
import com.vontroy.pku_ss_cloud_class.storage.crypto.states.StatesPresenter;
import com.vontroy.pku_ss_cloud_class.storage.file_utils.download.FileDownloadFragment;
import com.vontroy.pku_ss_cloud_class.storage.file_utils.download.FileDownloadPresenter;
import com.vontroy.pku_ss_cloud_class.storage.file_utils.upload.FileUploadFragment;
import com.vontroy.pku_ss_cloud_class.storage.file_utils.upload.FileUploadPresenter;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;


/**
 * Created by vontroy on 2017/11/1.
 */

public class CryptoFragment extends Fragment implements CryptoContract.View{

    private View mRootView;
    private CryptoContract.Presenter mPresenter;
    private int currentFragment;

    public static CryptoFragment newInstance() {

        return new CryptoFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle bundle = new Bundle();

        if (mRootView == null) {
            Log.e("666", "StorageFragment");
            mRootView = inflater.inflate(R.layout.crypto_frag, container, false);
        }

        setHasOptionsMenu(true);

        TabLayout tabLayout = (TabLayout) mRootView.findViewById(R.id.crypto_tab);
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
            StatesFragment statesFragment = StatesFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getChildFragmentManager(), statesFragment, R.id.crypto_content);

            new StatesPresenter("StorageAllFragment", ServerImp.getInstance(), statesFragment, SchedulerProvider.getInstance());
        } else if (id == 1) {
            EncryptFragment encryptFragment = EncryptFragment.newInstance();

            Bundle bundle = getArguments();
            encryptFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(getChildFragmentManager(), encryptFragment, R.id.crypto_content);

            new EncryptPresenter("StorageDocFragment", ServerImp.getInstance(), encryptFragment, SchedulerProvider.getInstance());
        } else if (id == 2) {
            DecryptFragment decryptFragment = DecryptFragment.newInstance();

            Bundle bundle = getArguments();
            decryptFragment.setArguments(bundle);

            ActivityUtils.addFragmentToActivity(getChildFragmentManager(), decryptFragment, R.id.crypto_content);

            new DecryptPresenter("StorageDocFragment", ServerImp.getInstance(), decryptFragment, SchedulerProvider.getInstance());
        }
    }

    @Override
    public void setPresenter(CryptoContract.Presenter presenter) {

    }
}
