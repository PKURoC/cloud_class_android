package com.vontroy.pku_ss_cloud_class.storage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.vontroy.abe_lib.algorithm.ABEFileUtils;
import com.vontroy.abe_lib.component.Decryptor;
import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.entry.FileUtils;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.utils.FilePath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by vontroy on 2017/11/1.
 */

public class ABEActivity extends BaseActivity {
    private String encryptFilePath;
    private String decryptFilePath;
    private String ciphertextDir;
    private String plaintextDir;
    private String attributesJsonArray;
    private String sid;

    private static final String DEFAULT_POLICY = "(school:pku and academy:computer) or (籍贯:北京  and age:130)";
    private static final String DEFAULT_ATTRIBUTE = "school:pku,academy:computer,籍贯:北京,age:130";
    private static final int ENCRYPT_CODE = 1010;
    private static final int DECRYPT_CODE = 1011;
    private static final int CT_PATH = 1100;
    private static final int PLAINTEXT_PATH = 1101;

    String jsonPK = "";
    String jsonSK = "";

    private Button updateSK;
    private Button executeEncrypt;
    private Button executeDecrypt;
    private Button selectEncryptFile;
    private Button selectDecryptFile;
    private Button selectCiphertextPath;
    private Button selectPlaintextPath;
    private Button systemSetup;
    private Button getPK;

    private TextView editAttributeTV;
    private TextView encryptFilePathTV;
    private TextView ciphertextPathTV;
    private TextView editPolicyTV;
    private TextView decryptFilePathTV;
    private TextView plaintextPathTV;
    private TextView pkState;
    private TextView skState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("加密/解密");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ABEActivity.this);
        jsonPK = sharedPreferences.getString("public_key", "");
        jsonSK = sharedPreferences.getString("secret_key", "");
        sid = sharedPreferences.getString("sid", "");

        pkState = (TextView) findViewById(R.id.pk_state);
        skState = (TextView) findViewById(R.id.sk_state);

        if (Strings.isNullOrEmpty(jsonPK)) {
            pkState.setText("未获取");
            pkState.setTextColor(Color.BLACK);
        } else {
            pkState.setText("已获取");
        }

        if (Strings.isNullOrEmpty(jsonSK)) {
            skState.setText("未获取");
            skState.setTextColor(Color.BLACK);
        } else {
            skState.setText("已获取");
        }

        updateSK = (Button) findViewById(R.id.update_secret_key);
        executeEncrypt = (Button) findViewById(R.id.execute_encrypt);
        executeDecrypt = (Button) findViewById(R.id.execute_decrypt);
        selectEncryptFile = (Button) findViewById(R.id.select_encrypt_file);
        selectDecryptFile = (Button) findViewById(R.id.select_decrypt_file);
        selectCiphertextPath = (Button) findViewById(R.id.select_ciphertext_path);
        selectPlaintextPath = (Button) findViewById(R.id.select_plaintext_path);
        systemSetup = (Button) findViewById(R.id.system_setup);
        getPK = (Button) findViewById(R.id.get_pk);

        editAttributeTV = (TextView) findViewById(R.id.edit_attribute);
        encryptFilePathTV = (TextView) findViewById(R.id.encrypt_file_path);
        ciphertextPathTV = (TextView) findViewById(R.id.ciphertext_path);
        editPolicyTV = (TextView) findViewById(R.id.edit_policy);
        decryptFilePathTV = (TextView) findViewById(R.id.decrypt_file_path);
        plaintextPathTV = (TextView) findViewById(R.id.plaintext_path);

        editAttributeTV.setText(DEFAULT_ATTRIBUTE);
        editPolicyTV.setText(DEFAULT_POLICY);

        final ABEFileUtils fileUtils = new ABEFileUtils();

        getPK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetPK().execute();
            }
        });

        updateSK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("vis".equals(editAttributeTV.getText().toString())) {
                    systemSetup.setVisibility(View.VISIBLE);
                } else {
                    String attributeStr = editAttributeTV.getText().toString();
                    String[] attributesArray = attributeStr.split(",");
                    attributesJsonArray = JSON.toJSONString(attributesArray);
                    new GetSK().execute();
                }
            }
        });

        selectCiphertextPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, CT_PATH);
            }
        });

        executeEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //设置密文策略
                String policy = editPolicyTV.getText().toString();
                if (Strings.isNullOrEmpty(policy)) {
                    policy = DEFAULT_POLICY;
                }

                //设置明文地址
                String fileURL = encryptFilePathTV.getText().toString();

                //设置生成密文存放的目录
