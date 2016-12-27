package com.tringuyen.anzi.ui.search_activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
    private final Context mContext;
    private LatLng mInitialLocation;

    public ResultListAdapter(List<Result> resultList, Context context,LatLng initialLocation)
    {
        mResultList = resultList;
        mContext = context;
        mInitialLocation = initialLocation;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_result_item,parent,false);
        ResultViewHolder vh = new ResultViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ((ResultViewHolder)holder).tv_ResultName
                .setText(mResultList.get(position).getName());
        ((ResultViewHolder)holder).tv_ResultAddress
                .setText(mContext.getString(R.string.addressTitle)+" "+ mResultList.get(position).getVicinity());
        //show rating bar, if rating is null, rating bar will be hidden
        if(mResultList.get(position).getRating() == null)
        {
            ((ResultViewHolder)holder).ln_Rating.setVisibility(View.GONE);
        }
        else
        {
            ((ResultViewHolder)holder).ln_Rating.setVisibility(View.VISIBLE);

            ((ResultViewHolder)holder).tv_Rating.setText(mResultList.get(position).getRating() +"");
            ((ResultViewHolder)holder).rtb_Rating.setRating(Float.parseFloat(mResultList.get(position).getRating().toString()));
        }

        if(mResultList.get(position).getPhotos() != null && mResultList.get(position).getPhotos().get(0) != null) {
            Glide.with(mContext)
                    .load(Constants.TEMP_IMAGE_URL + mResultList.get(position).getPhotos().get(0).getPhotoReference())
                    .centerCrop()
                    .error(R.drawable.ic_default_image)
                    .into(((ResultViewHolder) holder).img_Image);
        }
        else
        {
            Glide.with(mContext)
                    .load(R.drawable.ic_default_image)
                    .centerCrop()
                    .into(((ResultViewHolder) holder).img_Image);
        }

        ((ResultViewHolder) holder).cv_Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mInitialLocation != null) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constants.LOCATION_ID, mResultList.get(position).getPlaceId());
                    intent.putExtra(Constants.INITIAL_LAT_LOCATION, mInitialLocation.latitude);
                    intent.putExtra(Constants.INITIAL_LNG_LOCATION, mInitialLocation.longitude);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder
    {
        public CardView cv_Item;
        public TextView tv_ResultName, tv_ResultAddress,tv_Rating;
        public ImageView img_Image;
        public RatingBar rtb_Rating;
        public LinearLayout ln_Rating;
        public ResultViewHolder(View itemView) {
            super(itemView);
            img_Image = (ImageView) itemView.findViewById(R.id.image_view_avatar);
            tv_ResultName = (TextView) itemView.findViewById(R.id.text_view_name);
            cv_Item = (CardView) itemView.findViewById(R.id.card_view_result_item);
            rtb_Rating = (RatingBar) itemView.findViewById(R.id.rating_bar_location_rating_start);
            tv_ResultAddress = (TextView) itemView.findViewById(R.id.text_view_address);
            tv_Rating = (TextView) itemView.findViewById(R.id.text_view_rating_number);
            ln_Rating = (LinearLayout) itemView.findViewById(R.id.linear_layout_rating);
        }
    }
    public int getItemCount() {
        return mResultList.size();
    }

    public void setmInitialLocation(LatLng mInitialLocation) {
        this.mInitialLocation = mInitialLocation;
    }
}
