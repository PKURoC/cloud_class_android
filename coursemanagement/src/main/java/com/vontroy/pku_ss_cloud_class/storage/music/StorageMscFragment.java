package com.vontroy.pku_ss_cloud_class.storage.music;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.vontroy.pku_ss_cloud_class.databinding.StorageMscFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.FileUtils;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.storage.StorageFragment;

import java.util.ArrayList;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-17.
 */

public class StorageMscFragment extends Fragment implements StorageMscContract.View, TabHost.TabContentFactory {
    private StorageMscFragBinding storageMscFragBinding;
    private StorageMscContract.Presenter mPresenter;
    private View mRootView;
    private ListView music_list;

    public static StorageMscFragment newInstance(ArrayList<StorageInfo> arrayList) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(StorageFragment.STORAGELIST, arrayList);
        StorageMscFragment storageMscFragment = new StorageMscFragment();
        storageMscFragment.setArguments(args);
        return storageMscFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("666", "CurriculumFragment");
            mRootView = inflater.inflate(R.layout.storage_msc_frag, container, false);
        }
        music_list = (ListView) mRootView.findViewById(R.id.storage_msc);
        storageMscFragBinding = StorageMscFragBinding.bind(mRootView);

        final ArrayList<StorageInfo> storageInfos = getArguments().getParcelableArrayList(StorageFragment.STORAGELIST);

        final ArrayList<StorageInfo> filterStorageInfos = new ArrayList<>();
        for (StorageInfo storageInfo : storageInfos) {
            if (storageInfo.getType().equals(FileUtils.FileType.MUSIC)) {
                filterStorageInfos.add(storageInfo);
            }
        }

        StorageListAdapter adapter = new StorageListAdapter(this.getActivity(), filterStorageInfos, "music");
        music_list.setAdapter(adapter);
//        music_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    public void setPresenter(@NonNull StorageMscContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public View createTabContent(String tag) {
        final TextView tv = new TextView(getActivity());
        tv.setText("Content for tab with tag " + tag);
        return tv;
    }
}
