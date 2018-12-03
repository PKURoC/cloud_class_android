package com.vontroy.pku_ss_cloud_class.storage.img;

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
import com.vontroy.pku_ss_cloud_class.databinding.StorageImgFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.FileUtils;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.storage.StorageFragment;

import java.util.ArrayList;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-17.
 */

public class StorageImgFragment extends Fragment implements StorageImgContract.View, TabHost.TabContentFactory {
    private StorageImgFragBinding storageImgFragBinding;
    private StorageImgContract.Presenter mPresenter;
    private View mRootView;
    private ListView img_list;

    public static StorageImgFragment newInstance(ArrayList<StorageInfo> arrayList) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(StorageFragment.STORAGELIST, arrayList);
        StorageImgFragment storageImgFragment = new StorageImgFragment();
        storageImgFragment.setArguments(args);
        return storageImgFragment;
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
            mRootView = inflater.inflate(R.layout.storage_img_frag, container, false);
        }
        img_list = (ListView) mRootView.findViewById(R.id.storage_img);
        storageImgFragBinding = StorageImgFragBinding.bind(mRootView);

        final ArrayList<StorageInfo> storageInfos = getArguments().getParcelableArrayList(StorageFragment.STORAGELIST);

        final ArrayList<StorageInfo> filterStorageInfos = new ArrayList<>();
        for (StorageInfo storageInfo : storageInfos) {
            if (storageInfo.getType().equals(FileUtils.FileType.IMG)) {
                filterStorageInfos.add(storageInfo);
            }
        }

        StorageListAdapter adapter = new StorageListAdapter(this.getActivity(), filterStorageInfos, "img");
        img_list.setAdapter(adapter);
//        img_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    public void setPresenter(@NonNull StorageImgContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public View createTabContent(String tag) {
        final TextView tv = new TextView(getActivity());
        tv.setText("Content for tab with tag " + tag);
        return tv;
    }
}
