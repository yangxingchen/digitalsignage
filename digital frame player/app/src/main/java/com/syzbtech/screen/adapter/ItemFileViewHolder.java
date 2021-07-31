package com.syzbtech.screen.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syzbtech.screen.R;

import org.xutils.view.annotation.ViewInject;

public class ItemFileViewHolder extends RecyclerView.ViewHolder {

    @ViewInject(R.id.item)
    public RelativeLayout item;

    @ViewInject(R.id.img)
    public ImageView thumbImg;

    @ViewInject(R.id.selected)
    public ImageView selected;

    @ViewInject(R.id.name)
    public TextView name;

    public ItemFileViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
