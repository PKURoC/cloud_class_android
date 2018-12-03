package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;

import java.util.List;

/**
 * Created by Vontroy on 2016-10-11.
 */

public class CourseListAdapter extends BaseAdapter {

    private final Context mContext;
    private List<CourseInfo> courseList;

    public CourseListAdapter(Context context, List<CourseInfo> courseList) {
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
                    R.layout.course_item, parent, false);
            holder = new ViewHolder();
                    /*得到各个控件的对象*/
            holder.course_name = (TextView) convertView.findViewById(R.id.course_name);
            holder.teacher = (TextView) convertView.findViewById(R.id.teacher);
            holder.course_list_img = (ImageView) convertView.findViewById(R.id.course_list_img);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.course_name.setText(courseList.get(position).getCourseName());
        holder.teacher.setText("授课教师：" + courseList.get(position).getCourseTeacher());

        String courseId = courseList.get(position).getCourseId();
        switch (courseId) {
            case "056962237c964c0788ec1f62baeae43a":  //网络规划与设计
                holder.course_list_img.setBackgroundResource(R.drawable.course_bg1);
                break;
            case "11abb84641c645c0a12117c0a2157140":  //操作系统虚拟化
            case "82ebed7253fc4d2a858a57cf57326454":
                holder.course_list_img.setBackgroundResource(R.drawable.course_bg2);
                break;
            case "44a0b92f3c8f4c46b18b07a11018467f":  //hadoop
                holder.course_list_img.setBackgroundResource(R.drawable.course_bg3);
                break;
            case "c00c1222c0704893925f6ecf1461aa7d":  //高性能并行程序设计
                holder.course_list_img.setBackgroundResource(R.drawable.course_bg4);
                break;
            case "2488dbbe86da4d8ea523518d021c51bc":  //素质教育
                holder.course_list_img.setBackgroundResource(R.drawable.course_bg5);
                break;
            default:
                holder.course_list_img.setBackgroundResource(R.drawable.course_bg);
        }
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView course_name;
        TextView teacher;
        ImageView course_list_img;
    }
}
