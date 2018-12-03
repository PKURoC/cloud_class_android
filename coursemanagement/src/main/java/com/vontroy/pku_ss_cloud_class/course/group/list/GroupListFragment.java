package com.vontroy.pku_ss_cloud_class.course.group.list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.GroupAdapter;
import com.vontroy.pku_ss_cloud_class.course.group.create.AddGroupActivity;
import com.vontroy.pku_ss_cloud_class.databinding.GroupListFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.GroupInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 2016-12-23.
 */

public class GroupListFragment extends Fragment implements GroupListContract.View {
    private GroupListFragBinding groupListFragBinding;
    private GroupListContract.Presenter mPresenter;
    private ArrayList<GroupInfo> resultGroupInfos;
    private ArrayList<GroupInfo> groupInfos;
    private GroupAdapter groupAdapter;

    private ListView groupList;

    public static GroupListFragment newInstance() {
        return new GroupListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.group_list_frag, container, false);
        final Bundle bundle = this.getArguments();

        SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(getContext());
        String token = getToken.getString("token", "");
        String sid = getToken.getString("sid", "");

        groupListFragBinding = GroupListFragBinding.bind(root);

        groupListFragBinding.addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddGroupActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        groupInfos = (ArrayList<GroupInfo>) bundle.getSerializable("groupInfos");
        resultGroupInfos = new ArrayList<>();
        groupList = (ListView) root.findViewById(R.id.group_list);

        groupAdapter = new GroupAdapter(this.getActivity(), resultGroupInfos);

        groupList.setAdapter(groupAdapter);
        setListViewHeightBasedOnChildren(groupList);

//        groupListFragBinding.groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                GroupInfo groupItemDetail = groupInfos.get(i);
//                Bundle groupItemBundle = new Bundle();
//                groupItemBundle.putSerializable("groupItemDetail", groupItemDetail);
//                Intent intent = new Intent(getActivity(), GroupDetailActivity.class);
//                intent.putExtras(groupItemBundle);
//                startActivity(intent);
//            }
//        });

        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("sid", sid);

        mPresenter.setGroupInfos(groupInfos);
        mPresenter.setResultGroupInfos(resultGroupInfos);
        mPresenter.getGroupListInfos(params);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull GroupListContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void groupListDateChanged() {
        groupAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(groupList);
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
            listItem.measure(0, 0);  //<span style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">在还没有构建View 之前无法取得View的度宽。 </span><span style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">在此之前我们必须选 measure 一下. </span><br style="font-family: Helvetica, Tahoma, Arial, sans-serif; font-size: 14px; line-height: 25px; text-align: left; ">
            totalHeight += listItem.getMeasuredHeight();
            Log.d("ListViewHeight", "totalHeight: " + totalHeight);
            Log.d("ListViewHeight", "currentHeight: " + listItem.getMeasuredHeight());
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
