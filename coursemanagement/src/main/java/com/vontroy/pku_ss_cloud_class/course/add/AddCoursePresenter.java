package com.vontroy.pku_ss_cloud_class.course.add;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.data.CourseArrayResult;
import com.vontroy.pku_ss_cloud_class.data.CourseResult;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-16.
 */

public class AddCoursePresenter implements AddCourseContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final AddCourseContract.View mAddCourseView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;
    private ArrayList<CourseInfo> courseInfos;
    private ArrayList<CourseInfo> joinedCourseInfos;

    public AddCoursePresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                              @NonNull AddCourseContract.View view,
                              @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mAddCourseView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mAddCourseView.setPresenter(this);
    }

    @Override
    public void setCourseList(ArrayList<CourseInfo> courseList) {
        this.courseInfos = courseList;
    }

    @Override
    public void setJoinedCourseInfos(ArrayList<CourseInfo> joinedCourseInfos) {
        this.joinedCourseInfos = joinedCourseInfos;
    }

    @Override
    public void getCourses() {
        Map params = new HashMap();
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.getCourses, params, CourseArrayResult.class)
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

                            String courseName = courseResult.getName();
                            String[] courseNameSegs = courseName.split("[\\d\\s]+");

                            courseInfo.setCourseId(courseResult.getId());
                            courseInfo.setCourseName(courseName);
                            courseInfo.setCourseRealName(courseNameSegs[courseNameSegs.length - 1]);
                            courseInfo.setStudentNum(courseResult.getNums());
                            courseInfo.setCourseTeacher(courseResult.getTeacher());
                            courseInfo.setCourseIntroduction(courseResult.getAbout());
                            courseInfo.setClassTime(courseResult.getClasstime());
                            courseInfo.setOwnerId(courseResult.getOwnerid());
                            courseInfo.setClassroom(courseResult.getClassroom());
                            courseInfo.setOther(courseResult.getOther());
                            courseInfo.setClassTime2(courseResult.getClasstime2());

                            courseInfos.add(courseInfo);

                        }
                        mAddCourseView.listDataChanged();
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void getMyCourses(Map<String, String> params) {
        joinedCourseInfos.clear();
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
                            joinedCourseInfos.add(courseInfo);
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void subscribe() {
        getCourses();
    }

    @Override
    public void unsubscribe() {

    }
}
