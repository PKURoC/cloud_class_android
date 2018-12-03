package com.vontroy.pku_ss_cloud_class.home;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.vontroy.pku_ss_cloud_class.data.BaseResult;
import com.vontroy.pku_ss_cloud_class.data.CourseArrayResult;
import com.vontroy.pku_ss_cloud_class.data.CourseResult;
import com.vontroy.pku_ss_cloud_class.data.MsgResult;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;
import com.vontroy.pku_ss_cloud_class.entry.MyMsgInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by vontroy on 16-11-15.
 */

public class HomePresenter implements HomeContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final HomeContract.View mHomeView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    private ArrayList<CourseInfo> courseInfos;

    private ArrayList<MyMsgInfo> recentMsgs;

    public HomePresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                         @NonNull HomeContract.View regView,
                         @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = Preconditions.checkNotNull(serverImp, "serverImp cannot be null!");
        mHomeView = Preconditions.checkNotNull(regView, "regView cannot be null!");
        mSchedulerProvider = Preconditions.checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        mHomeView.setPresenter(this);
    }

    @Override
    public void setCourseInfos(ArrayList<CourseInfo> courseInfos) {
        this.courseInfos = courseInfos;
    }

    @Override
    public void setRecentMsgs(ArrayList<MyMsgInfo> recentMsgs) {
        this.recentMsgs = recentMsgs;
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
                        if ("0".equals(courseArrayResult.getCode())) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                            Date time = calendar.getTime();
                            int weekVal = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                            if (weekVal == 0) {
                                weekVal = 7;
                            }

                            for (CourseResult courseResult : courseArrayResult.getData()) {
                                int courseWeekVal = -1;
                                if (!Strings.isNullOrEmpty(courseResult.getClasstime())) {
                                    courseWeekVal = Integer.valueOf(courseResult.getClasstime().substring(0, 1));
                                }

                                int courseWeekVal2 = -1;

                                if (!Strings.isNullOrEmpty(courseResult.getClasstime2())) {
                                    courseWeekVal2 = Integer.valueOf(courseResult.getClasstime2().substring(0, 1));
                                }
                                if (courseWeekVal == weekVal || courseWeekVal2 == weekVal) {
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
                            }

                            if (courseInfos.size() == 0) {
                                CourseInfo courseInfo = new CourseInfo("", "");
                                courseInfo.setCourseName("今日无课");
                                courseInfo.setCourseRealName("今日无课");
                                courseInfos.add(courseInfo);
                            }
                        } else {
                            CourseInfo courseInfo = new CourseInfo("", "");
                            courseInfo.setCourseName("今日无课");
                            courseInfo.setCourseRealName("今日无课");
                            courseInfos.add(courseInfo);
                        }
                        mHomeView.courseDataChanged();
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void getRecent(Map params) {
        recentMsgs.clear();
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.getRecent, params, MsgResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<MsgResult>() {
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
                    public void onNext(MsgResult msgResult) {
                        Log.d("ddd", "onNext: ");
                        if ("0".equals(msgResult.getCode())) {
                            try {
                                JSONObject result = new JSONObject(msgResult.getData().toString());
                                Iterator<String> keys = result.keys();

                                if (!keys.hasNext()) {
                                    MyMsgInfo myMsgInfo = new MyMsgInfo("", "没有未读消息");
                                    myMsgInfo.setState("N");

                                    recentMsgs.add(myMsgInfo);
                                } else {
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        String msg = result.getString(key);

                                        String msgContent;
                                        String msgType;
                                        String msgTime;

                                        try {
                                            JSONObject jsonObject = new JSONObject(msg);
                                            msgTime = jsonObject.getString("time");
                                            msgType = jsonObject.getString("type");
                                            msgContent = jsonObject.getString("value");
                                        } catch (Exception e) {
                                            msgTime = "";
                                            msgType = "default";
                                            msgContent = msg;
                                        }

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd#HH:mm:ss");

                                        Date date = new Date(Long.valueOf(msgTime));
                                        Date today = new Date();
                                        Date yesterday = new Date(new Date().getTime() - 24 * 60 * 60 * 1000);
                                        String dateStr = sdf.format(date);
                                        String todayStr = sdf.format(today);
                                        String yesterdayStr = sdf.format(yesterday);
                                        String currentTime = sdfTime.format(date);
                                        String[] timeArray = currentTime.split("#");
                                        String timeHMS = timeArray[1];

                                        String resultDate;

                                        MyMsgInfo myMsgInfo = new MyMsgInfo("", "");
                                        myMsgInfo.setAccurateTime(msgTime);

                                        String titleStr = "";
                                        switch (msgType) {
                                            case "joingroup":
                                                titleStr = "小组消息";
                                                break;
                                            case "newjob":
                                                titleStr = "作业提醒";
                                                break;
                                        }

                                        myMsgInfo.setTitle(titleStr);
                                        myMsgInfo.setContent(msgContent);
                                        if (todayStr.equals(dateStr)) {
                                            resultDate = "今天 " + timeHMS;
                                        } else if (yesterdayStr.equals(dateStr)) {
                                            resultDate = "昨天 " + timeHMS;
                                        } else {
                                            resultDate = dateStr + " " + timeHMS;
                                        }
                                        myMsgInfo.setTime(resultDate);
                                        myMsgInfo.setNotifyId(key);
                                        myMsgInfo.setState("R");

                                        recentMsgs.add(myMsgInfo);
                                    }
                                    Collections.sort(recentMsgs, new SortByTime());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            MyMsgInfo myMsgInfo = new MyMsgInfo("", "没有未读消息");
                            myMsgInfo.setState("N");
                            recentMsgs.add(myMsgInfo);
                        }
                        mHomeView.getRecentMsgSuccess();
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void hasRead(Map params) {
        courseInfos.clear();
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.hasRead, params, BaseResult.class)
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
                            mHomeView.markSingleMsgAsHasReadSuccess();
                        }

                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void hasReadAll(Map params) {
        courseInfos.clear();
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.hasReadAll, params, BaseResult.class)
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
                            mHomeView.markAllMsgAsHasReadSuccess();
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

    @Override
    public void addCourse() {

    }

    class SortByTime implements Comparator {
        public int compare(Object o1, Object o2) {
            MyMsgInfo msg1 = (MyMsgInfo) o1;
            MyMsgInfo msg2 = (MyMsgInfo) o2;
            return (msg1.getAccurateTime().compareTo(msg2.getAccurateTime())) * -1;
        }
    }
}
