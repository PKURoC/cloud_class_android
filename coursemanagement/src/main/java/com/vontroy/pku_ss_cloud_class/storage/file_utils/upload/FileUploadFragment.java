package com.vontroy.pku_ss_cloud_class.storage.file_utils.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.google.common.base.Strings;
import com.vontroy.abe_lib.algorithm.ABEFileUtils;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.StorageArrayResult;
import com.vontroy.pku_ss_cloud_class.data.StorageResult;
import com.vontroy.pku_ss_cloud_class.databinding.FileUploadFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.storage.StorageFragment;
import com.vontroy.pku_ss_cloud_class.storage.all.StorageAllContract;
import com.vontroy.pku_ss_cloud_class.utils.Constants;
import com.vontroy.pku_ss_cloud_class.utils.FilePath;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import rx.Observer;
import rx.Subscription;

import static android.content.ContentValues.TAG;

/**
 * Created by vontroy on 2017/11/15.
 */

public class FileUploadFragment extends Fragment implements FileUploadContract.View, TabHost.TabContentFactory {
    private static final String DEFAULT_POLICY = "(school:pku and academy:computer) or (籍贯:北京  and age:130)";
    private static final int UPLOAD_PATH = 1010;

    private FileUploadFragBinding fileUploadFragBinding;
    private FileUploadContract.Presenter mPresenter;
    private View mRootView;
    private String sid;
    private String token;
    private String jsonPK;
    private String jsonSK;
    private String filePath;
    private boolean isEncUpload = false;
    private File[] downloadedFileItems;
    ArrayList<StorageInfo> storageInfos;

    public static FileUploadFragment newInstance() {
        Bundle args = new Bundle();
        //args.putParcelableArrayList(StorageFragment.STORAGELIST, arrayList);
        FileUploadFragment fileUploadFragment = new FileUploadFragment();
        fileUploadFragment.setArguments(args);
        return fileUploadFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("666", "CurriculumFragment");
            mRootView = inflater.inflate(R.layout.file_upload_frag, container, false);
        }

