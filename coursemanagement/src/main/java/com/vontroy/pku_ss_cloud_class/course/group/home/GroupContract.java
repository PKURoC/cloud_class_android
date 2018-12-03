package com.vontroy.pku_ss_cloud_class.course.group.home;

import android.support.annotation.NonNull;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.entry.GroupInfo;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vontroy on 16-11-16.
 */

public class GroupContract {
    interface View extends BaseView<Presenter> {
        void setPresenter(@NonNull GroupContract.Presenter presenter);

        void quitGroupResponse(String responseMsg);

        void getMyGroupSuccess();

        void listDataChanged();

        void groupDocDataChanged();
    }

    interface Presenter extends BasePresenter {

        void setGroupInfo(GroupInfo groupInfo);

        void setDocInfo(ArrayList<StorageInfo> docInfos);

        void getGroupInfo(Map params);

        void deleteGroup(Map params);

        void recoverGroup(Map params);

        void quitGroup(Map<String, String> params);

        void getGroupDocs(Map<String, String> params, String courseName);

        void deleteGroupDoc(Map params);
    }
}
