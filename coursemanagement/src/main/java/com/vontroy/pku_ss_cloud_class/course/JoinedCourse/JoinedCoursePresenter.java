package com.vontroy.pku_ss_cloud_class.course.JoinedCourse;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.data.CourseArrayResult;
import com.vontroy.pku_ss_cloud_class.data.CourseResult;
import com.vontroy.pku_ss_cloud_class.data.JoinCourseResult;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.util.ArrayList;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-22.
 */

public class JoinedCoursePresenter implements JoinedCourseContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final JoinedCourseContract.View mJoinedCourseView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;
    private ArrayList<CourseInfo> courseInfos;

    public JoinedCoursePresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                                 @NonNull JoinedCourseContract.View view,
                                 @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mJoinedCourseView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mJoinedCourseView.setPresenter(this);
    }

    @Override
    public void setCourseList(ArrayList<CourseInfo> courseList) {
        this.courseInfos = courseList;
    }

    @Override
    public void getMyCourses(Map params) {
        //TODO debug
        //courseInfos.clear();
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
                        mJoinedCourseView.listDataChanged();
                    }

                });
        mSubscriptions.add(subscription);
    }

    public void DropCourse(Map params) {
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
                            mJoinedCourseView.SuccessDropCourse();
                            mJoinedCourseView.listDataChanged();
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void subscribe(Map params) {
        getMyCourses(params);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
