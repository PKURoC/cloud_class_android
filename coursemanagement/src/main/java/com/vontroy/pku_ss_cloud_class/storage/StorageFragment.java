package com.vontroy.pku_ss_cloud_class.storage;

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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.vontroy.abe_lib.component.Encryptor;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.data.StorageArrayResult;
import com.vontroy.pku_ss_cloud_class.data.StorageResult;
import com.vontroy.pku_ss_cloud_class.entry.FileUtils;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.network.ServerImp;
import com.vontroy.pku_ss_cloud_class.network.ServerInterface;
import com.vontroy.pku_ss_cloud_class.storage.all.StorageAllFragment;
import com.vontroy.pku_ss_cloud_class.storage.all.StorageAllPresenter;
import com.vontroy.pku_ss_cloud_class.storage.crypto.CryptoActivity;
import com.vontroy.pku_ss_cloud_class.storage.doc.StorageDocFragment;
import com.vontroy.pku_ss_cloud_class.storage.doc.StorageDocPresenter;
import com.vontroy.pku_ss_cloud_class.storage.file_utils.FileUtilActivity;
import com.vontroy.pku_ss_cloud_class.storage.img.StorageImgFragment;
import com.vontroy.pku_ss_cloud_class.storage.img.StorageImgPresenter;
import com.vontroy.pku_ss_cloud_class.storage.music.StorageMscFragment;
import com.vontroy.pku_ss_cloud_class.storage.music.StorageMscPresenter;
import com.vontroy.pku_ss_cloud_class.storage.video.StorageVideoFragment;
import com.vontroy.pku_ss_cloud_class.storage.video.StorageVideoPresenter;
import com.vontroy.pku_ss_cloud_class.transfer_list.TransferListActivity;
import com.vontroy.pku_ss_cloud_class.utils.ActivityUtils;
import com.vontroy.pku_ss_cloud_class.utils.Constants;
import com.vontroy.pku_ss_cloud_class.utils.FilePath;
import com.vontroy.pku_ss_cloud_class.utils.schedulers.SchedulerProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscription;

import static android.content.ContentValues.TAG;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-17.
 */

public class StorageFragment extends Fragment implements StorageContract.View {

    private View mRootView;
    private StorageContract.Presenter mPresenter;
    public static final String STORAGELIST = "storageList";
    ArrayList<StorageInfo> storageInfos;
    private File[] downloadedFileItems;
    private int currentFragment;
    private String filePath;
    private String uuid;
    private String sid;
    private String token;
    private String attributesJsonArray;
    private String jsonSK;
    private String jsonPK;

