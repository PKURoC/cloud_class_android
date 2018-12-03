package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.entry.MyMsgInfo;

import java.util.ArrayList;

/**
 * Created by vontroy on 16-11-21.
 */

public class MsgDetailItemAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<MyMsgInfo> msgList;

    public MsgDetailItemAdapter(Context context, ArrayList<MyMsgInfo> myMsgInfos) {
        mContext = context;
        this.msgList = myMsgInfos;
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int i) {
        return msgList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MsgDetailItemAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.my_msg_detail_item, parent, false);
            holder = new MsgDetailItemAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.msg_title = (TextView) convertView.findViewById(R.id.msg_detail_title);
            holder.msg_content = (TextView) convertView.findViewById(R.id.msg_detail_content);
            holder.msg_time = (TextView) convertView.findViewById(R.id.msg_detail_time);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (MsgDetailItemAdapter.ViewHolder) convertView.getTag();
        }
        holder.msg_title.setText(msgList.get(position).getTitle());
        holder.msg_content.setText(msgList.get(position).getContent());
        holder.msg_time.setText(msgList.get(position).getTime());
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView msg_title;
        TextView msg_content;
        TextView msg_time;
    }
}
