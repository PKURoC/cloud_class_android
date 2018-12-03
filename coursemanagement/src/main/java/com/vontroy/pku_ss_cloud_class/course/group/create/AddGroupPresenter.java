package com.vontroy.pku_ss_cloud_class.course.group.create;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.data.GroupResult;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.content.ContentValues.TAG;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-16.
 */

public class AddGroupPresenter implements AddGroupContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final AddGroupContract.View mAddGroupView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public AddGroupPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                             @NonNull AddGroupContract.View view,
                             @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mAddGroupView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mAddGroupView.setPresenter(this);
    }

    @Override
    public void addGroup(@NonNull Map params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.createGroup, params, GroupResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<GroupResult>() {
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
                    public void onNext(GroupResult groupResult) {
                        if (groupResult.getCode().equals("0")) {
                            mAddGroupView.createGroupSuccess();
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void subscribe() {
        Log.d(TAG, "subscribe: ");
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }


}
