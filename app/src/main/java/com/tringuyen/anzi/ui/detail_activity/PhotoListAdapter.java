package com.tringuyen.anzi.ui.detail_activity;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tringuyen.anzi.Constants;
import com.tringuyen.anzi.R;
import com.tringuyen.anzi.model.event_bus.PhotoClickEvent;
import com.tringuyen.anzi.model.google.google_search_activity.Photo;
import com.tringuyen.anzi.ui.search_activity.ResultListAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Tri Nguyen on 12/20/2016.
 */

public class PhotoListAdapter extends RecyclerView.Adapter {
    private List<Photo> mPhotoList = null;
    private Context mContext;
    public PhotoListAdapter (Context context ,List<Photo> photoList)
    {
        this.mContext = context;
        this.mPhotoList = photoList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_detail_photo_item,null);
        PhotoListViewHolder holder = new PhotoListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Glide.with(mContext)
                .load(Constants.TEMP_DETAIL_IMAGE_URL + mPhotoList.get(position).getPhotoReference())
                .centerCrop()
                .error(R.drawable.ic_default_image)
                .into(((PhotoListViewHolder)holder).mPhotoImageView);
        ((PhotoListViewHolder)holder).mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PhotoClickEvent(mPhotoList.get(position).getPhotoReference()));
            }
        });
    }

    private static class PhotoListViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView mPhotoImageView;
        public CardView mPhotoItemCardView;
        public PhotoListViewHolder(View itemView) {
            super(itemView);
            mPhotoImageView = (ImageView) itemView.findViewById(R.id.photo_item);
            mPhotoItemCardView = (CardView) itemView.findViewById(R.id.card_view_photo_item);
        }
    }
    public int getItemCount() {
        return mPhotoList.size();
    }

}
