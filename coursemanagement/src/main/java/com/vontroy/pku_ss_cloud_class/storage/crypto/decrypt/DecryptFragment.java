package com.vontroy.pku_ss_cloud_class.storage.crypto.decrypt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.vontroy.abe_lib.component.Decryptor;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.StorageArrayResult;
import com.vontroy.pku_ss_cloud_class.data.StorageResult;
import com.vontroy.pku_ss_cloud_class.databinding.DecryptFragBinding;
import com.vontroy.pku_ss_cloud_class.databinding.FileUploadFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.storage.StorageFragment;
import com.vontroy.pku_ss_cloud_class.utils.Constants;
import com.vontroy.pku_ss_cloud_class.utils.FilePath;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import rx.Observer;
import rx.Subscription;

/**
 * Created by vontroy on 2017/11/15.
 */

public class DecryptFragment extends Fragment implements DecryptContract.View, TabHost.TabContentFactory {
    private String decryptFilePath;
    private String plaintextDir;
    private String jsonPK;
    private String jsonSK;
    private String sid;

    private static final int DECRYPT_CODE = 1011;
    private static final int PLAINTEXT_PATH = 1101;


    private DecryptFragBinding decryptFragBinding;
    private DecryptContract.Presenter mPresenter;
    private View mRootView;

    public static DecryptFragment newInstance() {
        Bundle args = new Bundle();
        //args.putParcelableArrayList(StorageFragment.STORAGELIST, arrayList);
        DecryptFragment fileUploadFragment = new DecryptFragment();
        fileUploadFragment.setArguments(args);
        return fileUploadFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("666", "CurriculumFragment");
            mRootView = inflater.inflate(R.layout.decrypt_frag, container, false);
        }

        decryptFragBinding = DecryptFragBinding.bind(mRootView);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        jsonPK = sharedPreferences.getString("public_key", "");
        jsonSK = sharedPreferences.getString("secret_key", "");
        sid = sharedPreferences.getString("sid", "");

        decryptFragBinding.selectDecryptFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择文件"), DECRYPT_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        });

        decryptFragBinding.executeDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!judge(jsonSK, jsonPK, sid)) {
                    new uploadEvent().execute();
                }
                ABEFileUtils fileUtils = new ABEFileUtils();
                String resultStr = "";
                String ciphertextURL = decryptFilePath;
//                String targetDirURL = abeLocalFileFragBinding.plaintextPath.getText().toString();
                String targetDirURL = Environment.getExternalStorageDirectory() + "/软微云课堂/plaintext";
                File targetDir = new File(targetDirURL);
                if (!targetDir.exists()) {
                    targetDir.mkdir();
                }

                if (Strings.isNullOrEmpty(jsonSK)) {
                    Toast.makeText(getContext(), "请先获取私钥!", Toast.LENGTH_LONG).show();
                }
                try {
                    Log.d("time_state", "file decrypt begin time: " + System.currentTimeMillis());
                    boolean flag = fileUtils.decFile(ciphertextURL, targetDirURL, jsonSK, sid);
                    Log.d("time_state", "file decrypt end time: " + System.currentTimeMillis());

                    if (flag)
                        resultStr += "Decryption Operates Successfully!";
                    else
                        resultStr += "Decryption Operates Unsuccessfully!";
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(getContext(), resultStr, Toast.LENGTH_SHORT).show();
            }
        });


        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DECRYPT_CODE) {
                Uri uri = data.getData();
                decryptFilePath = FilePath.getPath(getContext(), uri);
                decryptFragBinding.decryptFilePath.setText(decryptFilePath);
            } else if (requestCode == PLAINTEXT_PATH) {
                Uri uri = data.getData();
                plaintextDir = FilePath.getPath(getContext(), uri);
                decryptFragBinding.plaintextPath.setText(plaintextDir);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class uploadEvent extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String path = ServerInterface.uploadEvent;
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");

                OutputStream os = connection.getOutputStream();

                String tmpSk = jsonSK;
                String params = "sid=" + sid + "&info=test" + "&sk=" + URLEncoder.encode(tmpSk);
                os.write(params.getBytes("utf-8"));

                //获得结果码
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    //请求成功
                    InputStream is = connection.getInputStream();
                    return convertStreamToString(is);
                } else {
                    //请求失败
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    private boolean judge(String objSK, String objPK, String ID) {
        Decryptor decryptor = new Decryptor("decryptor");
        return decryptor.Judge(objSK, objPK, ID);
    }

    public static String convertStreamToString(InputStream is) {
        /*
          * To convert the InputStream to String we use the BufferedReader.readLine()
          * method. We iterate until the BufferedReader return null which means
          * there's no more data to read. Each line will appended to a StringBuilder
          * and returned as String.
          */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @Override
    public View createTabContent(String s) {
        return null;
    }

    @Override
    public void setPresenter(DecryptContract.Presenter presenter) {

    }
}
