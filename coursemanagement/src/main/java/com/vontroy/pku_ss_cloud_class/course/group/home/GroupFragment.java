package com.vontroy.pku_ss_cloud_class.course.group.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.GroupMemberAdapter;
import com.vontroy.pku_ss_cloud_class.adapter.StorageListAdapter;
import com.vontroy.pku_ss_cloud_class.data.Student;
import com.vontroy.pku_ss_cloud_class.databinding.CourseGroupFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.GroupInfo;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-16.
 */

public class GroupFragment extends Fragment implements GroupContract.View, TabHost.TabContentFactory {
    private CourseGroupFragBinding courseGroupFragBinding;
    private GroupContract.Presenter mPresenter;
    private ListView member_list;
    private ListView group_doc_list;
    private ArrayList<StorageInfo> docInfos;
    StorageListAdapter groupDocAdapter;
    private File[] downloadedFileItems;

    private GroupInfo myGroupInfo = new GroupInfo("", "");

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences getParams = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        final String token = getParams.getString("token", "");
        final String sid = getParams.getString("sid", "");

        final Bundle bundle = this.getArguments();
        final String groupId = bundle.getString("gid");
        final String courseId = bundle.getString("course_id");
        final String courseName = bundle.getString("course_name");
        final String groupName = bundle.getString("group_name");

        final Map<String, String> params = new HashMap<>();
        params.put("gid", groupId);
        params.put("token", token);
        params.put("sid", sid);
        params.put("cid", courseId);

        View root = inflater.inflate(R.layout.course_group_frag, container, false);
        courseGroupFragBinding = CourseGroupFragBinding.bind(root);
        courseGroupFragBinding.groupName.setText(groupName);

        ArrayList<Student> members = (ArrayList<Student>) bundle.getSerializable("member");

        member_list = (ListView) root.findViewById(R.id.groupMember);
        group_doc_list = (ListView) root.findViewById(R.id.groupDocs);
        GroupMemberAdapter groupMemberAdapter = new GroupMemberAdapter(this.getActivity(), members);

        member_list.setAdapter(groupMemberAdapter);
        setListViewHeightBasedOnChildren(member_list);

        String groupInvitation = bundle.getString(groupId + "_invitation");
        courseGroupFragBinding.groupInvitation.setText(groupInvitation);

        docInfos = new ArrayList<>();
        mPresenter.setDocInfo(docInfos);
        mPresenter.getGroupDocs(params, courseName);

        groupDocAdapter = new StorageListAdapter(getContext(), docInfos, "all");

        group_doc_list.setAdapter(groupDocAdapter);
        setListViewHeightBasedOnChildren(group_doc_list);

        courseGroupFragBinding.exitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> quitGroupParams = new HashMap<>();
                quitGroupParams.put("cid", courseId);
                quitGroupParams.put("gid", groupId);
                quitGroupParams.put("token", token);
                quitGroupParams.put("sid", sid);
                mPresenter.quitGroup(quitGroupParams);
            }
        });

        courseGroupFragBinding.uploadGroupDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle addGroupBundle = new Bundle();
                addGroupBundle.putString("gid", groupId);
                addGroupBundle.putString("sid", sid);
                addGroupBundle.putString("cid", courseId);
                addGroupBundle.putString("token", token);
                Intent intent = new Intent(getActivity(), GroupDocUploadActivity.class);
                intent.putExtras(addGroupBundle);

                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void setPresenter(@NonNull GroupContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public View createTabContent(String tag) {
        final TextView tv = new TextView(getActivity());
        tv.setText("Content for tab with tag " + tag);
        return tv;
    }

    @Override
    public void quitGroupResponse(String responseMsg) {
        Toast.makeText(getActivity(), responseMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getMyGroupSuccess() {

    }

    @Override
    public void listDataChanged() {

    }

    @Override
    public void groupDocDataChanged() {
        groupDocAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(group_doc_list);
    }

    /***
     * 动态设置listview的高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(-1, -1);  //<span style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">在还没有构建View 之前无法取得View的度宽。 </span><span style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">在此之前我们必须选 measure 一下. </span><br style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // params.height += 5;// if without this statement,the listview will be
        // a
        // little short
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
