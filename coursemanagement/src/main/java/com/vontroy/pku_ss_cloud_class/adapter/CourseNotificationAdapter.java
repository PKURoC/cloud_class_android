package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Vontroy on 2016-10-11.
 */

public class CourseNotificationAdapter extends BaseAdapter {

    private final Context mContext;
    private List<CourseInfo> courseList;

    public CourseNotificationAdapter(Context context, List<CourseInfo> courseList) {
        mContext = context;
        this.courseList = courseList;
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int i) {
        return courseList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.course_notification_item, parent, false);
            holder = new ViewHolder();
                    /*得到各个控件的对象*/
            holder.course_name = (TextView) convertView.findViewById(R.id.current_course_name);
            holder.course_position = (TextView) convertView.findViewById(R.id.current_course_position);
            holder.course_time = (TextView) convertView.findViewById(R.id.current_course_time);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.course_name.setText(courseList.get(position).getCourseRealName());

        if (!Strings.isNullOrEmpty(courseList.get(position).getClassroom())) {
            holder.course_position.setText(courseList.get(position).getClassroom());
        } else {
            holder.course_position.setVisibility(View.GONE);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date time = calendar.getTime();
        int weekVal = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (weekVal == 0) {
            weekVal = 7;
        }

        int courseWeekVal = -1;
        int courseWeekVal2 = -1;

        if (!Strings.isNullOrEmpty(courseList.get(position).getClassTime())) {
            courseWeekVal = Integer.valueOf(courseList.get(position).getClassTime().substring(0, 1));
        }
        if (!Strings.isNullOrEmpty(courseList.get(position).getClassTime2())) {
            courseWeekVal2 = Integer.valueOf(courseList.get(position).getClassTime2().substring(0, 1));
        }

        String detailTime = courseList.get(position).getClassTime();
        if (!Strings.isNullOrEmpty(detailTime) && weekVal == courseWeekVal) {
            String timeStr = detailTime.substring(1, 3) + ":" + detailTime.substring(3, 5);

            holder.course_time.setText(timeStr);
        }

        String detailTime2 = courseList.get(position).getClassTime2();
        if (!Strings.isNullOrEmpty(detailTime2) && weekVal == courseWeekVal2) {
            String timeStr = detailTime2.substring(1, 3) + ":" + detailTime2.substring(3, 5);
            holder.course_time.setText(timeStr);
        }

        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView course_name;
        TextView course_position;
        TextView course_time;
    }

}
