package com.vontroy.pku_ss_cloud_class.transfer_list;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.vontroy.pku_ss_cloud_class.BaseActivity;
import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.TransferListAdapter;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vontroy on 17-1-26.
 */

public class TransferListActivity extends BaseActivity {
    private ListView fileTransferList;
    private ArrayList<StorageInfo> downloadedFiles;
    private File[] downloadedFileItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_transfer_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("传输列表");
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        readDownloadedFiles();

        ArrayList<StorageInfo> storageInfos = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            storageInfos = (ArrayList<StorageInfo>) bundle.get("storageInfos");
        }

        downloadedFiles = new ArrayList<>();
        for (StorageInfo storageInfo : storageInfos) {
            for (File file : downloadedFileItems) {
                if (storageInfo.getFileName().equals(file.getName())) {
                    storageInfo.setLocalExists(true);
                    downloadedFiles.add(storageInfo);
                }
            }
        }

        fileTransferList = (ListView) findViewById(R.id.file_transfer_list);

        TransferListAdapter adapter = new TransferListAdapter(this, downloadedFiles);
        fileTransferList.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
}