    public static StorageFragment newInstance() {

        return new StorageFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle bundle = new Bundle();

        if (mRootView == null) {
            Log.e("666", "StorageFragment");
            mRootView = inflater.inflate(R.layout.storage_fragment, container, false);
        }

        SharedPreferences getParams = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        token = getParams.getString("token", "");
        sid = getParams.getString("sid", "");

        Map<String, String> params = new HashMap<>();

        params.put("token", token);
        params.put("sid", sid);

        storageInfos = new ArrayList<>();

        setHasOptionsMenu(true);

        TabLayout tabLayout = (TabLayout) mRootView.findViewById(R.id.storage_tab);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int id = tab.getPosition();
                switchFragment(id);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Button viewDownloaded = (Button) mRootView.findViewById(R.id.view_downloaded);

        Button debugTest = (Button) mRootView.findViewById(R.id.file_operation);

        LinearLayout fileUpload = (LinearLayout) mRootView.findViewById(R.id.file_upload);

        LinearLayout encDec = (LinearLayout) mRootView.findViewById(R.id.enc_dec);

        fileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FileUtilActivity.class);
                Bundle fileUploadBundle = new Bundle();
                fileUploadBundle.putSerializable("storageInfos", storageInfos);
                intent.putExtras(fileUploadBundle);
                startActivity(intent);
            }
        });

        encDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CryptoActivity.class);
                startActivity(intent);
            }
        });

        debugTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ABEActivity.class);
                startActivity(intent);
            }
        });

        readDownloadedFiles();

        getCloudObjects(params);

        viewDownloaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TransferListActivity.class);
                bundle.putSerializable("storageInfos", storageInfos);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        currentFragment = tabLayout.getSelectedTabPosition();
        switchFragment(currentFragment);

        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    private void switchFragment(int id) {
        if (id == 0) {
            StorageAllFragment storageAllFragment = StorageAllFragment.newInstance(storageInfos);

            ActivityUtils.addFragmentToActivity(getChildFragmentManager(), storageAllFragment, R.id.storageContent);

            new StorageAllPresenter("StorageAllFragment", ServerImp.getInstance(), storageAllFragment, SchedulerProvider.getInstance());
        } else if (id == 1) {
            StorageDocFragment storageDocFragment = StorageDocFragment.newInstance(storageInfos);

            ActivityUtils.addFragmentToActivity(getChildFragmentManager(), storageDocFragment, R.id.storageContent);

            new StorageDocPresenter("StorageDocFragment", ServerImp.getInstance(), storageDocFragment, SchedulerProvider.getInstance());
        } else if (id == 2) {
            StorageImgFragment storageImgFragment = StorageImgFragment.newInstance(storageInfos);

            ActivityUtils.addFragmentToActivity(getChildFragmentManager(), storageImgFragment, R.id.storageContent);
            new StorageImgPresenter("StorageImgFragment", ServerImp.getInstance(), storageImgFragment, SchedulerProvider.getInstance());
        } else if (id == 3) {
            StorageVideoFragment storageVideoFragment = StorageVideoFragment.newInstance(storageInfos);

            ActivityUtils.addFragmentToActivity(getChildFragmentManager(), storageVideoFragment, R.id.storageContent);
            new StorageVideoPresenter("StorageImgFragment", ServerImp.getInstance(), storageVideoFragment, SchedulerProvider.getInstance());
        } else if (id == 4) {
            StorageMscFragment storageMscFragment = StorageMscFragment.newInstance(storageInfos);

            ActivityUtils.addFragmentToActivity(getChildFragmentManager(), storageMscFragment, R.id.storageContent);
            new StorageMscPresenter("StorageImgFragment", ServerImp.getInstance(), storageMscFragment, SchedulerProvider.getInstance());
        }
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
                            ArrayList<StorageResult> storageResults = storageArrayResult.getData();
                            for (StorageResult storageResult : storageResults) {
                                StorageInfo storageInfo = new StorageInfo("", "");
                                //TODO
                                String name = storageResult.getFilename();
                                String uuid = storageResult.getUuid();

                                storageInfo.setFileName(name);
                                storageInfo.setUuid(uuid);
                                storageInfo.setStorageType(Constants.StorageType.STORAGE);
                                storageInfo.setIntegrity(storageResult.getIntegrity());
                                storageInfo.setLocalExists(false);

                                if (downloadedFileItems != null) {
                                    for (File file : downloadedFileItems) {
                                        String fileName = file.getName();
                                        if (fileName.equals(name)) {
                                            storageInfo.setLocalExists(true);
                                            break;
                                        } else {
                                            storageInfo.setLocalExists(false);
                                        }
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

                                storageInfos.add(storageInfo);
                            }
                        }
                        switchFragment(currentFragment);
                    }

                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.upload_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    static final int REQUEST_CODE = 1010;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_file: {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择文件"), REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Uri uri = data.getData();
                filePath = FilePath.getPath(getActivity(), uri);
                Map<String, String> uploadParams = new HashMap<>();

                uploadParams.put("token", token);
                uploadParams.put("sid", sid);
                upLoadObject(uploadParams);

                Log.d(TAG, "onActivityResult: " + FilePath.getPath(getActivity(), uri));
            }
        }
    }

    @Override
    public void setPresenter(StorageContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
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
                            uuid = storageResult.getUuid();
                            try {
                                ServerImp.getInstance().uploadMultipart(getContext(), filePath, uuid, false);
                            } catch (Exception exc) {
                                Log.e("AndroidUploadService", exc.getMessage(), exc);
                            }
                        }
                    }

                });
    }

    public void readDownloadedFiles() {
        String dir = Environment.getExternalStorageDirectory() + "/软微云课堂/Downloads/云盘";
        File downloadedFileDir = new File(dir);

        if (!downloadedFileDir.exists()) {
            downloadedFileDir.mkdir();
        } else {
            downloadedFileItems = downloadedFileDir.listFiles();
        }
    }

    private String encFile(String filePath) {
        Encryptor encryptor = new Encryptor("jack");
        String policy = "";
        String pk = "";
        String ciphertext = "";
        try {
            ciphertext = encryptor.encrypt(policy, pk, filePath, "try it!".getBytes("utf-8"));
            ciphertext.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ciphertext;
    }

    class GetPK extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(ServerInterface.getPKUrl);
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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.apply();
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
}
