package com.vontroy.pku_ss_cloud_class.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;

import java.util.ArrayList;

/**
 * Created by vontroy on 17-1-26.
 */

public class TransferListAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<StorageInfo> downloadedFiles;

    public TransferListAdapter(Context context, ArrayList<StorageInfo> downloadedFiles) {
        mContext = context;
        this.downloadedFiles = downloadedFiles;
    }

    @Override
    public int getCount() {
        return downloadedFiles.size();
    }

    @Override
    public Object getItem(int i) {
        return downloadedFiles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TransferListAdapter.ViewHolder holder;

        StorageInfo downloadedFile = downloadedFiles.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.file_transfer_item, parent, false);
            holder = new TransferListAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.file_name = (TextView) convertView.findViewById(R.id.file_name);
            holder.download_progressBar = (ProgressBar) convertView.findViewById(R.id.download_progressBar);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (TransferListAdapter.ViewHolder) convertView.getTag();
        }

        holder.file_name.setText(downloadedFile.getFileName());
        holder.download_progressBar.setMax(100);
        holder.download_progressBar.setProgress(100);

        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView file_name;
        ProgressBar download_progressBar;
    }
}
