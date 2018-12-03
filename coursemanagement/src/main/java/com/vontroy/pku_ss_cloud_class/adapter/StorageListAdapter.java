package com.vontroy.pku_ss_cloud_class.adapter;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.common.base.Strings;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.BaseResult;
import com.vontroy.pku_ss_cloud_class.data.CourseDocArrayResult;
import com.vontroy.pku_ss_cloud_class.data.CourseDocResult;
import com.vontroy.pku_ss_cloud_class.data.DocArrayResult;
import com.vontroy.pku_ss_cloud_class.data.DocResult;
import com.vontroy.pku_ss_cloud_class.data.JobArrayResult;
import com.vontroy.pku_ss_cloud_class.data.JobResult;
import com.vontroy.pku_ss_cloud_class.data.StorageArrayResult;
import com.vontroy.pku_ss_cloud_class.data.StorageResult;
import com.vontroy.pku_ss_cloud_class.entry.FileUtils;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.storage.StorageFragment;
import com.vontroy.pku_ss_cloud_class.utils.Constants;
import com.vontroy.pku_ss_cloud_class.utils.Utils;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.IDListener;
import rx.Observer;
import rx.Subscription;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;

/**
 * Created by vontroy on 16-11-17.
 */

public class StorageListAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<StorageInfo> storageList;
    private String tabTag;
    private File[] downloadedFileItems;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    public StorageListAdapter(Context context, ArrayList<StorageInfo> storageInfos, String tabTag) {
        mContext = context;
        this.storageList = storageInfos;
        this.tabTag = tabTag;
    }

    public StorageListAdapter(Context context, ArrayList<StorageInfo> storageInfos) {
        mContext = context;
        this.storageList = storageInfos;
        this.tabTag = "all";
    }

    @Override
    public int getCount() {
        return storageList.size();
    }

    @Override
    public Object getItem(int i) {
        return storageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final StorageListAdapter.ViewHolder holder;
        SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        final String token = getToken.getString("token", "");
        final String sid = getToken.getString("sid", "");
        final StorageInfo detailInfo = storageList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.storage_list_item, parent, false);
            holder = new StorageListAdapter.ViewHolder();
                    /*得到各个控件的对象*/
            holder.storage_pic = (SimpleDraweeView) convertView.findViewById(R.id.storage_pic);
            holder.file_name = (TextView) convertView.findViewById(R.id.file_name);
            holder.file_integrity_flag = (TextView) convertView.findViewById(R.id.file_integrity_flag);
            holder.download = (TextView) convertView.findViewById(R.id.download);
            holder.download_test = (TextView) convertView.findViewById(R.id.download_test);
            holder.delete = (TextView) convertView.findViewById(R.id.delete);
            holder.progress_bar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            holder.open_file = (TextView) convertView.findViewById(R.id.open_file);
            holder.file_status = (TextView) convertView.findViewById(R.id.file_status);
            holder.integrity_check = (TextView) convertView.findViewById(R.id.integrity_check);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (StorageListAdapter.ViewHolder) convertView.getTag();
        }

        if (detailInfo.isNone() && (Constants.StorageType.COURSE_WARE.equals(detailInfo.getStorageType()) || Constants.StorageType.COURSE_DATA.equals(detailInfo.getStorageType()))) {
            holder.progress_bar.setVisibility(GONE);
            holder.download.setVisibility(GONE);
            holder.download_test.setVisibility(GONE);
            holder.delete.setVisibility(GONE);
            holder.open_file.setVisibility(GONE);
            holder.storage_pic.setVisibility(GONE);
            holder.file_status.setVisibility(GONE);
            holder.file_integrity_flag.setVisibility(GONE);
            holder.integrity_check.setVisibility(GONE);
        }

        if (detailInfo.isLocalExists()) {
            holder.progress_bar.setProgress(holder.progress_bar.getMax());
        } else {
            holder.progress_bar.setProgress(0);
        }
        if ("0".equals(detailInfo.getIntegrity())) {
            holder.file_integrity_flag.setText("文件完整");
            holder.file_integrity_flag.setTextColor(Color.GREEN);
        } else {
            holder.file_integrity_flag.setText("文件损坏");
            holder.file_integrity_flag.setTextColor(Color.RED);
        }
        holder.file_name.setText(detailInfo.getFileName());
        holder.open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailInfo.isLocalExists()) {
                    String path = getFilePath(detailInfo);
                    File fileToOpen = new File(path);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(fileToOpen), getMimeType(fileToOpen.getAbsolutePath()));
                    mContext.startActivity(intent);
                } else {
                    new AlertDialog.Builder(mContext)
                            .setTitle("提示")
                            .setMessage("请先下载到本地!")
                            .setPositiveButton("知道了", null)
                            .show();
                }
            }
        });

        holder.integrity_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> params = new HashMap<>();
                params.put("sid", sid);
                params.put("token", token);
                params.put("uuid", detailInfo.getUuid());
                params.put("filename", detailInfo.getFileName());
                integrityCheck(params);
            }
        });

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setTitle("下载")
                        .setMessage("确认下载")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Utils.isWifi(mContext)) {
                                    String url = ServerInterface.download;
                                    String dir = "";
                                    Constants.StorageType storageType = detailInfo.getStorageType();
                                    switch (storageType) {
                                        case GROUP:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/小组资料";
                                            break;
                                        case STORAGE:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads/云盘";
                                            break;
                                        case JOB_ATTACH:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/作业信息/" + detailInfo.getJobName() + "/作业附件";
                                            break;
                                        case PERSONAL_JOB:
                                        case GROUP_JOB:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/作业信息/" + detailInfo.getJobName() + "/已提交作业";
                                            break;
                                        case COURSE_WARE:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/课件";
                                            break;
                                        case COURSE_DATA:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/课程资料";
                                            break;
                                    }

                                    String uuid = detailInfo.getUuid();
                                    String fileName = detailInfo.getFileName();
                                    String name = fileName;
                                    String groupId = detailInfo.getGroupId();
                                    String courseId = detailInfo.getCourseId();

                                    url += "?";
                                    url += "uuid=" + uuid;
                                    try {
                                        url += "&filename=" + URLEncoder.encode(fileName, "utf-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    url += "&sid=" + sid;

                                    if (storageType != null) {
                                        switch (storageType) {
                                            case GROUP:
                                            case GROUP_JOB:
                                                url += "&gid=" + groupId;
                                                url += "&cid=" + courseId;
                                                break;
                                            case PERSONAL_JOB:
                                                url += "&gid=job";
                                                url += "&cid=" + courseId;
                                                break;
                                            case JOB_ATTACH:
                                                url += "&gid=doc";
                                                url += "&cid=" + courseId;
                                                break;
                                            case COURSE_WARE:
                                            case COURSE_DATA:
                                                url += "&gid=doc";
                                                url += "&cid=" + courseId;
                                                break;
                                            case STORAGE:
                                                break;
                                            default:
                                                break;
                                        }
                                    }

                                    mNotifyManager =
                                            (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                    mBuilder = new NotificationCompat.Builder(mContext);
                                    mBuilder.setContentTitle("文件下载")
                                            .setContentText(detailInfo.getFileName())
                                            .setSmallIcon(R.drawable.icon);

                                    try {
                                        DLManager.getInstance(mContext).dlStart(url, dir, name, null, new IDListener() {
                                            @Override
                                            public void onPrepare() {

                                            }

                                            @Override
                                            public void onStart(String fileName, String realUrl, int fileLength) {
                                                long fileDownloadBeginTime = System.currentTimeMillis();
                                                Log.d("time_state", "file download begin time: " + fileDownloadBeginTime);
                                                holder.progress_bar.setMax(fileLength);
                                            }

                                            @Override
                                            public void onProgress(int progress) {
                                                holder.progress_bar.setProgress(progress);
                                                int fileLength = holder.progress_bar.getMax();
                                                mBuilder.setProgress(fileLength, progress, false);

                                                double percentage = progress * 1D / fileLength;
                                                NumberFormat percentVal = NumberFormat.getPercentInstance();
                                                percentVal.setMinimumFractionDigits(1);

                                                Log.d(TAG, "onProgress: percent" + percentage + "---" + percentVal.format(percentage));

                                                mBuilder.setContentText(detailInfo.getFileName() + " (" + percentVal.format(percentage) + ")");
                                                mNotifyManager.notify(0, mBuilder.build());
                                            }

                                            @Override
                                            public void onStop(int progress) {
                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                            }

                                            @Override
                                            public void onFinish(File file) {
                                                detailInfo.setLocalExists(true);
                                                Log.d(TAG, "onFinish: " + file.getPath());

                                                mBuilder.setContentText(detailInfo.getFileName() + " 下载完成!").setProgress(0, 0, false);
                                                mNotifyManager.notify(0, mBuilder.build());

                                                Toast.makeText(mContext, "下载成功!", Toast.LENGTH_SHORT).show();
                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                            }

                                            @Override
                                            public void onError(int status, String error) {
                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (Utils.isNetworkConnected(mContext)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                    builder.setTitle("提示").setMessage("当前处于移动网络环境, 下载可能产生流量费用, 是否继续?")
                                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    String url = ServerInterface.download;
                                                    String dir = "";
                                                    Constants.StorageType storageType = detailInfo.getStorageType();
                                                    switch (storageType) {
                                                        case GROUP:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/小组资料";
                                                            break;
                                                        case STORAGE:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads/云盘";
                                                            break;
                                                        case JOB_ATTACH:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/作业附件";
                                                            break;
                                                        case PERSONAL_JOB:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/个人作业";
                                                            break;
                                                        case GROUP_JOB:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/小组作业";
                                                            break;
                                                        case COURSE_WARE:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/课件";
                                                            break;
                                                        case COURSE_DATA:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/课程资料";
                                                            break;
                                                    }
                                                    String uuid = detailInfo.getUuid();
                                                    String fileName = detailInfo.getFileName();
                                                    String name = fileName;
                                                    String groupId = detailInfo.getGroupId();
                                                    String courseId = detailInfo.getCourseId();
                                                    url += "?";
                                                    url += "uuid=" + uuid;
                                                    url += "&filename=" + fileName;
                                                    url += "&sid=" + sid;

                                                    if (storageType != null) {
                                                        switch (storageType) {
                                                            case GROUP:
                                                            case GROUP_JOB:
                                                                url += "&gid=" + groupId;
                                                                url += "&cid=" + courseId;
                                                                break;
                                                            case PERSONAL_JOB:
                                                                url += "&gid=job";
                                                                url += "&cid=" + courseId;
                                                                break;
                                                            case JOB_ATTACH:
                                                                url += "&gid=doc";
                                                                url += "&cid=" + courseId;
                                                                break;
                                                            case COURSE_WARE:
                                                            case COURSE_DATA:
                                                                url += "&gid=doc";
                                                                url += "&cid=" + courseId;
                                                                break;
                                                            case STORAGE:
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                    }

                                                    mNotifyManager =
                                                            (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                                    mBuilder = new NotificationCompat.Builder(mContext);
                                                    mBuilder.setContentTitle("文件下载")
                                                            .setContentText(detailInfo.getFileName())
                                                            .setSmallIcon(R.drawable.icon);

                                                    try {
                                                        DLManager.getInstance(mContext).dlStart(url, dir, name, null, new IDListener() {
                                                            @Override
                                                            public void onPrepare() {

                                                            }

                                                            @Override
                                                            public void onStart(String fileName, String realUrl, int fileLength) {
                                                                holder.progress_bar.setMax(fileLength);
                                                                Log.d("time_state", "file download begin time: " + System.currentTimeMillis());
                                                            }

                                                            @Override
                                                            public void onProgress(int progress) {
                                                                holder.progress_bar.setProgress(progress);
                                                                int fileLength = holder.progress_bar.getMax();
                                                                mBuilder.setProgress(fileLength, progress, false);

                                                                double percentage = progress * 1D / fileLength;
                                                                NumberFormat percentVal = NumberFormat.getPercentInstance();
                                                                percentVal.setMinimumFractionDigits(1);

                                                                Log.d(TAG, "onProgress: percent" + percentage + "---" + percentVal.format(percentage));

                                                                mBuilder.setContentText(detailInfo.getFileName() + " (" + percentVal.format(percentage) + ")");
                                                                mNotifyManager.notify(0, mBuilder.build());
                                                            }

                                                            @Override
                                                            public void onStop(int progress) {
                                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                                            }

                                                            @Override
                                                            public void onFinish(File file) {
                                                                detailInfo.setLocalExists(true);
                                                                Log.d(TAG, "onFinish: " + file.getPath());

                                                                mBuilder.setContentText(detailInfo.getFileName() + " 下载完成!").setProgress(0, 0, false);
                                                                mNotifyManager.notify(0, mBuilder.build());

                                                                Toast.makeText(mContext, "下载成功!", Toast.LENGTH_SHORT).show();
                                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                                            }

                                                            @Override
                                                            public void onError(int status, String error) {
                                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                }
                                            }).show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                    builder.setTitle("提示").setMessage("无网络连接, 请检查您的网络状况后重试.")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            })
                                            .show();
                                }
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
            }
        });

        holder.download_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setTitle("下载")
                        .setMessage("确认下载")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (Utils.isWifi(mContext)) {
                                    String url = ServerInterface.download;
                                    String dir = "";
                                    Constants.StorageType storageType = detailInfo.getStorageType();
                                    switch (storageType) {
                                        case GROUP:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/小组资料";
                                            break;
                                        case STORAGE:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads/云盘";
                                            break;
                                        case JOB_ATTACH:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/作业信息/" + detailInfo.getJobName() + "/作业附件";
                                            break;
                                        case PERSONAL_JOB:
                                        case GROUP_JOB:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/作业信息/" + detailInfo.getJobName() + "/已提交作业";
                                            break;
                                        case COURSE_WARE:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/课件";
                                            break;
                                        case COURSE_DATA:
                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/课程资料";
                                            break;
                                    }

                                    String uuid = detailInfo.getUuid();
                                    String fileName = detailInfo.getFileName();
                                    String name = fileName;
                                    String groupId = detailInfo.getGroupId();
                                    String courseId = detailInfo.getCourseId();

                                    url += "?";
                                    url += "uuid=" + uuid;
                                    try {
                                        url += "&filename=" + URLEncoder.encode(fileName, "utf-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    url += "&sid=" + sid;

                                    if (storageType != null) {
                                        switch (storageType) {
                                            case GROUP:
                                            case GROUP_JOB:
                                                url += "&gid=" + groupId;
                                                url += "&cid=" + courseId;
                                                break;
                                            case PERSONAL_JOB:
                                                url += "&gid=job";
                                                url += "&cid=" + courseId;
                                                break;
                                            case JOB_ATTACH:
                                                url += "&gid=doc";
                                                url += "&cid=" + courseId;
                                                break;
                                            case COURSE_WARE:
                                            case COURSE_DATA:
                                                url += "&gid=doc";
                                                url += "&cid=" + courseId;
                                                break;
                                            case STORAGE:
                                                break;
                                            default:
                                                break;
                                        }
                                    }

                                    mNotifyManager =
                                            (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                    mBuilder = new NotificationCompat.Builder(mContext);
                                    mBuilder.setContentTitle("文件下载")
                                            .setContentText(detailInfo.getFileName())
                                            .setSmallIcon(R.drawable.icon);

                                    try {
                                        DLManager.getInstance(mContext).dlStart(url, dir, name, null, new IDListener() {
                                            @Override
                                            public void onPrepare() {

                                            }

                                            @Override
                                            public void onStart(String fileName, String realUrl, int fileLength) {
                                                holder.progress_bar.setMax(fileLength);
                                                Log.d("time_state", "file download begin time: " + System.currentTimeMillis());
                                            }

                                            @Override
                                            public void onProgress(int progress) {
                                                holder.progress_bar.setProgress(progress);
                                                int fileLength = holder.progress_bar.getMax();
                                                mBuilder.setProgress(fileLength, progress, false);

                                                double percentage = progress * 1D / fileLength;
                                                NumberFormat percentVal = NumberFormat.getPercentInstance();
                                                percentVal.setMinimumFractionDigits(1);

                                                Log.d(TAG, "onProgress: percent" + percentage + "---" + percentVal.format(percentage));

                                                mBuilder.setContentText(detailInfo.getFileName() + " (" + percentVal.format(percentage) + ")");
                                                mNotifyManager.notify(0, mBuilder.build());
                                            }

                                            @Override
                                            public void onStop(int progress) {
                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                            }

                                            @Override
                                            public void onFinish(File file) {
                                                detailInfo.setLocalExists(true);
                                                Log.d(TAG, "onFinish: " + file.getPath());

                                                try {
                                                    byte[] fileContent = getBytes(file.getPath());
                                                    FileOutputStream outputStream = new FileOutputStream(file.getPath());
                                                    JSONObject jsonObject = JSON.parseObject(new String(fileContent), JSONObject.class);
                                                    jsonObject.getBytes("load");
                                                    outputStream.write(jsonObject.getBytes("load"));
                                                    outputStream.close();

                                                } catch (FileNotFoundException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }


                                                mBuilder.setContentText(detailInfo.getFileName() + " 下载完成!").setProgress(0, 0, false);
                                                mNotifyManager.notify(0, mBuilder.build());
                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                                Toast.makeText(mContext, "下载成功!", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onError(int status, String error) {
                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (Utils.isNetworkConnected(mContext)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                    builder.setTitle("提示").setMessage("当前处于移动网络环境, 下载可能产生流量费用, 是否继续?")
                                            .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    String url = ServerInterface.download;
                                                    String dir = "";
                                                    Constants.StorageType storageType = detailInfo.getStorageType();
                                                    switch (storageType) {
                                                        case GROUP:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/小组资料";
                                                            break;
                                                        case STORAGE:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads/云盘";
                                                            break;
                                                        case JOB_ATTACH:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/作业附件";
                                                            break;
                                                        case PERSONAL_JOB:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/个人作业";
                                                            break;
                                                        case GROUP_JOB:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/小组作业";
                                                            break;
                                                        case COURSE_WARE:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/课件";
                                                            break;
                                                        case COURSE_DATA:
                                                            dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + detailInfo.getCourseName() + "/课程资料";
                                                            break;
                                                    }
                                                    String uuid = detailInfo.getUuid();
                                                    String fileName = detailInfo.getFileName();
                                                    String name = fileName;
                                                    String groupId = detailInfo.getGroupId();
                                                    String courseId = detailInfo.getCourseId();
                                                    url += "?";
                                                    url += "uuid=" + uuid;
                                                    url += "&filename=" + fileName;
                                                    url += "&sid=" + sid;

                                                    if (storageType != null) {
                                                        switch (storageType) {
                                                            case GROUP:
                                                            case GROUP_JOB:
                                                                url += "&gid=" + groupId;
                                                                url += "&cid=" + courseId;
                                                                break;
                                                            case PERSONAL_JOB:
                                                                url += "&gid=job";
                                                                url += "&cid=" + courseId;
                                                                break;
                                                            case JOB_ATTACH:
                                                                url += "&gid=doc";
                                                                url += "&cid=" + courseId;
                                                                break;
                                                            case COURSE_WARE:
                                                            case COURSE_DATA:
                                                                url += "&gid=doc";
                                                                url += "&cid=" + courseId;
                                                                break;
                                                            case STORAGE:
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                    }

                                                    mNotifyManager =
                                                            (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                                    mBuilder = new NotificationCompat.Builder(mContext);
                                                    mBuilder.setContentTitle("文件下载")
                                                            .setContentText(detailInfo.getFileName())
                                                            .setSmallIcon(R.drawable.icon);

                                                    try {
                                                        DLManager.getInstance(mContext).dlStart(url, dir, name, null, new IDListener() {
                                                            @Override
                                                            public void onPrepare() {

                                                            }

                                                            @Override
                                                            public void onStart(String fileName, String realUrl, int fileLength) {
                                                                Log.d("time_state", "file download begin time: " + System.currentTimeMillis());
                                                                holder.progress_bar.setMax(fileLength);
                                                            }

                                                            @Override
                                                            public void onProgress(int progress) {
                                                                holder.progress_bar.setProgress(progress);
                                                                int fileLength = holder.progress_bar.getMax();
                                                                mBuilder.setProgress(fileLength, progress, false);

                                                                double percentage = progress * 1D / fileLength;
                                                                NumberFormat percentVal = NumberFormat.getPercentInstance();
                                                                percentVal.setMinimumFractionDigits(1);

                                                                Log.d(TAG, "onProgress: percent" + percentage + "---" + percentVal.format(percentage));

                                                                mBuilder.setContentText(detailInfo.getFileName() + " (" + percentVal.format(percentage) + ")");
                                                                mNotifyManager.notify(0, mBuilder.build());
                                                            }

                                                            @Override
                                                            public void onStop(int progress) {
                                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                                            }

                                                            @Override
                                                            public void onFinish(File file) {
                                                                detailInfo.setLocalExists(true);
                                                                Log.d(TAG, "onFinish: " + file.getPath());

                                                                try {
                                                                    byte[] fileContent = getBytes(file.getPath());
                                                                    FileOutputStream outputStream = new FileOutputStream(file.getPath());
                                                                    JSONObject jsonObject = JSON.parseObject(new String(fileContent), JSONObject.class);
                                                                    jsonObject.getBytes("load");
                                                                    outputStream.write(jsonObject.getBytes("load"));
                                                                    outputStream.close();

                                                                } catch (FileNotFoundException e) {
                                                                    e.printStackTrace();
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }


                                                                mBuilder.setContentText(detailInfo.getFileName() + " 下载完成!").setProgress(0, 0, false);
                                                                mNotifyManager.notify(0, mBuilder.build());

                                                                Toast.makeText(mContext, "下载成功!", Toast.LENGTH_SHORT).show();
                                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                                            }

                                                            @Override
                                                            public void onError(int status, String error) {
                                                                Log.d("time_state", "file download end time: " + System.currentTimeMillis());
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                }
                                            }).show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                    builder.setTitle("提示").setMessage("无网络连接, 请检查您的网络状况后重试.")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            })
                                            .show();
                                }
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
            }
        });

        if (Constants.StorageType.JOB_ATTACH.equals(detailInfo.getStorageType())) {
            holder.delete.setVisibility(GONE);
        } else {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String[] items = new String[]{"仅删除本地文件", "仅删除云端文件", "删除本地及云端文件"};
                    final BtnOnClick deleteOnclick = new BtnOnClick(-1);

                    if (Constants.StorageType.COURSE_DATA.equals(detailInfo.getStorageType()) || Constants.StorageType.COURSE_WARE.equals(detailInfo.getStorageType())) {
                        String deleteFilePath = getFilePath(detailInfo);
                        File toDelete = new File(deleteFilePath);
                        if (toDelete.exists()) {
                            new AlertDialog.Builder(mContext)
                                    .setTitle("删除文件")
                                    .setMessage("确认删除已下载文件?")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String deletePath = getFilePath(detailInfo);
                                            File fileToDelete = new File(deletePath);

                                            if (fileToDelete.exists()) {
                                                fileToDelete.delete();
                                                detailInfo.setLocalExists(false);
                                                holder.progress_bar.setProgress(0);
                                                Toast.makeText(mContext, "本地文件 " + "[" + detailInfo.getFileName() + "]" + " 已成功删除", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                        } else {
                            new AlertDialog.Builder(mContext)
                                    .setTitle("删除文件")
                                    .setMessage("本地文件不存在, 无需删除!")
                                    .setPositiveButton("确定", null)
                                    .show();
                        }
                    } else {
                        new AlertDialog.Builder(mContext)
                                .setTitle("删除文件")
                                .setSingleChoiceItems(items, -1, deleteOnclick)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (deleteOnclick.getIndex() == 0) {
                                            String deletePath = getFilePath(detailInfo);
                                            File fileToDelete = new File(deletePath);
                                            if (fileToDelete.exists()) {
                                                fileToDelete.delete();
                                                detailInfo.setLocalExists(false);
                                                holder.progress_bar.setProgress(0);
                                                Toast.makeText(mContext, "本地文件 " + "[" + detailInfo.getFileName() + "]" + " 已成功删除", Toast.LENGTH_SHORT).show();
                                            }
                                        } else if (deleteOnclick.getIndex() == 1) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                            builder.setTitle("提示").setMessage("云端文件删除后无法恢复, 是否继续?.")
                                                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Map<String, String> params = new HashMap<String, String>();
                                                            String uuid = detailInfo.getUuid();
                                                            Constants.StorageType storageType = detailInfo.getStorageType();
                                                            String gid = detailInfo.getGroupId();
                                                            String cid = detailInfo.getCourseId();
                                                            String courseName = detailInfo.getCourseName();
                                                            String jobName = detailInfo.getJobName();
                                                            params.put("token", token);
                                                            params.put("sid", sid);
                                                            if (!Strings.isNullOrEmpty(uuid)) {
                                                                params.put("uuid", uuid);
                                                            }

                                                            if (!Strings.isNullOrEmpty(gid)) {
                                                                params.put("gid", gid);
                                                            }

                                                            if (!Strings.isNullOrEmpty(cid)) {
                                                                params.put("cid", cid);
                                                            }

                                                            switch (storageType) {
                                                                case STORAGE:
                                                                    deleteStorageFile(params);
                                                                    break;
                                                                case GROUP:
                                                                    deleteGroupDoc(params);
                                                                    break;
                                                                case GROUP_JOB:
                                                                case PERSONAL_JOB:
                                                                    params.put("jid", detailInfo.getJobid());
                                                                    deleteJobDoc(params, courseName, jobName, detailInfo.getJobid());
                                                                    break;
                                                                case COURSE_WARE:
                                                                    deleteCourseDoc(params, Constants.StorageType.COURSE_WARE, courseName);
                                                                    break;
                                                                case COURSE_DATA:
                                                                    deleteCourseDoc(params, Constants.StorageType.COURSE_DATA, courseName);
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("取消", null)
                                                    .show();
                                        } else if (deleteOnclick.getIndex() == 2) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                            builder.setTitle("提示").setMessage("云端文件删除后无法恢复, 是否继续?.")
                                                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            String deletePath = getFilePath(detailInfo);
                                                            File fileToDelete = new File(deletePath);
                                                            if (fileToDelete.exists()) {
                                                                fileToDelete.delete();
                                                                detailInfo.setLocalExists(false);
                                                                holder.progress_bar.setProgress(0);
                                                                Toast.makeText(mContext, "本地文件 " + "[" + detailInfo.getFileName() + "]" + " 已成功删除", Toast.LENGTH_SHORT).show();
                                                            }

                                                            Map<String, String> params = new HashMap<String, String>();
                                                            String uuid = detailInfo.getUuid();
                                                            Constants.StorageType storageType = detailInfo.getStorageType();
                                                            String gid = detailInfo.getGroupId();
                                                            String cid = detailInfo.getCourseId();
                                                            String courseName = detailInfo.getCourseName();
                                                            String jobName = detailInfo.getJobName();
                                                            params.put("token", token);
                                                            params.put("sid", sid);
                                                            if (!Strings.isNullOrEmpty(uuid)) {
                                                                params.put("uuid", uuid);
                                                            }

                                                            if (!Strings.isNullOrEmpty(gid)) {
                                                                params.put("gid", gid);
                                                            }

                                                            if (!Strings.isNullOrEmpty(cid)) {
                                                                params.put("cid", cid);
                                                            }

                                                            switch (storageType) {
                                                                case STORAGE:
                                                                    deleteStorageFile(params);
                                                                    break;
                                                                case GROUP:
                                                                    deleteGroupDoc(params);
                                                                    break;
                                                                case GROUP_JOB:
                                                                case PERSONAL_JOB:
                                                                    params.put("jid", detailInfo.getJobid());
                                                                    deleteJobDoc(params, courseName, jobName, detailInfo.getJobid());
                                                                    break;
                                                                case COURSE_WARE:
//                                                    deleteCourseDoc(params, Constants.StorageType.COURSE_WARE, courseName);
                                                                    break;
                                                                case COURSE_DATA:
//                                                    deleteCourseDoc(params, Constants.StorageType.COURSE_DATA, courseName);
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("取消", null)
                                                    .show();
                                        }
                                    }
                                })
                                .setNegativeButton("取消", deleteOnclick)
                                .show();
                    }
                }
            });
        }
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        TextView file_name;
        TextView download;
        TextView download_test;
        TextView delete;
        TextView open_file;
        ProgressBar progress_bar;
        SimpleDraweeView storage_pic;
        TextView file_integrity_flag;
        TextView file_status;
        TextView integrity_check;
    }

    //// TODO: 17-1-25
    public void deleteJobDoc(final Map<String, String> params, final String courseName, final String jobName, final String jobId) {
        Subscription subscription = ServerImp.getInstance()
                .common(StorageListAdapter.class.getSimpleName(), Request.Method.POST, ServerInterface.deleteJob, params, BaseResult.class)
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
                            Toast.makeText(mContext, "云端文件删除成功", Toast.LENGTH_SHORT).show();
                            storageList.clear();
                            getJobs(params, courseName, jobName, jobId);
                        }
                    }

                });
    }

    public void deleteCourseDoc(final Map<String, String> params, final Constants.StorageType storageType, final String courseName) {
        Subscription subscription = ServerImp.getInstance()
                .common(StorageListAdapter.class.getSimpleName(), Request.Method.POST, ServerInterface.deleteCourseDoc, params, BaseResult.class)
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
                            Toast.makeText(mContext, "云端文件删除成功", Toast.LENGTH_SHORT).show();
                            storageList.clear();
                            getCourseDocs(params, storageType, courseName);
                        }
                    }

                });
    }

    public void deleteGroupDoc(final Map<String, String> params) {
        Subscription subscription = ServerImp.getInstance()
                .common(StorageListAdapter.class.getSimpleName(), Request.Method.POST, ServerInterface.deleteGroupDoc, params, BaseResult.class)
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
                            Toast.makeText(mContext, "云端文件删除成功", Toast.LENGTH_SHORT).show();
                            storageList.clear();
                            getGroupDocs(params);
                        }
                    }

                });
    }

    public void deleteStorageFile(final Map params) {
        final Map<String, String> getStorageListParams = params;
        Subscription subscription = ServerImp.getInstance()
                .common(StorageListAdapter.class.getSimpleName(), Request.Method.POST, ServerInterface.deleteObject, params, BaseResult.class)
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
                            Toast.makeText(mContext, "云端文件删除成功", Toast.LENGTH_SHORT).show();
                            storageList.clear();
                            getCloudObjects(getStorageListParams);
                        }
                    }

                });
    }

    public void getCloudObjects(Map<String, String> params) {
        Subscription subscription = ServerImp.getInstance()
                .common(StorageFragment.class.getSimpleName(), Request.Method.GET, ServerInterface.getCloudObjects, params, StorageArrayResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<StorageArrayResult>() {
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
                    public void onNext(StorageArrayResult storageArrayResult) {
                        Log.d("ddd", "onNext: ");
                        if (storageArrayResult.getCode().equals("0")) {
                            readDownloadedFiles(Constants.StorageType.STORAGE, "", "");
                            ArrayList<StorageResult> storageResults = storageArrayResult.getData();
                            for (StorageResult storageResult : storageResults) {
                                StorageInfo storageInfo = new StorageInfo("", "");
                                //TODO
                                String name = storageResult.getFilename();
                                String uuid = storageResult.getUuid();

                                storageInfo.setFileName(name);
                                storageInfo.setUuid(uuid);
                                storageInfo.setIntegrity(storageResult.getIntegrity());
                                storageInfo.setStorageType(Constants.StorageType.STORAGE);
                                storageInfo.setLocalExists(false);
                                for (File file : downloadedFileItems) {
                                    String fileName = file.getName();
                                    if (fileName.equals(name)) {
                                        storageInfo.setLocalExists(true);
                                        break;
                                    } else {
                                        storageInfo.setLocalExists(false);
                                    }
                                }

                                String[] nameSeg = name.split("\\.");
                                int len = nameSeg.length;
                                int pos = len <= 0 ? 0 : len - 1;
                                String type = nameSeg[pos];

                                switch (type.toLowerCase()) {
                                    case "txt":
                                    case "pdf":
                                    case "doc":
                                    case "xls":
                                    case "ppt":
                                    case "pptx":
                                        storageInfo.setType(FileUtils.FileType.DOC);
                                        break;

                                    case "jpg":
                                    case "jpeg":
                                    case "png":
                                    case "bmp":
                                        storageInfo.setType(FileUtils.FileType.IMG);
                                        break;

                                    case "mp4":
                                    case "avi":
                                    case "wmv":
                                    case "rmvb":
                                    case "rm":
                                    case "flash":
                                    case "swf":
                                    case "3gp":
                                        storageInfo.setType(FileUtils.FileType.VIDEO);
                                        break;

                                    case "mp3":
                                    case "wav":
                                    case "mid":
                                    case "wma":
                                        storageInfo.setType(FileUtils.FileType.MUSIC);
                                        break;

                                    default:
                                        storageInfo.setType(FileUtils.FileType.DEFAULT);
                                }

                                if ("all".equals(tabTag) && FileUtils.FileType.DEFAULT.equals(storageInfo.getType())) {
                                    storageList.add(storageInfo);
                                } else if ("doc".equals(tabTag) && FileUtils.FileType.DOC.equals(storageInfo.getType())) {
                                    storageList.add(storageInfo);
                                } else if ("img".equals(tabTag) && FileUtils.FileType.IMG.equals(storageInfo.getType())) {
                                    storageList.add(storageInfo);
                                } else if ("music".equals(tabTag) && FileUtils.FileType.MUSIC.equals(storageInfo.getType())) {
                                    storageList.add(storageInfo);
                                } else if ("video".equals(tabTag) && FileUtils.FileType.VIDEO.equals(storageInfo.getType())) {
                                    storageList.add(storageInfo);
                                } else {
                                    storageList.add(storageInfo);
                                }

                            }
                        }
                        notifyDataSetChanged();
                    }

                });
    }

    public void getGroupDocs(Map<String, String> params) {
        final String groupId = params.get("gid");
        final String courseId = params.get("cid");
        Subscription subscription = ServerImp.getInstance()
                .common(StorageListAdapter.class.getSimpleName(), Request.Method.GET, ServerInterface.getGroupDocs, params, DocArrayResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<DocArrayResult>() {
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
                    public void onNext(DocArrayResult docArrayResult) {
                        Log.d("ddd", "onNext: ");
                        if (docArrayResult.getCode().equals("0")) {
                            Log.d(TAG, "onNext: ");
                            ArrayList<DocResult> docResults = docArrayResult.getData();

                            readDownloadedFiles(Constants.StorageType.GROUP, "", "");
                            for (DocResult docResult : docResults) {
                                StorageInfo docInfo = new StorageInfo("", "");
                                String name = docResult.getFilename();
                                docInfo.setFileName(name);
                                docInfo.setUuid(docResult.getUuid());
                                docInfo.setStorageType(Constants.StorageType.GROUP);
                                docInfo.setGroupId(groupId);
                                docInfo.setIntegrity(docResult.getIntegrity());
                                docInfo.setCourseId(courseId);

                                docInfo.setLocalExists(false);
                                for (File file : downloadedFileItems) {
                                    String fileName = file.getName();
                                    if (fileName.equals(name)) {
                                        docInfo.setLocalExists(true);
                                        break;
                                    } else {
                                        docInfo.setLocalExists(false);
                                    }
                                }

                                storageList.add(docInfo);
                            }
                        }
                        notifyDataSetChanged();
                    }

                });
    }

    public void getJobs(Map<String, String> params, final String courseName, final String jobName, final String jobId) {
        Subscription subscription = ServerImp.getInstance()
                .common(StorageListAdapter.class.getSimpleName(), Request.Method.GET, ServerInterface.getMyJobs, params, JobArrayResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<JobArrayResult>() {
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
                    public void onNext(JobArrayResult jobArrayResult) {
                        Log.d("ddd", "onNext: ");
                        if (jobArrayResult.getCode().equals("0")) {
                            ArrayList<JobResult> finishedJobResults = jobArrayResult.getFinished();
                            for (JobResult finishedJobResult : finishedJobResults) {
                                StorageInfo storageInfo = new StorageInfo("", "");
                                String name = finishedJobResult.getFilename();
                                storageInfo.setFileName(name);
                                storageInfo.setJobid(finishedJobResult.getJobid());
                                storageInfo.setUuid(finishedJobResult.getUuid());
                                storageInfo.setIntegrity(finishedJobResult.getIntegrity());
                                if ("2".equals(finishedJobResult.getType())) {
                                    storageInfo.setStorageType(Constants.StorageType.PERSONAL_JOB);
                                } else if ("1".equals(finishedJobResult.getType())) {
                                    storageInfo.setStorageType(Constants.StorageType.GROUP_JOB);
                                }
                                if (finishedJobResult.getJobid().equals(jobId)) {
                                    storageList.add(storageInfo);
                                }
                            }

                            readDownloadedFiles(Constants.StorageType.PERSONAL_JOB, courseName, jobName);
                            for (StorageInfo storageInfo : storageList) {
                                storageInfo.setLocalExists(false);
                                for (File file : downloadedFileItems) {
                                    String fileName = file.getName();
                                    if (fileName.equals(storageInfo.getFileName())) {
                                        storageInfo.setLocalExists(true);
                                        break;
                                    }
                                }
                            }

                            readDownloadedFiles(Constants.StorageType.GROUP_JOB, courseName, jobName);
                            for (StorageInfo storageInfo : storageList) {
                                for (File file : downloadedFileItems) {
                                    String fileName = file.getName();
                                    if (fileName.equals(storageInfo.getFileName())) {
                                        storageInfo.setLocalExists(true);
                                        break;
                                    }
                                }
                            }

                            notifyDataSetChanged();
                        }
                    }

                });
    }

    //TODO
    public void getCourseDocs(Map params, final Constants.StorageType storageType, final String courseName) {
        Subscription subscription = ServerImp.getInstance()
                .common(StorageListAdapter.class.getSimpleName(), Request.Method.GET, ServerInterface.getCourseDocs, params, CourseDocArrayResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<CourseDocArrayResult>() {
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
                    public void onNext(CourseDocArrayResult courseDocArrayResult) {
                        Log.d("ddd", "onNext: ");
                        if ("0".equals(courseDocArrayResult.getCode())) {
                            ArrayList<CourseDocResult> courseDocResults = courseDocArrayResult.getData();
                            for (CourseDocResult courseDocResult : courseDocResults) {
                                String fileName = courseDocResult.getFilename();
                                String[] fileNameSeg = fileName.split("\\.");
                                String type;
                                int segLen = fileNameSeg.length;
                                if (segLen == 1) {
                                    type = "other";
                                } else {
                                    type = fileNameSeg[segLen - 1];
                                }

                                StorageInfo storageInfo = new StorageInfo("", "");
                                storageInfo.setFileName(fileName);
                                storageInfo.setUuid(courseDocResult.getUuid());
                                storageInfo.setIntegrity(courseDocResult.getIntegrity());
                                storageInfo.setRemarks(type);

                                if ("0".equals(courseDocResult.getType()) && Constants.StorageType.COURSE_WARE.equals(storageType)) {
                                    storageList.add(storageInfo);
                                } else if ("1".equals(courseDocResult.getType()) && Constants.StorageType.COURSE_DATA.equals(storageType)) {
                                    storageList.add(storageInfo);
                                }
                            }

                            readDownloadedFiles(Constants.StorageType.COURSE_WARE, courseName, "");
                            for (StorageInfo storageInfo : storageList) {
                                storageInfo.setLocalExists(false);
                                for (File file : downloadedFileItems) {
                                    String fileName = file.getName();
                                    if (fileName.equals(storageInfo.getFileName())) {
                                        storageInfo.setLocalExists(true);
                                        break;
                                    }
                                }
                            }

                            readDownloadedFiles(Constants.StorageType.COURSE_DATA, courseName, "");
                            for (StorageInfo storageInfo : storageList) {
                                for (File file : downloadedFileItems) {
                                    String fileName = file.getName();
                                    if (fileName.equals(storageInfo.getFileName())) {
                                        storageInfo.setLocalExists(true);
                                        break;
                                    }
                                }
                            }

                            notifyDataSetChanged();
                        }
                    }

                });
    }

    public void integrityCheck(final Map<String, String> params) {
        Subscription subscription = ServerImp.getInstance()
                .common(StorageListAdapter.class.getSimpleName(), Request.Method.POST, ServerInterface.integrityCheckUrl, params, BaseResult.class)
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
                            Toast.makeText(mContext, "文件完整", Toast.LENGTH_SHORT).show();
                        } else if (baseResult.getCode().equals("1")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                            builder.setTitle("提示").setMessage("文件损坏, 是否修复?")
                                    .setPositiveButton("修复", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            integrityRecover(params);
                                        }
                                    })
                                    .setNegativeButton("取消", null).show();
                            Toast.makeText(mContext, "文件损坏", Toast.LENGTH_SHORT).show();
                        } else if (baseResult.getCode().equals("3")) {
                            Toast.makeText(mContext, "系统错误", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    private void integrityRecover(Map<String, String> params) {
        Subscription subscription = ServerImp.getInstance()
                .common(StorageListAdapter.class.getSimpleName(), Request.Method.POST, ServerInterface.integrityRecoverUrl, params, BaseResult.class)
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
                            Toast.makeText(mContext, "文件恢复成功!", Toast.LENGTH_SHORT).show();
                        } else if (baseResult.getCode().equals("3")) {
                            Toast.makeText(mContext, "操作失败!", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    private void readDownloadedFiles(Constants.StorageType storageType, String courseName, String jobName) {
        String dir = "";
        switch (storageType) {
            case GROUP:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/小组资料";
                break;
            case STORAGE:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads/云盘";
                break;
            case JOB_ATTACH:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/作业信息/" + jobName + "/作业附件";
                break;
            case PERSONAL_JOB:
            case GROUP_JOB:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/作业信息/" + jobName + "/已提交作业";
                break;
            case COURSE_WARE:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/课件";
                break;
            case COURSE_DATA:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/课程资料";
                break;
        }

        File downloadedFileDir = new File(dir);

        if (!downloadedFileDir.exists()) {
            downloadedFileDir.mkdir();
        } else {
            downloadedFileItems = downloadedFileDir.listFiles();
        }
    }

    private String getFilePath(StorageInfo storageInfo) {
        String dir = "";
        String courseName = storageInfo.getCourseName();
        String jobName = storageInfo.getJobName();

        Constants.StorageType storageType = storageInfo.getStorageType();
        switch (storageType) {
            case GROUP:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/小组资料";
                break;
            case STORAGE:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads/云盘";
                break;
            case JOB_ATTACH:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/作业信息/" + jobName + "/作业附件";
                break;
            case PERSONAL_JOB:
            case GROUP_JOB:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/作业信息/" + jobName + "/已提交作业";
                break;
            case COURSE_WARE:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/课件";
                break;
            case COURSE_DATA:
                dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads" + "/" + courseName + "/课程资料";
                break;
        }
        return dir + "/" + storageInfo.getFileName();
    }

    private String getMimeType(String url) {
        String parts[] = url.split("\\.");
        String extension = parts[parts.length - 1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private class BtnOnClick implements DialogInterface.OnClickListener {
        private int index;

        public BtnOnClick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int idx) {
            if (idx >= 0) {
                index = idx;
            } else {
                if (idx == DialogInterface.BUTTON_POSITIVE) {
                    Toast.makeText(mContext, "index: " + index, Toast.LENGTH_SHORT).show();
                } else if (idx == DialogInterface.BUTTON_NEGATIVE) {

                }
            }
        }

        public int getIndex() {
            return this.index;
        }
    }

    private byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
