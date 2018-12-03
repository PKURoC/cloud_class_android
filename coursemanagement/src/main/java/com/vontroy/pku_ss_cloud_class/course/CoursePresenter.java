package com.vontroy.pku_ss_cloud_class.course;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.data.JoinCourseResult;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by LinkedME06 on 16/11/9.
 */

public class CoursePresenter implements CourseContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final CourseContract.View mCourseView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    public CoursePresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                           @NonNull CourseContract.View view,
                           @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mCourseView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mCourseView.setPresenter(this);
    }

    public void JoinCourse(Map params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.joinClass, params, JoinCourseResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<JoinCourseResult>() {
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
                    public void onNext(JoinCourseResult joinCourseResult) {
                        Log.d("ddd", "onNext: ");
                        if (joinCourseResult.getCode().equals("0")) {
                            mCourseView.successJoinCourse();
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void DropCourse(Map<String, String> params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.dropClass, params, JoinCourseResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<JoinCourseResult>() {
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
                    public void onNext(JoinCourseResult joinCourseResult) {
                        Log.d("ddd", "onNext: ");
                        if (joinCourseResult.getCode().equals("0")) {
                            mCourseView.successDropCourse();
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }

//        Map<String, String> pa = new HashMap<>();
//        pa.put("type","test110");
//        pa.put("postid", "dd");
//        Subscription subscription = mServerImp
//                .common(requestTag, Request.Method.GET, ServerInterface.testUrl, pa, TestResult.class)
//                .subscribeOn(mSchedulerProvider.computation())
//                .observeOn(mSchedulerProvider.ui())
//                .subscribe(new Observer<TestResult>() {
//                    @Override
//                    public void onCompleted() {
//                        //mTaskDetailView.setLoadingIndicator(false);
//                        Log.d("ddd", "onCompleted: ");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("ddd", "onError: ");
//                    }
//
//                    @Override
//                    public void onNext(TestResult loginResult) {
//                        Log.d("ddd", "onNext: " + loginResult.getStatus());
//                        //showTask(task);
//                    }
//                });
//        mSubscriptions.add(subscription);

    @Override
    public void subscribe() {
        //此处为页面打开后开始加载数据时调用的方法
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
