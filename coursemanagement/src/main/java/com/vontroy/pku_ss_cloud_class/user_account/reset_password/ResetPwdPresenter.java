package com.vontroy.pku_ss_cloud_class.user_account.reset_password;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.google.common.base.Preconditions;
import com.vontroy.pku_ss_cloud_class.data.BaseResult;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by vontroy on 2017-02-24.
 */

public class ResetPwdPresenter implements ResetPwdContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final ResetPwdContract.View mResetPwdView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public ResetPwdPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                             @NonNull ResetPwdContract.View resetPwdView,
                             @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = Preconditions.checkNotNull(serverImp, "serverImp cannot be null!");
        mResetPwdView = Preconditions.checkNotNull(resetPwdView, "regView cannot be null!");
        mSchedulerProvider = Preconditions.checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mResetPwdView.setPresenter(this);
    }

    @Override
    public void resetPassword(Map<String, String> params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.resetPwd, params, BaseResult.class)
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

                    }

                    @Override
                    public void onNext(BaseResult baseResult) {
                        if (baseResult.getCode().equals("0")) {
                            //success
                            mResetPwdView.resetPwdSuccess();
                        } else {
                            //error
                            mResetPwdView.resetPwdFailed(baseResult.getMessage());
                        }
                        //showTask(task);
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
