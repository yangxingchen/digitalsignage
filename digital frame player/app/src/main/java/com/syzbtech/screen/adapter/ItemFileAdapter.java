package com.syzbtech.screen.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.syzbtech.screen.MyApplication;
import com.syzbtech.screen.R;
import com.syzbtech.screen.entities.LocalFile;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class ItemFileAdapter extends RecyclerView.Adapter<ItemFileViewHolder> {

    private RecyclerView recyclerView;

    public void setView(RecyclerView recyclerView) {
        this.recyclerView  = recyclerView;
    }

    private List<LocalFile> dataList;
    private int resourceId;

    public interface OnItemClickListener{
        void onClick(View v, LocalFile localFile);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public ItemFileAdapter(List<LocalFile> dataList, int resourceId) {
        this.dataList = dataList;
        this.resourceId = resourceId;
    }

    public void addAll(List<LocalFile> dataList) {
        if(this.dataList==null) {
            this.dataList = new ArrayList<>();
        }
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), resourceId, null);
        ItemFileViewHolder viewHolder = new ItemFileViewHolder(view);
        x.view().inject(viewHolder, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemFileViewHolder holder, int position) {
        LocalFile localFile = getItem(position);
        if(localFile!=null) {
            /*
            if(localFile.isSelected()) {
                holder.selected.setImageResource(R.mipmap.ic_check);
            } else {
                holder.selected.setImageResource(R.mipmap.ic_not_check);
            }*/

            if(localFile.getType()==3) {
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(localFile.getDisplay());
                holder.thumbImg.setImageResource(R.mipmap.ic_music_default);
            } else {
                holder.name.setVisibility(View.GONE);
                Glide.with(MyApplication.getInstance().getApplicationContext())
                        .load(localFile.getThumb()).into(holder.thumbImg);
            }
            if(onItemClickListener!=null) {
                holder.item.setOnClickListener((v)->{

                    localFile.setSelected(!localFile.isSelected());
                    if(localFile.isSelected()) {
                        holder.selected.setImageResource(R.mipmap.ic_check);
                    } else {
                        holder.selected.setImageResource(R.mipmap.ic_not_check);
                    }
                    //onItemClickListener.onClick(v, localFile);
                });
            }

            holder.item.setOnFocusChangeListener((view, focus)->{
                if(this.recyclerView!=null) {
                    int c = this.recyclerView.getChildCount();
                    Log.d("ItemFileAdapter", ">> " + c);
                    for (int i=0;i<c;i++) {
                        View v = this.recyclerView.getChildAt(i);
                        Log.d("ItemFileAdapter"," view >> " + v);
                        if(v!=null) {
                            if(v instanceof LinearLayout) {
                                LinearLayout o = (LinearLayout) v;
                                View f = o.getChildAt(0);
                                Log.d("ItemFileAdapter"," view f >> " + f);
                                if(f!=null) {
                                    f.setSelected(false);
                                }
                            }

                        }
                    }
                }
                view.setSelected(true);
            });
        }
    }

    private LocalFile getItem(int position) {
        if(dataList!=null && dataList.size()>0) {
            return dataList.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return dataList==null ? 0 : dataList.size();
    }
}
