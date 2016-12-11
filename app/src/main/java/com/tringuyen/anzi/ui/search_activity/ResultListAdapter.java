package com.tringuyen.anzi.ui.search_activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.tringuyen.anzi.Constants;
import com.tringuyen.anzi.R;
import com.tringuyen.anzi.model.google.google_search_activity.Result;
import com.tringuyen.anzi.ui.detail_activity.DetailActivity;

import java.util.List;

/**
 * Created by Tri Nguyen on 11/28/2016.
 */
public class ResultListAdapter extends RecyclerView.Adapter {
    private List<Result> mResultList;
    private final Context mConstext;
    private LatLng mInitialLocation;

    public ResultListAdapter(List<Result> resultList, Context context,LatLng initialLocation)
    {
        mResultList = resultList;
        mConstext = context;
        mInitialLocation = initialLocation;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mConstext).inflate(R.layout.adapter_result_item,parent,false);
        ResultViewHolder vh = new ResultViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ((ResultViewHolder)holder).tv_ResultName
                .setText(mResultList.get(position).getName());
        ((ResultViewHolder)holder).tv_ResultAddress
                .setText(mResultList.get(position).getVicinity());

        if(mResultList.get(position).getPhotos().size() > 0) {
            Glide.with(mConstext)
                    .load(Constants.TEMP_IMAGE_URL + mResultList.get(position).getPhotos().get(0).getPhotoReference())
                    .centerCrop()
                    .error(R.drawable.ic_default_image)
                    .into(((ResultViewHolder) holder).img_Image);
        }
        else
        {
            Glide.with(mConstext)
                    .load(R.drawable.ic_default_image)
                    .centerCrop()
                    .into(((ResultViewHolder) holder).img_Image);
        }

        ((ResultViewHolder) holder).cv_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO consider to pass initial location
                if(mInitialLocation != null) {
                    Intent intent = new Intent(mConstext, DetailActivity.class);
                    intent.putExtra(Constants.LOCATION_ID, mResultList.get(position).getPlaceId());
                    intent.putExtra(Constants.INITIAL_LAT_LOCATION, mInitialLocation.latitude);
                    intent.putExtra(Constants.INITIAL_LNG_LOCATION, mInitialLocation.longitude);
                    mConstext.startActivity(intent);
                }
            }
        });
    }


    public static class ResultViewHolder extends RecyclerView.ViewHolder
    {
        public CardView cv_Item;
        public TextView tv_ResultName,tv_ResultDistance, tv_ResultAddress;
        public ImageView img_Image;
        public ResultViewHolder(View itemView) {
            super(itemView);
            img_Image = (ImageView) itemView.findViewById(R.id.image_view_avatar);
            tv_ResultName = (TextView) itemView.findViewById(R.id.text_view_name);
            cv_Item = (CardView) itemView.findViewById(R.id.card_view_result_item);
            //TODO calculate distance later
//            tv_ResultDistance = (TextView) itemView.findViewById(R.id.text_view_distance);
            tv_ResultAddress = (TextView) itemView.findViewById(R.id.text_view_address);

        }
    }
    public int getItemCount() {
        return mResultList.size();
    }

    public void setmInitialLocation(LatLng mInitialLocation) {
        this.mInitialLocation = mInitialLocation;
    }
}
