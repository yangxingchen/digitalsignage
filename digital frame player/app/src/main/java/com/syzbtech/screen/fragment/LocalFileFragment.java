package com.syzbtech.screen.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syzbtech.screen.R;
import com.syzbtech.screen.adapter.ItemFileAdapter;
import com.syzbtech.screen.dao.LocalFileDao;
import com.syzbtech.screen.entities.LocalFile;
import com.syzbtech.screen.utils.Util;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.fragment_local_file)
public class LocalFileFragment extends Fragment {

    private String TAG = LocalFileFragment.class.getName();

    protected static final String TYPE_PARAM = "type";

    private List<LocalFile> localFiles = new ArrayList<>();

    @ViewInject(R.id.rvLocalFile)
    private RecyclerView rvLocalFile;

    private ItemFileAdapter adapter;

    protected int type;

    public static LocalFileFragment newInstance(int type) {
        LocalFileFragment fragment = new LocalFileFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE_PARAM, type);
        fragment.setArguments(args);
        return fragment;
    }

    public void unSelectAll() {
        Util.unSelectAll(localFiles);
        adapter.notifyDataSetChanged();
        rvLocalFile.post(()->{
            removeSelectFlag(rvLocalFile);
        });
    }

    private void removeSelectFlag(ViewGroup viewGroup) {
        int c = viewGroup.getChildCount();
        for(int i=0;i<c;i++) {
           View v = viewGroup.getChildAt(i);
           v.setSelected(false);
           if(v instanceof ViewGroup) {
               removeSelectFlag((ViewGroup) v);
           }
        }
    }

    public boolean isSelect() {
        List<LocalFile> selectedList = Util.collectSelectedLocalFile(localFiles);
        return selectedList.size()>0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        setupView(view);
        loadLocalFile();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if(args!=null) {
            this.type = args.getInt(TYPE_PARAM);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }
    protected void setupView(View view) {
        Configuration configuration = getResources().getConfiguration();
        int ori = configuration.orientation;
        int itemCount = 4;
        if(ori==Configuration.ORIENTATION_LANDSCAPE) {
            itemCount = 8;
        } else if(ori==Configuration.ORIENTATION_PORTRAIT) {
            itemCount = 4;
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), itemCount);
        rvLocalFile.setLayoutManager(layoutManager);
        adapter = new ItemFileAdapter(localFiles, R.layout.item_file);
        rvLocalFile.setAdapter(adapter);
        adapter.setOnItemClickListener((v, localFile) -> {
            localFile.setSelected(!localFile.isSelected());
            adapter.notifyDataSetChanged();
        });
        adapter.setView(rvLocalFile);
    }

    protected void loadLocalFile() {
        List<LocalFile> dataList = LocalFileDao.instance().listByType(type);
        Util.sortLocalFile(dataList);
        adapter.addAll(dataList);
    }

    public void doTop() {
        List<LocalFile> selectedList = Util.collectSelectedLocalFile(localFiles);
        for(LocalFile localFile : selectedList) {
            localFile.setSort(Long.MIN_VALUE);
        }
        Util.sortLocalFile(localFiles);
        Util.resetSortLocalFile(localFiles);

        LocalFileDao.instance().updateBatch(localFiles, "sort");

    }

    public void doBottom() {
        List<LocalFile> selectedList = Util.collectSelectedLocalFile(localFiles);
        for(LocalFile localFile : selectedList) {
            localFile.setSort(Long.MAX_VALUE);
        }
        Util.sortLocalFile(localFiles);
        Util.resetSortLocalFile(localFiles);

        LocalFileDao.instance().updateBatch(localFiles, "sort");

    }

    public void doUp() {
        List<LocalFile> selectedList = Util.collectSelectedLocalFile(localFiles);
        int offset = 0;
        for(LocalFile localFile : selectedList) {
            localFile.setSort(localFile.getSort() - 2 - offset);
            offset++;
        }
        Util.sortLocalFile(localFiles);
        Util.resetSortLocalFile(localFiles);

        LocalFileDao.instance().updateBatch(localFiles, "sort");

    }

    public void doDown() {
        List<LocalFile> selectedList = Util.collectSelectedLocalFile(localFiles);
        int offset = 0;
        for(int i=selectedList.size()-1;i>=0;i--) {
            LocalFile localFile = selectedList.get(i);
            localFile.setSort(localFile.getSort() + 2 + offset);
            offset++;
        }
        Util.sortLocalFile(localFiles);
        Util.resetSortLocalFile(localFiles);

        LocalFileDao.instance().updateBatch(localFiles, "sort");

    }

    public void doDelete() {
        List<LocalFile> removeList = Util.deleteLocalFile(localFiles);
        List<Long> ids = new ArrayList<>();
        for(LocalFile localFile : removeList) {
            ids.add(localFile.getId());
            boolean b = new File(localFile.getPath()).delete();
            Log.d(TAG, localFile.getPath() + " deleted >>" + b);
        }
        Util.resetSortLocalFile(localFiles);
        LocalFileDao.instance().removeBatch(ids);
        LocalFileDao.instance().updateBatch(localFiles, "sort");

    }
}
