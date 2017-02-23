package com.daxiang.taojin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.daxiang.taojin.R;
import com.daxiang.taojin.bean.ImgInfo;
import com.daxiang.taojin.constants.ImgApiConstants;
import com.daxiang.taojin.ui.MainActivity;

import java.util.List;

/**
 * Created by daxiang on 2017/2/22.
 */
public class ImgListAdapter extends RecyclerView.Adapter<MainActivity.ImgListViewHolder> {
    private List<ImgInfo> mDatas;
    private Context mContext;

    public ImgListAdapter(Context mContext, List<ImgInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    @Override
    public MainActivity.ImgListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MainActivity.ImgListViewHolder viewHolder = new MainActivity.ImgListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img_list, parent, false));
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(MainActivity.ImgListViewHolder holder, int position) {
        Glide.with(mContext).load(ImgApiConstants.IMG_URI_PREFIX + mDatas.get(position).img).into(holder.imageView);
        holder.imgDesc.setText(mDatas.get(position).title);
    }

}




