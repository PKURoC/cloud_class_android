package com.vontroy.pku_ss_cloud_class.course.homework.main;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.data.GroupResult;
import com.vontroy.pku_ss_cloud_class.data.JobArrayResult;
import com.vontroy.pku_ss_cloud_class.data.JobResult;
import com.vontroy.pku_ss_cloud_class.entry.JobDetailInfo;
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
 * Created by vontroy on 16-11-16.
 */

public class HomeworkPresenter implements HomeworkContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final HomeworkContract.View mHomeworkView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    private ArrayList<JobDetailInfo> finishedJobDetailInfos;

    private ArrayList<JobDetailInfo> unfinishedJobDetailInfos;

    public HomeworkPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                             @NonNull HomeworkContract.View view,
                             @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mHomeworkView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");
        finishedJobDetailInfos = new ArrayList<>();
        unfinishedJobDetailInfos = new ArrayList<>();
        mSubscriptions = new CompositeSubscription();
        mHomeworkView.setPresenter(this);
    }

    @Override
    public void setFinishedJobDetailInfos(ArrayList<JobDetailInfo> finishedJobDetailInfos) {
        this.finishedJobDetailInfos = finishedJobDetailInfos;
    }

    @Override
    public void setUnfinishedJobDetailInfos(ArrayList<JobDetailInfo> unfinishedJobDetailInfos) {
        this.unfinishedJobDetailInfos = unfinishedJobDetailInfos;
    }

    @Override
    public void getJobs(Map params, final String courseName) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.getMyJobs, params, JobArrayResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<JobArrayResult>() {
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
                    public void onNext(JobArrayResult jobArrayResult) {
                        Log.d("ddd", "onNext: ");
                        if (jobArrayResult.getCode().equals("0")) {
                            ArrayList<JobResult> finishedJobResults = jobArrayResult.getFinished();
                            ArrayList<JobResult> unfinishedJobResults = jobArrayResult.getUnfinished();
                            for (JobResult finishedJobResult : finishedJobResults) {
                                JobDetailInfo jobDetailInfo = new JobDetailInfo();
                                jobDetailInfo.setName(finishedJobResult.getName());
                                jobDetailInfo.setId(finishedJobResult.getId());
                                jobDetailInfo.setJobid(finishedJobResult.getJobid());
                                jobDetailInfo.setAbout(finishedJobResult.getAbout());
                                jobDetailInfo.setAttach(finishedJobResult.getAttach());
                                jobDetailInfo.setType(finishedJobResult.getType());
                                jobDetailInfo.setSubmittime(finishedJobResult.getSubmittime());
                                jobDetailInfo.setDeadline(finishedJobResult.getDeadline());
                                jobDetailInfo.setUuid(finishedJobResult.getUuid());
                                jobDetailInfo.setUserid(finishedJobResult.getUserid());
                                jobDetailInfo.setFilename(finishedJobResult.getFilename());
                                jobDetailInfo.setCoursename(courseName);
                                jobDetailInfo.setFinished(true);
                                finishedJobDetailInfos.add(jobDetailInfo);
                            }
                            for (JobResult unfinishedJobResult : unfinishedJobResults) {
                                JobDetailInfo jobDetailInfo = new JobDetailInfo();
                                jobDetailInfo.setName(unfinishedJobResult.getName());
                                jobDetailInfo.setId(unfinishedJobResult.getId());
                                jobDetailInfo.setJobid(unfinishedJobResult.getJobid());
                                jobDetailInfo.setAbout(unfinishedJobResult.getAbout());
                                jobDetailInfo.setAttach(unfinishedJobResult.getAttach());
                                jobDetailInfo.setType(unfinishedJobResult.getType());
                                jobDetailInfo.setSubmittime(unfinishedJobResult.getSubmittime());
                                jobDetailInfo.setDeadline(unfinishedJobResult.getDeadline());
                                jobDetailInfo.setUuid(unfinishedJobResult.getUuid());
                                jobDetailInfo.setUserid(unfinishedJobResult.getUserid());
                                jobDetailInfo.setFilename(unfinishedJobResult.getFilename());
                                jobDetailInfo.setCoursename(courseName);
                                jobDetailInfo.setFinished(false);
                                unfinishedJobDetailInfos.add(jobDetailInfo);
                            }
                            if (finishedJobDetailInfos.size() == 0) {
                                JobDetailInfo jobDetailInfo = new JobDetailInfo();
                                jobDetailInfo.setName("暂无作业");
                                jobDetailInfo.setType("NONE");
                                finishedJobDetailInfos.add(jobDetailInfo);
                            }
                            if (unfinishedJobDetailInfos.size() == 0) {
                                JobDetailInfo jobDetailInfo = new JobDetailInfo();
                                jobDetailInfo.setType("NONE");
                                jobDetailInfo.setName("暂无作业");
                                unfinishedJobDetailInfos.add(jobDetailInfo);
                            }
                            mHomeworkView.listDataChanged();
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void getMyDocs(Map params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.getMyDocs, params, GroupResult.class)
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
                        Log.d("ddd", "onNext: ");
                        if (groupResult.getCode().equals("0")) {

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
