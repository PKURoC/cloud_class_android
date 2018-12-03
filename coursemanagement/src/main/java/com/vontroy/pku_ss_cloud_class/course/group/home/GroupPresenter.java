package com.vontroy.pku_ss_cloud_class.course.group.home;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vontroy.pku_ss_cloud_class.data.DocArrayResult;
import com.vontroy.pku_ss_cloud_class.data.DocResult;
import com.vontroy.pku_ss_cloud_class.data.GroupInfoResult;
import com.vontroy.pku_ss_cloud_class.data.GroupResult;
import com.vontroy.pku_ss_cloud_class.data.Student;
import com.vontroy.pku_ss_cloud_class.entry.GroupInfo;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.Constants;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.BaseSchedulerProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.content.ContentValues.TAG;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-16.
 */

public class GroupPresenter implements GroupContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final GroupContract.View mGroupView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    private ArrayList<GroupInfo> groupInfos;

    private GroupInfo myGroupInfo;

    private ArrayList<StorageInfo> docInfos;

    private File[] downloadedFileItems;

    public GroupPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                          @NonNull GroupContract.View view,
                          @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mGroupView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");
        myGroupInfo = new GroupInfo("", "");
        mSubscriptions = new CompositeSubscription();
        mGroupView.setPresenter(this);
    }

    @Override
    public void setGroupInfo(GroupInfo groupInfo) {
        this.myGroupInfo = groupInfo;
    }

    @Override
    public void setDocInfo(ArrayList<StorageInfo> docInfos) {
        this.docInfos = docInfos;
    }

    @Override
    public void getGroupInfo(Map params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.getGroupInfo, params, GroupInfoResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<GroupInfoResult>() {
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
                    public void onNext(GroupInfoResult groupInfoResult) {
                        Log.d("ddd", "onNext: ");
                        JsonArray memberArray = groupInfoResult.getMembers();
                        JsonObject groupInfoJson = groupInfoResult.getGroupinfo();

                        GroupInfo groupInfo = new GroupInfo("", "");
                        groupInfo.setGroupId(groupInfoJson.get("id").getAsString());
                        groupInfo.setGroupName(groupInfoJson.get("name").getAsString());
                        groupInfo.setOwnerName(groupInfoJson.get("ownername").getAsString());
                        groupInfo.setGroupIntroduction(groupInfoJson.get("about").getAsString());
                        groupInfo.setCourseId(groupInfoJson.get("courseid").getAsString());

                        ArrayList<Student> members = new ArrayList<Student>();
                        for (JsonElement memberElement : memberArray) {
                            Student member = new Student();
                            JsonObject memberJson = memberElement.getAsJsonObject();
                            member.setNick(memberJson.get("nick").getAsString());
                            member.setSid(memberJson.get("sid").getAsString());
                            members.add(member);
                        }

                        groupInfo.setGroupMembers(members);
                        groupInfos.add(groupInfo);
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void deleteGroup(Map params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.deleteGroup, params, GroupResult.class)
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
    public void recoverGroup(Map params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.recoverGroup, params, GroupResult.class)
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
    public void quitGroup(Map<String, String> params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.POST, ServerInterface.quitGroup, params, GroupResult.class)
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
                        String responseMsg = groupResult.getMessage();
                        if (groupResult.getCode().equals("0")) {
                            Log.d(TAG, "onNext: " + "exit success!");

                            if (Strings.isNullOrEmpty(responseMsg)) {
                                responseMsg = "退出小组成功";
                            }
                        }
                        mGroupView.quitGroupResponse(responseMsg);
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void getGroupDocs(Map<String, String> params, final String courseName) {
        final String groupId = params.get("gid");
        final String courseId = params.get("cid");
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.getGroupDocs, params, DocArrayResult.class)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(new Observer<DocArrayResult>() {
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
                    public void onNext(DocArrayResult docArrayResult) {
                        Log.d("ddd", "onNext: ");
                        if (docArrayResult.getCode().equals("0")) {
                            Log.d(TAG, "onNext: ");
                            ArrayList<DocResult> docResults = docArrayResult.getData();
                            readDownloadedFiles(courseName);
                            for (DocResult docResult : docResults) {
                                StorageInfo docInfo = new StorageInfo("", "");
                                docInfo.setFileName(docResult.getFilename());
                                docInfo.setUuid(docResult.getUuid());
                                docInfo.setStorageType(Constants.StorageType.GROUP);
                                docInfo.setGroupId(groupId);
                                docInfo.setCourseId(courseId);
                                docInfo.setIntegrity(docResult.getIntegrity());
                                docInfo.setCourseName(courseName);
                                if (downloadedFileItems != null) {
                                    for (File file : downloadedFileItems) {
                                        if (file.getName().equals(docInfo.getFileName())) {
                                            docInfo.setLocalExists(true);
                                            break;
                                        } else {
                                            docInfo.setLocalExists(false);
                                        }
                                    }
                                }
                                docInfos.add(docInfo);
                            }

                            //Debug
//                            for (int i = 0; i < 3; i++) {
//                                DocInfo docInfo = new DocInfo();
//                                docInfo.setName("Test" + String.valueOf(i));
//                                docInfo.setId(String.valueOf(i));
//                                docInfos.add(docInfo);
//                            }
                        }
                        mGroupView.groupDocDataChanged();
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void deleteGroupDoc(Map params) {
        Subscription subscription = mServerImp
                .common(requestTag, Request.Method.GET, ServerInterface.deleteGroupDoc, params, GroupResult.class)
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

    public void readDownloadedFiles(String courseName) {
        String dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/小组资料";

        File downloadedFileDir = new File(dir);

        if (!downloadedFileDir.exists()) {
            downloadedFileDir.mkdir();
        } else {
            downloadedFileItems = downloadedFileDir.listFiles();
        }
    }


}
