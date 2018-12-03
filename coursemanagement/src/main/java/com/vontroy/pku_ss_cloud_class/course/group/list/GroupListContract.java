package com.vontroy.pku_ss_cloud_class.course.group.list;

import android.support.annotation.NonNull;

import com.vontroy.pku_ss_cloud_class.BasePresenter;
import com.vontroy.pku_ss_cloud_class.BaseView;
import com.vontroy.pku_ss_cloud_class.entry.GroupInfo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by vontroy on 16-12-27.
 */

public class GroupListContract {
    interface View extends BaseView<Presenter> {
        void setPresenter(@NonNull GroupListContract.Presenter presenter);

        void groupListDateChanged();
    }

    interface Presenter extends BasePresenter {

        void setGroupInfos(ArrayList<GroupInfo> groupInfos);

        void setResultGroupInfos(ArrayList<GroupInfo> resultGroupInfos);

        void getGroupListInfos(Map<String, String> params);

        void getGroupInfo(Map<String, String> params);
    }
}
