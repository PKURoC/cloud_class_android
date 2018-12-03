package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.BaseResult;
import com.vontroy.pku_ss_cloud_class.entry.DocInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscription;

/**
 * Created by vontroy on 17-1-2.
 */

public class GroupDocAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<DocInfo> docInfos;

    public GroupDocAdapter(Context context, ArrayList<DocInfo> docInfos) {
        mContext = context;
        this.docInfos = docInfos;
    }

    @Override
    public int getCount() {
        return docInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return docInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        GroupDocAdapter.ViewHolder holder;
        SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        final String token = getToken.getString("token", "");
        final String sid = getToken.getString("sid", "");

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.group_doc_item, parent, false);
            holder = new GroupDocAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.group_doc_item_name = (TextView) convertView.findViewById(R.id.group_doc_item_name);
            holder.delete_group_doc_file = (SimpleDraweeView) convertView.findViewById(R.id.delete_group_doc_file);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (GroupDocAdapter.ViewHolder) convertView.getTag();
        }
        holder.group_doc_item_name.setText(docInfos.get(position).getName());
        holder.delete_group_doc_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> params = new HashMap<String, String>();
                String uuid = docInfos.get(position).getUuid();

                params.put("token", token);
                params.put("sid", sid);
                params.put("uuid", uuid);

                deleteDocFile(params);
            }
        });
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView group_doc_item_name;
        SimpleDraweeView delete_group_doc_file;
    }

    public void deleteDocFile(Map params) {
        Subscription subscription = ServerImp.getInstance()
                .common(GroupDocAdapter.class.getSimpleName(), Request.Method.POST, ServerInterface.deleteGroupDoc, params, BaseResult.class)
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
                            Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
}
