package com.vontroy.pku_ss_cloud_class.storage.video;

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
import com.vontroy.pku_ss_cloud_class.databinding.StorageVideoFragBinding;
import com.vontroy.pku_ss_cloud_class.entry.FileUtils;
import com.vontroy.pku_ss_cloud_class.entry.StorageInfo;
import com.vontroy.pku_ss_cloud_class.storage.StorageFragment;

import java.util.ArrayList;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by vontroy on 16-11-17.
 */

public class StorageVideoFragment extends Fragment implements StorageVideoContract.View, TabHost.TabContentFactory {
    private StorageVideoFragBinding storageVideoFragBinding;
    private StorageVideoContract.Presenter mPresenter;
    private View mRootView;
    private ListView video_list;

    public static StorageVideoFragment newInstance(ArrayList<StorageInfo> arrayList) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(StorageFragment.STORAGELIST, arrayList);
        StorageVideoFragment storageVideoFragment = new StorageVideoFragment();
        storageVideoFragment.setArguments(args);
        return storageVideoFragment;
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
            mRootView = inflater.inflate(R.layout.storage_video_frag, container, false);
        }
        video_list = (ListView) mRootView.findViewById(R.id.storage_video);
        storageVideoFragBinding = StorageVideoFragBinding.bind(mRootView);

        final ArrayList<StorageInfo> storageInfos = getArguments().getParcelableArrayList(StorageFragment.STORAGELIST);

        final ArrayList<StorageInfo> filterStorageInfos = new ArrayList<>();
        for (StorageInfo storageInfo : storageInfos) {
            if (storageInfo.getType().equals(FileUtils.FileType.VIDEO)) {
                filterStorageInfos.add(storageInfo);
            }
        }

        StorageListAdapter adapter = new StorageListAdapter(this.getActivity(), filterStorageInfos, "video");
        video_list.setAdapter(adapter);
//        video_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    public void setPresenter(@NonNull StorageVideoContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public View createTabContent(String tag) {
        final TextView tv = new TextView(getActivity());
        tv.setText("Content for tab with tag " + tag);
        return tv;
    }
}
