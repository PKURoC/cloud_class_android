package com.vontroy.pku_ss_cloud_class.update;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.IDListener;

import static com.vontroy.pku_ss_cloud_class.network.ServerInterface.VERSION_INFO_URL;

/**
 * Created by vontroy on 2017/2/6.
 */

public class CheckVersionInfo extends AsyncTask<Void, Void, String> {
    private static final String TAG = "CheckVersionInfoTask";
    private ProgressDialog dialog;
    private Context mContext;
    private boolean mShowProgressDialog;
    private boolean mFirstOpenFlag;
    private String checkoutTag; //检查新版本操作的来源: "Manual":手动点击按钮检查, "Daily":每天一次系统自动检查

    private static final String dir = Environment.getExternalStorageDirectory() + "/软微云课堂/NewApkTemp";

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int apkFileLength;

    public CheckVersionInfo(Context context, boolean showProgressDialog) {
        this.mContext = context;
        this.mShowProgressDialog = showProgressDialog;
        this.mFirstOpenFlag = false;
        this.checkoutTag = "Manual";
    }

    public CheckVersionInfo(Context context, boolean showProgressDialog, boolean isFirstOpen) {
        this.mContext = context;
        this.mShowProgressDialog = showProgressDialog;
        this.mFirstOpenFlag = true;
        this.checkoutTag = "Manual";
    }

    public CheckVersionInfo(Context context, boolean showProgressDialog, String tag) {
        this.mContext = context;
        this.mShowProgressDialog = showProgressDialog;
        this.mFirstOpenFlag = false;
        this.checkoutTag = tag;
    }

    //初始化显示Dialog
    protected void onPreExecute() {
        if (mShowProgressDialog) {
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("正在检查最新版本信息");
            dialog.show();
        }
    }

    //在后台任务(子线程)中检查服务器的版本信息
    @Override
    protected String doInBackground(Void... params) {
        return getVersionInfo(VERSION_INFO_URL);
    }


