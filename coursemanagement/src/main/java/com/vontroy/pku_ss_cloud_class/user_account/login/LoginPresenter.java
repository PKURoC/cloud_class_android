package com.vontroy.pku_ss_cloud_class.user_account.login;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.JsonObject;
import com.vontroy.pku_ss_cloud_class.data.BaseResult;
import com.vontroy.pku_ss_cloud_class.data.LoginResult;
import com.vontroy.pku_ss_cloud_class.data.Student;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by LinkedME06 on 16/10/27.
 */

public class LoginPresenter implements LoginContract.Presenter {

    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final LoginContract.View mLoginView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public LoginPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                          @NonNull LoginContract.View loginView,
                          @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mLoginView = checkNotNull(loginView, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mLoginView.setPresenter(this);
    }


    @Override
    public void login(@NonNull final Student student) {
        Subscription subscription = mServerImp
                .login(requestTag, student)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<LoginResult>() {
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
                    public void onNext(LoginResult loginResult) {
                        Log.d("ddd", "onNext: " + loginResult.getCode() + "ddd" + loginResult.getData());
                        if (loginResult.getCode().equals("0")) {
                            Log.d("ddd", "is success");
                            JsonObject dataJson = loginResult.getData();
                            String token = dataJson.get("token").getAsString();
                            String nick = dataJson.get("nick").getAsString();
                            mLoginView.setToken(token);
                            mLoginView.setSid(student.getSid());
                            mLoginView.setNick(nick);
                            mLoginView.success();
                        } else {
                            mLoginView.fail("密码错误!");
                        }
                        //showTask(task);
                    }
                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void forgotPwd(Map<String, String> params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.forgotPwd, params, BaseResult.class)
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
                        if ("0".equals(baseResult.getCode())) {
                            mLoginView.resetPwdSuccess(baseResult.getMessage());
                        }
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
