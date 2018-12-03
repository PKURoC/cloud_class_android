package com.vontroy.pku_ss_cloud_class.storage;

import android.support.annotation.NonNull;

import com.google.common.base.Preconditions;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by vontroy on 16-11-17.
 */

public class StoragePresenter implements StorageContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final StorageContract.View mStorageView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public StoragePresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                            @NonNull StorageContract.View regView,
                            @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = Preconditions.checkNotNull(serverImp, "serverImp cannot be null!");
        mStorageView = Preconditions.checkNotNull(regView, "regView cannot be null!");
        mSchedulerProvider = Preconditions.checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mStorageView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        //此处为页面打开后开始加载数据时调用的方法
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
