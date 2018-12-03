package com.vontroy.pku_ss_cloud_class.profile;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.google.common.base.Preconditions;
import com.vontroy.pku_ss_cloud_class.data.BaseResult;
import com.vontroy.pku_ss_cloud_class.data.CourseArrayResult;
import com.vontroy.pku_ss_cloud_class.data.CourseResult;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.util.ArrayList;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by vontroy on 16-11-17.
 */

public class ProfilePresenter implements ProfileContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final ProfileContract.View mProfileView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    private ArrayList<CourseInfo> courseInfos;

    public ProfilePresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                            @NonNull ProfileContract.View regView,
                            @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = Preconditions.checkNotNull(serverImp, "serverImp cannot be null!");
        mProfileView = Preconditions.checkNotNull(regView, "regView cannot be null!");
        mSchedulerProvider = Preconditions.checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mProfileView.setPresenter(this);
    }

    @Override
    public void setCourseInfos(ArrayList<CourseInfo> courseInfos) {
        this.courseInfos = courseInfos;
    }

    @Override
    public void getMyCourses(Map params) {
        courseInfos.clear();
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.getMyCourses, params, CourseArrayResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<CourseArrayResult>() {
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
                    public void onNext(CourseArrayResult courseArrayResult) {
                        Log.d("ddd", "onNext: ");
                        for (CourseResult courseResult : courseArrayResult.getData()) {
                            CourseInfo courseInfo = new CourseInfo("", "");
                            courseInfo.setCourseId(courseResult.getId());

                            String courseName = courseResult.getName();
                            String[] courseNameSegs = courseName.split("[\\d\\s]+");

                            courseInfo.setCourseName(courseName);
                            courseInfo.setCourseRealName(courseNameSegs[courseNameSegs.length - 1]);
                            courseInfo.setStudentNum(courseResult.getNums());
                            courseInfo.setCourseTeacher(courseResult.getTeacher());
                            courseInfo.setCourseIntroduction(courseResult.getAbout());
                            courseInfo.setOther(courseResult.getOther());
                            courseInfo.setClassroom(courseResult.getClassroom());
                            courseInfo.setOwnerId(courseResult.getOwnerid());
                            courseInfo.setClassTime(courseResult.getClasstime());
                            courseInfo.setClassTime2(courseResult.getClasstime2());
                            courseInfos.add(courseInfo);

                        }
                        mProfileView.getCourseInfosSuccess();
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void modifyNick(Map<String, String> params) {
        final String nick = params.get("newnick");
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.modifyNick, params, BaseResult.class)
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
                            mProfileView.updateNickSuccess(nick);
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
