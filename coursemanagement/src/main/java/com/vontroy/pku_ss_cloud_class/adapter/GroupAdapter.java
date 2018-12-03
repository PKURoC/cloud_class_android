package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.BaseResult;
import com.vontroy.pku_ss_cloud_class.data.Student;
import com.vontroy.pku_ss_cloud_class.entry.GroupInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscription;

/**
 * Created by vontroy on 2016-12-27.
 */

public class GroupAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<GroupInfo> groupInfos;

    public GroupAdapter(Context context, ArrayList<GroupInfo> groupInfos) {
        mContext = context;
        this.groupInfos = groupInfos;
    }

    @Override
    public int getCount() {
        return groupInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return groupInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        GroupAdapter.ViewHolder holder;
        SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String token = getToken.getString("token", "");
        final String sid = getToken.getString("sid", "");
        ArrayList<Student> members = groupInfos.get(position).getGroupMembers();

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.group_list_item, parent, false);
            holder = new GroupAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.group_name = (TextView) convertView.findViewById(R.id.group_item_name);
            holder.group_introduction = (TextView) convertView.findViewById(R.id.group_item_introduction);
            holder.join_group_tv = (TextView) convertView.findViewById(R.id.join_group_tv);
            holder.group_item_owner = (TextView) convertView.findViewById(R.id.group_item_owner);
            holder.layer_1 = (LinearLayout) convertView.findViewById(R.id.layer_1);
            holder.layer_2 = (LinearLayout) convertView.findViewById(R.id.layer_2);
            holder.layer_3 = (LinearLayout) convertView.findViewById(R.id.layer_3);
            holder.layer_4 = (LinearLayout) convertView.findViewById(R.id.layer_4);

            holder.member_1 = (TextView) convertView.findViewById(R.id.member_1);
            holder.member_2 = (TextView) convertView.findViewById(R.id.member_2);
            holder.member_3 = (TextView) convertView.findViewById(R.id.member_3);
            holder.member_4 = (TextView) convertView.findViewById(R.id.member_4);
            holder.member_5 = (TextView) convertView.findViewById(R.id.member_5);
            holder.member_6 = (TextView) convertView.findViewById(R.id.member_6);
            holder.member_7 = (TextView) convertView.findViewById(R.id.member_7);
            holder.member_8 = (TextView) convertView.findViewById(R.id.member_8);
            holder.member_9 = (TextView) convertView.findViewById(R.id.member_9);
            holder.member_10 = (TextView) convertView.findViewById(R.id.member_10);
            holder.member_11 = (TextView) convertView.findViewById(R.id.member_11);
            holder.member_12 = (TextView) convertView.findViewById(R.id.member_12);
            holder.member_13 = (TextView) convertView.findViewById(R.id.member_13);
            holder.member_14 = (TextView) convertView.findViewById(R.id.member_14);
            holder.member_15 = (TextView) convertView.findViewById(R.id.member_15);
            holder.member_16 = (TextView) convertView.findViewById(R.id.member_16);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (GroupAdapter.ViewHolder) convertView.getTag();
        }
        holder.group_name.setText(groupInfos.get(position).getGroupName());
        holder.group_introduction.setText(groupInfos.get(position).getGroupIntroduction());
        holder.group_item_owner.setText(groupInfos.get(position).getOwnerName());
        holder.join_group_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputServer = new EditText(mContext);
                inputServer.setFocusable(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("请输入邀请码:").setIcon(
                        R.drawable.msg_fore_icon).setView(inputServer).setNegativeButton(
                        "取消", null);
                builder.setPositiveButton("加入",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String invitationCode = inputServer.getText().toString();

                                Map<String, String> params = new HashMap<String, String>();

                                GroupInfo groupInfo = groupInfos.get(position);
                                String groupId = groupInfo.getGroupId();
                                String cid = groupInfo.getCourseId();

                                params.put("gid", groupId);
                                params.put("token", token);
                                params.put("sid", sid);
                                params.put("cid", cid);
                                params.put("invitation", invitationCode);

                                joinGroup(params);
                            }
                        });
                builder.show();
            }
        });

        int memberSize = members.size();

        if (memberSize < 5) {
            holder.layer_2.setVisibility(View.GONE);
            holder.layer_3.setVisibility(View.GONE);
            holder.layer_4.setVisibility(View.GONE);
        } else if (memberSize < 9) {
            holder.layer_3.setVisibility(View.GONE);
            holder.layer_4.setVisibility(View.GONE);
        } else if (memberSize < 13) {
            holder.layer_4.setVisibility(View.GONE);
        }

        for (int i = 0; i < members.size(); ++i) {
            switch (i) {
                case 0:
                    holder.member_1.setVisibility(View.VISIBLE);
                    holder.member_1.setText(members.get(i).getNick());

                    break;
                case 1:
                    holder.member_2.setVisibility(View.VISIBLE);
                    holder.member_2.setText(members.get(i).getNick());

                    break;
                case 2:
                    holder.member_3.setVisibility(View.VISIBLE);
                    holder.member_3.setText(members.get(i).getNick());

                    break;
                case 3:
                    holder.member_4.setVisibility(View.VISIBLE);
                    holder.member_4.setText(members.get(i).getNick());

                    break;
                case 4:
                    holder.member_5.setVisibility(View.VISIBLE);
                    holder.member_5.setText(members.get(i).getNick());

                    break;
                case 5:
                    holder.member_6.setVisibility(View.VISIBLE);
                    holder.member_6.setText(members.get(i).getNick());

                    break;
                case 6:
                    holder.member_7.setVisibility(View.VISIBLE);
                    holder.member_7.setText(members.get(i).getNick());

                    break;
                case 7:
                    holder.member_8.setVisibility(View.VISIBLE);
                    holder.member_8.setText(members.get(i).getNick());

                    break;
                case 8:
                    holder.member_9.setVisibility(View.VISIBLE);
                    holder.member_9.setText(members.get(i).getNick());

                    break;
                case 9:
                    holder.member_10.setVisibility(View.VISIBLE);
                    holder.member_10.setText(members.get(i).getNick());

                    break;
                case 10:
                    holder.member_11.setVisibility(View.VISIBLE);
                    holder.member_11.setText(members.get(i).getNick());

                    break;
                case 11:
                    holder.member_12.setVisibility(View.VISIBLE);
                    holder.member_12.setText(members.get(i).getNick());

                    break;
                case 12:
                    holder.member_13.setVisibility(View.VISIBLE);
                    holder.member_13.setText(members.get(i).getNick());

                    break;
                case 13:
                    holder.member_14.setVisibility(View.VISIBLE);
                    holder.member_14.setText(members.get(i).getNick());

                    break;
                case 14:
                    holder.member_15.setVisibility(View.VISIBLE);
                    holder.member_15.setText(members.get(i).getNick());

                    break;
                case 15:
                    holder.member_16.setVisibility(View.VISIBLE);
                    holder.member_16.setText(members.get(i).getNick());

                    break;
            }
        }

            switch (members.size()) {
                case 0:
                    holder.member_1.setVisibility(View.GONE);
                case 1:
                    holder.member_2.setVisibility(View.GONE);
                case 2:
                    holder.member_3.setVisibility(View.GONE);
                case 3:
                    holder.member_4.setVisibility(View.GONE);
                case 4:
                    holder.member_5.setVisibility(View.GONE);
                case 5:
                    holder.member_6.setVisibility(View.GONE);
                case 6:
                    holder.member_7.setVisibility(View.GONE);
                case 7:
                    holder.member_8.setVisibility(View.GONE);
                case 8:
                    holder.member_9.setVisibility(View.GONE);
                case 9:
                    holder.member_10.setVisibility(View.GONE);
                case 10:
                    holder.member_11.setVisibility(View.GONE);
                case 11:
                    holder.member_12.setVisibility(View.GONE);
                case 12:
                    holder.member_13.setVisibility(View.GONE);
                case 13:
                    holder.member_14.setVisibility(View.GONE);
                case 14:
                    holder.member_15.setVisibility(View.GONE);
                case 15:
                    holder.member_16.setVisibility(View.GONE);
            }


        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView group_name;
        TextView group_introduction;
        TextView join_group_tv;
        TextView group_item_owner;

        LinearLayout layer_1;
        LinearLayout layer_2;
        LinearLayout layer_3;
        LinearLayout layer_4;

        TextView member_1;
        TextView member_2;
        TextView member_3;
        TextView member_4;
        TextView member_5;
        TextView member_6;
        TextView member_7;
        TextView member_8;
        TextView member_9;
        TextView member_10;
        TextView member_11;
        TextView member_12;
        TextView member_13;
        TextView member_14;
        TextView member_15;
        TextView member_16;
    }

    private void joinGroup(Map params) {
        Subscription subscription = ServerImp.getInstance()
                .common(GroupAdapter.class.getSimpleName(), Request.Method.POST, ServerInterface.joinGroup, params, BaseResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
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
                        if (baseResult.getCode().equals("0")) {
                            Toast.makeText(mContext, "加入小组成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
}