        fileUploadFragBinding = FileUploadFragBinding.bind(mRootView);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        jsonPK = sharedPreferences.getString("public_key", "");
        jsonSK = sharedPreferences.getString("secret_key", "");
        sid = sharedPreferences.getString("sid", "");
        token = sharedPreferences.getString("token", "");
        final boolean[] enableEncrypt = {false};
        fileUploadFragBinding.enableEncrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    enableEncrypt[0] = true;
                } else {
                    enableEncrypt[0] = false;
                }
                isEncUpload = enableEncrypt[0];
            }
        });

        fileUploadFragBinding.getUploadPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择文件"), UPLOAD_PATH);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        });

        fileUploadFragBinding.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Strings.isNullOrEmpty(filePath)) {
                    Toast.makeText(getContext(), "请先选择要上传的文件！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (enableEncrypt[0]) {
                    ABEFileUtils fileUtils = new ABEFileUtils();
                    String tmpFilePath = Environment.getExternalStorageDirectory() + "/软微云课堂/tmp/";
                    File file = new File(tmpFilePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String tmpCTPath = "";
                    try {
                        long encStartTime = System.currentTimeMillis();
                        Log.d("time_state", "encrypt start time" + encStartTime);
                        tmpCTPath = fileUtils.encFile(filePath, tmpFilePath, DEFAULT_POLICY, jsonPK, "try it!".getBytes("utf-8"), sid);
                        long encEndTime = System.currentTimeMillis();
                        Log.d("time_state", "encrypt end time" + encEndTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Strings.isNullOrEmpty(tmpCTPath)) {
                        Toast.makeText(getActivity(), "加密失败!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        filePath = tmpCTPath;
                    }

                    Toast.makeText(getActivity(), "加密上传", Toast.LENGTH_SHORT).show();
                }

                Log.d("time_state", "file upload begin time: " + System.currentTimeMillis());
                Map<String, String> uploadParams = new HashMap<>();
                uploadParams.put("token", token);
                uploadParams.put("sid", sid);
                upLoadObject(uploadParams);

//                if (enableEncrypt[0]) {
//                    File toDelete = new File(filePath);
//                    toDelete.delete();
//                    Toast.makeText(getContext(), "加密上传成功", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "不加密上传成功", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        fileUploadFragBinding.uploadTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long startTime = System.currentTimeMillis();
                Log.d("time_state", "upload start time: " + startTime);
                if (Strings.isNullOrEmpty(filePath)) {
                    Toast.makeText(getContext(), "请先选择要上传的文件！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (enableEncrypt[0]) {
                    ABEFileUtils fileUtils = new ABEFileUtils();
                    String tmpFilePath = Environment.getExternalStorageDirectory() + "/软微云课堂/tmp/";
                    File file = new File(tmpFilePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    String tmpCTPath = "";
                    try {
                        long encStartTime = System.currentTimeMillis();
//                        Log.d("time_state", "encrypt start time" + encStartTime);
                        tmpCTPath = fileUtils.encFile(filePath, tmpFilePath, DEFAULT_POLICY, jsonPK, "try it!".getBytes("utf-8"), sid);
                        long encEndTime = System.currentTimeMillis();
//                        Log.d("time_state", "encrypt end time" + encEndTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Strings.isNullOrEmpty(tmpCTPath)) {
                        Toast.makeText(getActivity(), "加密失败!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        filePath = tmpCTPath;
                    }
                    Toast.makeText(getActivity(), "加密上传", Toast.LENGTH_SHORT).show();
                }

                JSONObject obj = new JSONObject();
                byte[] load = getBytes(filePath);
                obj.put("load", load);

                try {
                    String tmpFilePath = Environment.getExternalStorageDirectory() + "/软微云课堂/tmp/";
                    String tmpPlaintextPath = tmpFilePath + "plantext/" + new File(filePath).getName();
                    File tmpPlaintextPathDir = new File(tmpFilePath + "plantext/");
                    if (!tmpPlaintextPathDir.exists()) {
                        tmpPlaintextPathDir.mkdir();
                    }

                    FileOutputStream outputStream = new FileOutputStream(tmpPlaintextPath);
                    outputStream.write(obj.toJSONString().getBytes());
                    outputStream.close();
                    filePath = tmpPlaintextPath;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Map<String, String> uploadParams = new HashMap<>();
                uploadParams.put("token", token);
                uploadParams.put("sid", sid);
                upLoadObject(uploadParams);

//                if (enableEncrypt[0]) {
//                    File toDelete = new File(filePath);
//                    toDelete.delete();
//                    Toast.makeText(getContext(), "加密上传成功", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "不加密上传成功", Toast.LENGTH_SHORT).show();
//                }
            }
        });


        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UPLOAD_PATH) {
                Uri uri = data.getData();
                filePath = FilePath.getPath(getContext(), uri);
                fileUploadFragBinding.uploadPath.setText(filePath);
            }
        }
    }

    public void upLoadObject(Map params) {
        Subscription subscription = ServerImp.getInstance()
                .common(StorageFragment.class.getSimpleName(), Request.Method.POST, ServerInterface.uploadObject, params, StorageResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<StorageResult>() {
                    @Override
                    public void onCompleted() {
                        //mTaskDetailView.setLoadingIndicator(false);
                        Log.d("ddd", "onCompleted: ");
                        Toast.makeText(getContext(), "开始上传文件...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("ddd", "onError: ");
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(StorageResult storageResult) {
                        Log.d("ddd", "onNext: ");
                        if (storageResult.getCode().equals("0")) {
                            Log.d(TAG, "onNext: add Doc Success");
                            String uuid = storageResult.getUuid();
                            try {
                                ServerImp.getInstance().uploadMultipart(getContext(), filePath, uuid, isEncUpload);
                            } catch (Exception exc) {
                                Log.e("AndroidUploadService", exc.getMessage(), exc);
                            }
                        }
                    }

                });
    }


    @Override
    public View createTabContent(String s) {
        return null;
    }

    @Override
    public void setPresenter(FileUploadContract.Presenter presenter) {

    }
}
