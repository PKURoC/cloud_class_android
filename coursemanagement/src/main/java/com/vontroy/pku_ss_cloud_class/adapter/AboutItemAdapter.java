package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.entry.AboutInfo;

import java.util.ArrayList;

/**
 * Created by vontroy on 16-11-17.
 */

public class AboutItemAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<AboutInfo> aboutInfoItems;

    public AboutItemAdapter(Context context, ArrayList<AboutInfo> aboutInfos) {
        mContext = context;
        this.aboutInfoItems = aboutInfos;
    }

    @Override
    public int getCount() {
        return aboutInfoItems.size();
    }

    @Override
    public Object getItem(int i) {
        return aboutInfoItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AboutItemAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.about_list_item, parent, false);
            holder = new AboutItemAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.about_item = (TextView) convertView.findViewById(R.id.about_item);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (AboutItemAdapter.ViewHolder) convertView.getTag();
        }
        holder.about_item.setText(aboutInfoItems.get(position).getAboutItemName());
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView about_item;
    }
}
