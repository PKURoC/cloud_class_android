package com.vontroy.pku_ss_cloud_class.course.group.home;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.DocResult;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.FilePath;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscription;

/**
 * Created by vontroy on 2017-01-04.
 */

public class GroupDocUploadActivity extends BaseActivity {
    private static final int REQUEST_CODE = 1010;
    private EditText filePathET;
    private String uuid;
    private String filePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_doc_upload);
        Button selectFileBtn = (Button) findViewById(R.id.select_file_path);
        Button uploadFileBtn = (Button) findViewById(R.id.upload_selected_file);

        filePathET = (EditText) findViewById(R.id.file_path_to_upload);

        Bundle bundle = getIntent().getExtras();
        final String gid = bundle.getString("gid");
        final String sid = bundle.getString("sid");
        final String cid = bundle.getString("cid");
        final String token = bundle.getString("token");

        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择文件"), REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        });

        uploadFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePath = filePathET.getText().toString();

                Map<String, String> params = new HashMap<String, String>();
                params.put("gid", gid);
                params.put("sid", sid);
                params.put("cid", cid);
                params.put("token", token);
                addGroupDoc(params);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Uri uri = data.getData();
                String path = FilePath.getPath(this, uri);
                filePathET.setText(path);
                Log.d(TAG, "onActivityResult: " + FilePath.getPath(this, uri));
            }
        }
    }


    public void addGroupDoc(Map params) {
        Subscription subscription = ServerImp.getInstance()
                .common(GroupDocUploadActivity.class.getSimpleName(), Request.Method.POST, ServerInterface.addGroupDoc, params, DocResult.class)
                .subscribeOn(SchedulerProvider.getInstance().computation())
                .observeOn(SchedulerProvider.getInstance().ui())
                .subscribe(new Observer<DocResult>() {
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
                    public void onNext(DocResult docResult) {
                        Log.d("ddd", "onNext: ");
                        if (docResult.getCode().equals("0")) {
                            Log.d(TAG, "onNext: add Doc Success");
                            uuid = docResult.getUuid();
                            try {
                                ServerImp.getInstance().uploadMultipart(getApplicationContext(), filePath, uuid, false);
                            } catch (Exception exc) {
                                Log.e("AndroidUploadService", exc.getMessage(), exc);
                            }
                        }
                    }

                });
    }
}
