package com.vontroy.pku_ss_cloud_class.storage.crypto;

import android.os.Bundle;

import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

/**
 * Created by vontroy on 2017/11/15.
 */

public class CryptoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto);

        CryptoFragment cryptoFragment = CryptoFragment.newInstance();

        Bundle bundle = getIntent().getExtras();
        cryptoFragment.setArguments(bundle);

        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                cryptoFragment, R.id.frag_container);

        // Create the presenter
        new CryptoPresenter(TAG, ServerImp.getInstance(), cryptoFragment, SchedulerProvider.getInstance());

    }
}
