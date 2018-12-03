package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.entry.JobDetailInfo;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by vontroy on 2016-12-30.
 */

public class JobAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<JobDetailInfo> jobDetailInfos;

    public JobAdapter(Context context, ArrayList<JobDetailInfo> jobDetailInfos) {
        mContext = context;
        this.jobDetailInfos = jobDetailInfos;
    }

    @Override
    public int getCount() {
        return jobDetailInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return jobDetailInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JobAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.job_list_item, parent, false);
            holder = new JobAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.job_item_name = (TextView) convertView.findViewById(R.id.job_item_name);
            holder.job_item_about = (TextView) convertView.findViewById(R.id.job_item_about);
            holder.job_item_type = (TextView) convertView.findViewById(R.id.job_item_type);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (JobAdapter.ViewHolder) convertView.getTag();
        }
        if ("1".equals(jobDetailInfos.get(position).getType())) {
            holder.job_item_type.setText("[小组作业]");
            holder.job_item_type.setTextColor(Color.BLUE);
        } else if ("2".equals(jobDetailInfos.get(position).getType())) {
            holder.job_item_type.setText("[个人作业]");
            holder.job_item_type.setTextColor(Color.RED);
        } else if ("NONE".equals(jobDetailInfos.get(position).getType())) {
            holder.job_item_type.setVisibility(GONE);
        }
        holder.job_item_name.setText(jobDetailInfos.get(position).getName());
        holder.job_item_about.setText(jobDetailInfos.get(position).getAbout());

        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView job_item_name;
        TextView job_item_about;
        TextView job_item_type;
    }
}
