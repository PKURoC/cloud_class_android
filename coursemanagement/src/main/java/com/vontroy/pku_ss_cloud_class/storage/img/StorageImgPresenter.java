package com.vontroy.pku_ss_cloud_class.storage.img;

import android.support.annotation.NonNull;

import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-17.
 */

public class StorageImgPresenter implements StorageImgContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final StorageImgContract.View mStorageImgView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public StorageImgPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                               @NonNull StorageImgContract.View view,
                               @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mStorageImgView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mStorageImgView.setPresenter(this);
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
