package com.vontroy.pku_ss_cloud_class.course.group.detail;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.databinding.GroupDetailFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.GroupInfo;

import java.util.HashMap;
import java.util.Map;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 2016-12-28.
 */

public class GroupDetailFragment extends Fragment implements GroupDetailContract.View {
    private GroupDetailFragBinding groupDetailFragBinding;
    private GroupDetailContract.Presenter mPresenter;

    public static GroupDetailFragment newInstance() {
        return new GroupDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.group_detail_frag, container, false);
        Bundle groupItemBundle = getArguments();
        final SharedPreferences getParams = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        final String token = getParams.getString("token", "");
        final String sid = getParams.getString("sid", "");

        final GroupInfo groupInfo = (GroupInfo) groupItemBundle.getSerializable("groupItemDetail");
        final String cid = groupInfo.getCourseId();
        final String groupId = groupInfo.getGroupId();


        TextView groupName = (TextView) root.findViewById(R.id.group_detail_name);
        TextView groupOwner = (TextView) root.findViewById(R.id.group_detail_owner);
        TextView groupIntroduction = (TextView) root.findViewById(R.id.group_detail_about);

        groupName.setText(groupInfo.getGroupName());
        groupOwner.setText(groupInfo.getOwnerName());
        groupIntroduction.setText(groupInfo.getGroupIntroduction());

        groupDetailFragBinding = GroupDetailFragBinding.bind(root);
        groupDetailFragBinding.joinGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText inputServer = new EditText(getActivity());
                inputServer.setFocusable(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("请输入邀请码:").setIcon(
                        R.drawable.msg_fore_icon).setView(inputServer).setNegativeButton(
                        "取消", null);
                builder.setPositiveButton("加入",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String invitationCode = inputServer.getText().toString();

                                Map<String, String> params = new HashMap<String, String>();

                                params.put("gid", groupId);
                                params.put("token", token);
                                params.put("sid", sid);
                                params.put("cid", cid);
                                params.put("invitation", invitationCode);

                                mPresenter.joinGroup(params);
                            }
                        });
                builder.show();


            }
        });

        return root;
    }

    @Override
    public void joinGroupSuccess() {
        Toast.makeText(getActivity(), "加入小组成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(@NonNull GroupDetailContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