//                String targetDirURL = abeLocalFileFragBinding.ciphertextPath.getText().toString();
                String targetDirURL = Environment.getExternalStorageDirectory() + "/软微云课堂/ciphertext";
                //加密文件，得到所在密文地址
                try {
                    String ciphertextURL = fileUtils.encFile(fileURL, targetDirURL, policy, jsonPK, "try it!".getBytes("utf-8"), sid);
                    Toast.makeText(ABEActivity.this, "文件加密成功!\n密文存储在" + ciphertextURL, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        selectPlaintextPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                try {
                    startActivityForResult(intent, PLAINTEXT_PATH);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ABEActivity.this, "error", Toast.LENGTH_LONG).show();
                }
            }
        });

        executeDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!judge(jsonSK, jsonPK, "1112")) {
                    new uploadEvent().execute();
                }
                String resultStr = "";
                String ciphertextURL = decryptFilePath;
//                String targetDirURL = abeLocalFileFragBinding.plaintextPath.getText().toString();
                String targetDirURL = Environment.getExternalStorageDirectory() + "/软微云课堂/plaintext";
                if (Strings.isNullOrEmpty(jsonSK)) {
                    Toast.makeText(ABEActivity.this, "请先获取私钥!", Toast.LENGTH_LONG).show();
                }
                try {
                    boolean flag = fileUtils.decFile(ciphertextURL, targetDirURL, jsonSK, sid);

                    if (flag)
                        resultStr += "Decryption Operates Successfully!";
                    else
                        resultStr += "Decryption Operates Unsuccessfully!";
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(ABEActivity.this, resultStr, Toast.LENGTH_SHORT).show();
            }
        });

        selectEncryptFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择文件"), ENCRYPT_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ABEActivity.this, "error", Toast.LENGTH_LONG).show();
                }
            }
        });

        selectDecryptFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择文件"), DECRYPT_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ABEActivity.this, "error", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ENCRYPT_CODE) {
                Uri uri = data.getData();
                encryptFilePath = FilePath.getPath(this, uri);
                encryptFilePathTV.setText(encryptFilePath);
            } else if (requestCode == DECRYPT_CODE) {
                Uri uri = data.getData();
                decryptFilePath = FilePath.getPath(this, uri);
                decryptFilePathTV.setText(decryptFilePath);
            } else if (requestCode == CT_PATH) {
                Uri uri = data.getData();
                ciphertextDir = FilePath.getPath(this, uri);
                ciphertextPathTV.setText(ciphertextDir);
            } else if (requestCode == PLAINTEXT_PATH) {
                Uri uri = data.getData();
                plaintextDir = FilePath.getPath(this, uri);
                plaintextPathTV.setText(plaintextDir);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetPK extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String path = ServerInterface.getPKUrl;
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                //获得结果码
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    //请求成功 获得返回的流
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
            JSONObject jsonObject = JSON.parseObject(result);
            jsonPK = jsonObject.get("PK").toString();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ABEActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("public_key", jsonPK);
            editor.apply();
            pkState.setText("已获取");
            pkState.setTextColor(Color.GREEN);
            Toast.makeText(ABEActivity.this, "公钥获取成功!", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetSK extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String path = ServerInterface.getSKUrl;
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");

                //数据准备
                String data = "attributes=" + attributesJsonArray;
                //至少要设置的两个请求头
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", (data.length() + 8) + "");

                //post的方式提交实际上是留的方式提交给服务器
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes());

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
            JSONObject jsonObject = JSON.parseObject(result);
            jsonSK = jsonObject.get("SK").toString();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ABEActivity.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("secret_key", jsonSK);
            editor.apply();
            skState.setText("已获取");
            skState.setTextColor(Color.GREEN);
            Toast.makeText(ABEActivity.this, "私钥获取成功!", Toast.LENGTH_LONG).show();
        }
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
                os.write(("sid=" + sid + "&info=testtt222t ttt").getBytes("utf-8"));

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
