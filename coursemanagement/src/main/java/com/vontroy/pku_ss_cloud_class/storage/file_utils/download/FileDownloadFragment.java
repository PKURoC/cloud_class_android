package com.vontroy.pku_ss_cloud_class.storage.file_utils.download;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.vontroy.pku_ss_cloud_class.R;
import com.vontroy.pku_ss_cloud_class.adapter.StorageListAdapter;
import com.vontroy.pku_ss_cloud_class.databinding.FileDownloadFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.storage.StorageFragment;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by vontroy on 2017/11/15.
 */

public class FileDownloadFragment extends Fragment implements FileDownloadContract.View, TabHost.TabContentFactory {
    private FileDownloadFragBinding fileDownloadFragBinding;
    private FileDownloadContract.Presenter mPresenter;
    private View mRootView;
    private ListView all_list;


    public static FileDownloadFragment newInstance(ArrayList<StorageInfo> arrayList) {
        Bundle args = new Bundle();
        //args.putParcelableArrayList(StorageFragment.STORAGELIST, arrayList);
        FileDownloadFragment fileDownloadFragment = new FileDownloadFragment();
        fileDownloadFragment.setArguments(args);
        return fileDownloadFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("666", "CurriculumFragment");
            mRootView = inflater.inflate(R.layout.file_download_frag, container, false);
        }
        all_list = (ListView) mRootView.findViewById(R.id.file_list);
        fileDownloadFragBinding = FileDownloadFragBinding.bind(mRootView);

        Bundle bundle = getArguments();

        final ArrayList<StorageInfo> storageInfos = (ArrayList<StorageInfo>) bundle.getSerializable("storageInfos");

        StorageListAdapter adapter = new StorageListAdapter(this.getActivity(), storageInfos, "all");
        all_list.setAdapter(adapter);
//        all_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(getActivity(), CourseActivity.class);
//                Bundle bundle = new Bundle();
//
//                StorageInfo storageInfo = storageInfos.get(i);
//
//                bundle.putString("file_name", storageInfo.getFileName());
//                bundle.putString("remarks", storageInfo.getRemarks());
//
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }


    @Override
    public View createTabContent(String s) {
        final TextView tv = new TextView(getActivity());
        tv.setText("Content for tab with tag " + s);
        return tv;
    }

    @Override
    public void setPresenter(FileDownloadContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
