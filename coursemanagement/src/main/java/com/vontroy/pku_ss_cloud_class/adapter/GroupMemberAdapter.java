package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.Student;

import java.util.ArrayList;

/**
 * Created by vontroy on 2016-12-27.
 */

public class GroupMemberAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<Student> memberItem;

    public GroupMemberAdapter(Context context, ArrayList<Student> memberInfo) {
        mContext = context;
        this.memberItem = memberInfo;
    }

    @Override
    public int getCount() {
        return memberItem.size();
    }

    @Override
    public Object getItem(int i) {
        return memberItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupMemberAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.group_member_item, parent, false);
            holder = new GroupMemberAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.member_nick = (TextView) convertView.findViewById(R.id.member_nick);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (GroupMemberAdapter.ViewHolder) convertView.getTag();
        }
        holder.member_nick.setText(memberItem.get(position).getNick());
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView member_nick;
    }
}
