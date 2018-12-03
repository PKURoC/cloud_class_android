package com.vontroy.pku_ss_cloud_class.course.group.detail;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.data.BaseResult;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 2016-12-28.
 */

public class GroupDetailPresenter implements GroupDetailContract.Presenter {

    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final GroupDetailContract.View mAddGroupView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public GroupDetailPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                                @NonNull GroupDetailContract.View view,
                                @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mAddGroupView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mAddGroupView.setPresenter(this);
    }

    @Override
    public void joinGroup(Map params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.joinGroup, params, BaseResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<BaseResult>() {
                    @Override
                    public void onCompleted() {
                        //mTaskDetailView.setLoadingIndicator(false);
                        Log.d("ddd", "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("ddd", "onError: ");
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(BaseResult baseResult) {
                        Log.d("ddd", "onNext: ");
                        if (baseResult.getCode().equals("0")) {
                            mAddGroupView.joinGroupSuccess();
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
