package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.entry.ProfileInfo;

import java.util.ArrayList;

/**
 * Created by vontroy on 16-11-17.
 */

public class ProfileItemAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<ProfileInfo> profileInfoItems;

    public ProfileItemAdapter(Context context, ArrayList<ProfileInfo> profileInfos) {
        mContext = context;
        this.profileInfoItems = profileInfos;
    }

    @Override
    public int getCount() {
        return profileInfoItems.size();
    }

    @Override
    public Object getItem(int i) {
        return profileInfoItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProfileItemAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.profile_info_item, parent, false);
            holder = new ProfileItemAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.profile_item = (TextView) convertView.findViewById(R.id.profile_item);
            holder.item_icon = (ImageView) convertView.findViewById(R.id.item_icon);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ProfileItemAdapter.ViewHolder) convertView.getTag();
        }
        holder.profile_item.setText(profileInfoItems.get(position).getItemName());
        if (position == 0) {
            holder.item_icon.setImageResource(R.drawable.my_msg_icon);
        } else if (position == 1) {
            holder.item_icon.setImageResource(R.drawable.my_storage_icon);
        } else if (position == 2) {
            holder.item_icon.setImageResource(R.drawable.about_app_icon);
        } else if (position == 3 && getCount() == 4) {
            holder.item_icon.setImageResource(R.drawable.quit_login_icon);
        } else if (position == 3 && getCount() == 5) {
            holder.item_icon.setImageResource(R.drawable.goto_reg_icon);
        } else if (position == 4 && getCount() == 5) {
            holder.item_icon.setImageResource(R.drawable.goto_login_icon);
        }
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView profile_item;
        ImageView item_icon;
    }
}
