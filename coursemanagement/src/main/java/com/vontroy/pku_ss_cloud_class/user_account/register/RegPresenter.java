package com.vontroy.pku_ss_cloud_class.user_account.register;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.google.common.base.Preconditions;
import com.vontroy.pku_ss_cloud_class.data.RegResult;
import com.vontroy.pku_ss_cloud_class.data.Student;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.Decrypt;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by vontroy on 16-11-14.
 */

public class RegPresenter implements RegContract.Presenter {

    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final RegContract.View mRegView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public RegPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                        @NonNull RegContract.View regView,
                        @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = Preconditions.checkNotNull(serverImp, "serverImp cannot be null!");
        mRegView = Preconditions.checkNotNull(regView, "regView cannot be null!");
        mSchedulerProvider = Preconditions.checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mRegView.setPresenter(this);
    }


    @Override
    public void reg(@NonNull Student student) {
        Map<String, String> params = new HashMap<>();
        params.put("sid", student.getSid());
        params.put("password", Decrypt.MD5(student.getPassword()));
        params.put("nick", student.getNick());

        Log.d("debug", "reg: " + params.toString());
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.reg, params, RegResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<RegResult>() {
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
                    public void onNext(RegResult regResult) {
                        Log.d("ddd", "onNext: " + regResult.getCode() + "ddd" + regResult.getToken());
                        if (regResult.getCode().equals("0")) {
                            //success
                            mRegView.success();
                        } else {
                            //error
                            mRegView.regFail(regResult.getMessage());
                        }
                        //showTask(task);
                    }
                });
        mSubscriptions.add(subscription);
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
