package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.course.course_evaluate.CourseEvaluateActivity;
import com.vontroy.pku_ss_cloud_class.entry.CourseInfo;
import com.vontroy.pku_ss_cloud_class.main.MainActivity;

import java.util.List;

/**
 * Created by vontroy on 16-11-22.
 */

public class JoinedCourseAdapter extends BaseAdapter {
    private final Context mContext;
    private List<CourseInfo> courseList;
    private OnDropClickListener onDropClickListener;

    public JoinedCourseAdapter(Context context, List<CourseInfo> courseList) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        JoinedCourseAdapter.ViewHolder holder;
        final CourseInfo courseInfo = courseList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.joined_course_item, parent, false);
            holder = new JoinedCourseAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.course_name = (TextView) convertView.findViewById(R.id.course_name);
            holder.teacher = (TextView) convertView.findViewById(R.id.teacher);
            holder.drop_course = (TextView) convertView.findViewById(R.id.drop_course);
            holder.course_evaluate = (TextView) convertView.findViewById(R.id.course_evaluate);
            holder.joined_course_img = (ImageView) convertView.findViewById(R.id.joined_course_img);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (JoinedCourseAdapter.ViewHolder) convertView.getTag();
        }
        holder.course_name.setText(courseInfo.getCourseName());
        holder.teacher.setText(courseInfo.getCourseTeacher());
        holder.drop_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDropClickListener.dropClick(position);
            }
        });
        holder.course_evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CourseEvaluateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("course_info", courseInfo);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        String courseId = courseInfo.getCourseId();
        switch (courseId) {
            case "056962237c964c0788ec1f62baeae43a":  //网络规划与设计
                holder.joined_course_img.setBackgroundResource(R.drawable.course_bg1);
                break;
            case "11abb84641c645c0a12117c0a2157140":  //操作系统虚拟化
            case "82ebed7253fc4d2a858a57cf57326454":
                holder.joined_course_img.setBackgroundResource(R.drawable.course_bg2);
                break;
            case "44a0b92f3c8f4c46b18b07a11018467f":  //hadoop
                holder.joined_course_img.setBackgroundResource(R.drawable.course_bg3);
                break;
            case "c00c1222c0704893925f6ecf1461aa7d":  //高性能并行程序设计
                holder.joined_course_img.setBackgroundResource(R.drawable.course_bg4);
                break;
            case "2488dbbe86da4d8ea523518d021c51bc":  //素质教育
                holder.joined_course_img.setBackgroundResource(R.drawable.course_bg5);
                break;
            default:
                holder.joined_course_img.setBackgroundResource(R.drawable.course_bg);
        }
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView course_name;
        TextView teacher;
        TextView drop_course;
        TextView course_evaluate;
        ImageView joined_course_img;
    }

    public interface OnDropClickListener {
        void dropClick(int position);
    }

    public void setOnDropClickListener(OnDropClickListener onDropClickListener) {
        this.onDropClickListener = onDropClickListener;
    }

}
