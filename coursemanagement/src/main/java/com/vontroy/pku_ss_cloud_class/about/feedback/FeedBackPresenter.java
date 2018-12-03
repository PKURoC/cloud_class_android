package com.vontroy.pku_ss_cloud_class.about.feedback;

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

import static android.content.ContentValues.TAG;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 2017/2/16.
 */

public class FeedBackPresenter implements FeedBackContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final FeedBackContract.View mFeedBackView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public FeedBackPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                             @NonNull FeedBackContract.View view,
                             @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mFeedBackView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mFeedBackView.setPresenter(this);
    }

    @Override
    public void addFeedBack(@NonNull Map<String, String> params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.addFeedBack, params, BaseResult.class)
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
                        if ("0".equals(baseResult.getCode())) {
                            mFeedBackView.submitFeedBackSuccess();
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
    }
}
