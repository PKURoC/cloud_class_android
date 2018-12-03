package com.vontroy.pku_ss_cloud_class.course.group.list;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vontroy.pku_ss_cloud_class.data.GroupInfoResult;
import com.vontroy.pku_ss_cloud_class.data.Student;
import com.vontroy.pku_ss_cloud_class.entry.GroupInfo;
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
 * Created by vontroy on 16-12-27.
 */

public class GroupListPresenter implements GroupListContract.Presenter {
    @NonNull
    private String requestTag;

    @NonNull
    private final ServerImp mServerImp;
    @NonNull
    private final GroupListContract.View mGroupView;
    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;
    @NonNull
    private CompositeSubscription mSubscriptions;

    private ArrayList<GroupInfo> groupInfos;
    private ArrayList<GroupInfo> resultGroupInfos;

    public GroupListPresenter(@NonNull String requestTag, @NonNull ServerImp serverImp,
                              @NonNull GroupListContract.View view,
                              @NonNull BaseSchedulerProvider schedulerProvider) {
        this.requestTag = requestTag;
        mServerImp = checkNotNull(serverImp, "serverImp cannot be null!");
        mGroupView = checkNotNull(view, "loginView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");
        mSubscriptions = new CompositeSubscription();
        mGroupView.setPresenter(this);
    }

    @Override
    public void setGroupInfos(ArrayList<GroupInfo> groupInfos) {
        this.groupInfos = groupInfos;
    }

    @Override
    public void setResultGroupInfos(ArrayList<GroupInfo> resultGroupInfos) {
        this.resultGroupInfos = resultGroupInfos;
    }

    @Override
    public void getGroupListInfos(Map<String, String> params) {
        String sid = params.get("sid");
        String token = params.get("token");

        resultGroupInfos.clear();
        for (GroupInfo groupInfo : groupInfos) {
            Map<String, String> getGroupInfoParams = new HashMap<>();
            getGroupInfoParams.put("sid", sid);
            getGroupInfoParams.put("token", token);
            getGroupInfoParams.put("gid", groupInfo.getGroupId());
            getGroupInfo(getGroupInfoParams);
        }
    }

    @Override
    public void getGroupInfo(Map<String, String> params) {
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
                        try {
                            groupInfo.setGroupId(groupInfoJson.get("id").getAsString());
                        } catch (Exception e) {
                            groupInfo.setGroupId("");
                        }
                        try {
                            groupInfo.setGroupName(groupInfoJson.get("name").getAsString());
                        } catch (Exception e) {
                            groupInfo.setGroupName("");
                        }
                        try {
                            groupInfo.setOwnerName(groupInfoJson.get("ownername").getAsString());
                        } catch (Exception e) {
                            groupInfo.setOwnerName("");
                        }
                        try {
                            groupInfo.setGroupIntroduction(groupInfoJson.get("about").getAsString());
                        } catch (Exception e) {
                            groupInfo.setGroupIntroduction("");
                        }
                        try {
                            groupInfo.setCourseId(groupInfoJson.get("courseid").getAsString());
                        } catch (Exception e) {
                            groupInfo.setCourseId("");
                        }

                        ArrayList<Student> members = new ArrayList<Student>();
                        for (JsonElement memberElement : memberArray) {
                            Student member = new Student();
                            JsonObject memberJson = memberElement.getAsJsonObject();
                            member.setNick(memberJson.get("nick").getAsString());
                            member.setSid(memberJson.get("sid").getAsString());
                            members.add(member);
                        }

                        groupInfo.setGroupMembers(members);
                        resultGroupInfos.add(groupInfo);
                        if (resultGroupInfos.size() == groupInfos.size()) {
                            mGroupView.groupListDateChanged();
                        }
                    }

                });
        mSubscriptions.add(subscription);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