    //后台任务执行完毕后，解除Dialog并且解析return返回的结果
    @Override
    protected void onPostExecute(String result) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        if (!TextUtils.isEmpty(result)) {
            parseJson(result);
        }
    }


    /**
     * 从服务器取得版本信息
     * {
     * "url":"http://crazyfzw.github.io/demo/auto-update-version/new-version-v2.0.apk",
     * "versionCode":2,
     * "updateMessage":"[1]新增视频弹幕功能<br/>[2]优化离线缓存功能<br/>[3]增强了稳定性"
     * }
     *
     * @return
     */
    public String getVersionInfo(String urlStr) {
        HttpURLConnection uRLConnection = null;
        InputStream is = null;
        BufferedReader buffer = null;
        String result = null;
        try {
            URL url = new URL(urlStr);
            uRLConnection = (HttpURLConnection) url.openConnection();
            uRLConnection.setRequestMethod("GET");
            is = uRLConnection.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(is));
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                strBuilder.append(line);
            }
            result = strBuilder.toString();
        } catch (Exception e) {
            Log.e(TAG, "http post error");
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException ignored) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {

                }
            }
            if (uRLConnection != null) {
                uRLConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * @param result
     */
    private void parseJson(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            String apkUrl = obj.getString("url");                 //APK下载路径
            String updateMessage = obj.getString("updateMessage");//版本更新说明
            int apkCode = obj.getInt("versionCode");              //新版APK对于的版本号

            //取得已经安装在手机的APP的版本号 versionCode
            int versionCode = getCurrentVersionCode();

            if (mFirstOpenFlag) {
                String[] urlSeg = apkUrl.split("/");
                String apkFileName = urlSeg[urlSeg.length - 1];
                String apkLocalPath = dir + "/" + apkFileName;
                deleteApk(apkLocalPath);
                return;
            }

            //对比版本号判断是否需要更新
            if (apkCode > versionCode) {
                showDialog(updateMessage, apkUrl);
            } else if (mShowProgressDialog) {
                if ("Manual".equals(checkoutTag)) {
                    Toast.makeText(mContext, "已经是最新版本", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "parse json error");
        }
    }

    /**
     * 取得当前版本号
     *
     * @return
     */
    public int getCurrentVersionCode() {

        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return 0;
    }


    /**
     * 显示对话框提示用户有新版本，并且让用户选择是否更新版本
     *
     * @param content
     * @param downloadUrl
     */
    public void showDialog(String content, final String downloadUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("发现新版本");
        builder.setMessage(Html.fromHtml(content))
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //下载apk文件
                        String[] urlSeg = downloadUrl.split("/");
                        String apkFileName = urlSeg[urlSeg.length - 1];
                        String apkLocalPath = dir + "/" + apkFileName;

                        File apkFile = new File(apkLocalPath);

                        if (apkFile.exists()) {
                            installApk(apkLocalPath);
                        } else {
                            if (Utils.isWifi(mContext)) {
                                goToDownloadApk(downloadUrl);
                            } else if (Utils.isNetworkConnected(mContext)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                                builder.setTitle("提示").setMessage("当前处于移动网络环境, 下载可能产生流量费用, 是否继续?")
                                        .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                goToDownloadApk(downloadUrl);
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
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog dialog = builder.create();
        //点击对话框外面,对话框不消失
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 用intent启用DownloadService服务去下载AKP文件
     *
     * @param downloadUrl
     */
    private void goToDownloadApk(String downloadUrl) {
        String urlStr = downloadUrl;
        String[] urlSeg = urlStr.split("/");
        final String name = urlSeg[urlSeg.length - 1];

        mNotifyManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle("下载更新")
                .setContentText("下载中...")
                .setSmallIcon(R.drawable.icon);

        try {
            DLManager.getInstance(mContext).dlStart(urlStr, dir, name, null, new IDListener() {
                @Override
                public void onPrepare() {

                }

                @Override
                public void onStart(String fileName, String realUrl, int fileLength) {
                    apkFileLength = fileLength;
                }

                @Override
                public void onProgress(int progress) {
                    mBuilder.setProgress(apkFileLength, progress, false);

                    double percentage = progress * 1D / apkFileLength;
                    NumberFormat percentVal = NumberFormat.getPercentInstance();
                    percentVal.setMinimumFractionDigits(1);

                    Log.d(TAG, "onProgress: percent" + percentage + "---" + percentVal.format(percentage));

                    mBuilder.setContentText("下载中... " + percentVal.format(percentage));
                    mNotifyManager.notify(0, mBuilder.build());
//                    holder.progress_bar.setProgress(progress);
                }

                @Override
                public void onStop(int progress) {

                }

                @Override
                public void onFinish(File file) {
                    Log.d(TAG, "onFinish: " + file.getPath());
//                    Toast.makeText(mContext, "下载成功!", Toast.LENGTH_SHORT).show();
                    mBuilder.setContentText("更新下载完成, 请点击此处进行安装!").setProgress(0, 0, false);

                    mNotifyManager.notify(0, mBuilder.build());

                    String apkFilePath = dir + "/" + name;

                    installApk(apkFilePath);

                    mNotifyManager.cancel(0);
                }

                @Override
                public void onError(int status, String error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        mNotifyManager.notify(0, mBuilder.build());
    }

    public void installApk(String path) {
        SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = getToken.edit();
        editor.putString("FIRST", "");
        editor.commit();

        File apkFile = new File(path);
        Uri uri = Uri.fromFile(apkFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        mContext.startActivity(intent);
    }

    private void deleteApk(String path) {
        File apkFile = new File(path);
        apkFile.delete();
        if (!apkFile.exists()) {
            SharedPreferences getToken = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = getToken.edit();
            editor.putString("FIRST", "deleted");
            editor.commit();
        }
    }

}
