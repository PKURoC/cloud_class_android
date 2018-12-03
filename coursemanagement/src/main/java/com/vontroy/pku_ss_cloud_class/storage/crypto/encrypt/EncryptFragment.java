package com.vontroy.pku_ss_cloud_class.storage.crypto.encrypt;

import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.widget.CompoundButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.common.base.Strings;
import com.vontroy.abe_lib.algorithm.ABEFileUtils;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.StorageArrayResult;
import com.vontroy.pku_ss_cloud_class.data.StorageResult;
import com.vontroy.pku_ss_cloud_class.databinding.EncryptFragBinding;
import com.vontroy.pku_ss_cloud_class.databinding.FileUploadFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.storage.StorageFragment;
import com.vontroy.pku_ss_cloud_class.utils.Constants;
import com.vontroy.pku_ss_cloud_class.utils.FilePath;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import rx.Observer;
import rx.Subscription;

/**
 * Created by vontroy on 2017/11/15.
 */

public class EncryptFragment extends Fragment implements EncryptContract.View, TabHost.TabContentFactory {
    private static final String DEFAULT_POLICY = "(school:pku and academy:computer) or (籍贯:北京  and age:130)";

    private static final int ENCRYPT_CODE = 1010;
    private static final int CT_PATH = 1100;

    private String encryptFilePath;
    private String ciphertextDir;

    private EncryptFragBinding encryptFragBinding;
    private EncryptContract.Presenter mPresenter;
    private View mRootView;
    private String sid;
    private String jsonPK;


    public static EncryptFragment newInstance() {
        Bundle args = new Bundle();
        //args.putParcelableArrayList(StorageFragment.STORAGELIST, arrayList);
        EncryptFragment fileUploadFragment = new EncryptFragment();
        fileUploadFragment.setArguments(args);
        return fileUploadFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("666", "CurriculumFragment");
            mRootView = inflater.inflate(R.layout.encrypt_frag, container, false);
        }

        encryptFragBinding = EncryptFragBinding.bind(mRootView);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        jsonPK = sharedPreferences.getString("public_key", "");
        sid = sharedPreferences.getString("sid", "");

        encryptFragBinding.editPolicy.setText(DEFAULT_POLICY);

        final ABEFileUtils fileUtils = new ABEFileUtils();

        encryptFragBinding.selectCiphertextPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, CT_PATH);
            }
        });

        encryptFragBinding.executeEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //设置密文策略
                String policy = encryptFragBinding.editPolicy.getText().toString();
                if (Strings.isNullOrEmpty(policy)) {
                    policy = DEFAULT_POLICY;
                }

                //设置明文地址
                String fileURL = encryptFragBinding.encryptFilePath.getText().toString();

                //设置生成密文存放的目录
//                String targetDirURL = abeLocalFileFragBinding.ciphertextPath.getText().toString();
                //TODO 如果路径不存在，创建一个
                String targetDirURL = Environment.getExternalStorageDirectory() + "/软微云课堂/ciphertext";

                File targetPath = new File(targetDirURL);
                if (!targetPath.exists()) {
                    targetPath.mkdir();
                }

                //加密文件，得到所在密文地址
                try {
                    String ciphertextURL = fileUtils.encFile(fileURL, targetDirURL, policy, jsonPK, "try it!".getBytes("utf-8"), sid);
                    Toast.makeText(getContext(), "文件加密成功!\n密文存储在" + ciphertextURL, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        encryptFragBinding.selectEncryptFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择文件"), ENCRYPT_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        });
        return mRootView;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ENCRYPT_CODE) {
                Uri uri = data.getData();
                encryptFilePath = FilePath.getPath(getContext(), uri);
                encryptFragBinding.encryptFilePath.setText(encryptFilePath);
            } else if (requestCode == CT_PATH) {
                Uri uri = data.getData();
                ciphertextDir = FilePath.getPath(getContext(), uri);
                encryptFragBinding.ciphertextPath.setText(ciphertextDir);
            }
        }
    }


    @Override
    public View createTabContent(String s) {
        return null;
    }

    @Override
    public void setPresenter(EncryptContract.Presenter presenter) {

    }
}
