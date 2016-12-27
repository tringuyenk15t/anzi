package com.tringuyen.anzi.ui.detail_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.tringuyen.anzi.Constants;
import com.tringuyen.anzi.R;
import com.tringuyen.anzi.model.event_bus.PhotoClickEvent;
import com.tringuyen.anzi.model.google.google_detail_activity.GoogleDetailResponse;
import com.tringuyen.anzi.model.google.google_detail_activity.DetailResult;
import com.tringuyen.anzi.model.google.google_search_activity.Photo;
import com.tringuyen.anzi.network.GoogleAPI;
import com.tringuyen.anzi.network.GoogleServiceGenerator;
import com.tringuyen.anzi.ui.map.MapsActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private String mLocationID;
    private TextView mNameTextView, mAddressTextView,mPhoneTextView,mWebsiteTextView,mRatingTextView;
    private LinearLayout mAddressLinearLayout, mRatingLinearLayout, mPhoneLinearLayout,mWebsiteLinearLayout;
    private RatingBar mRatingBar;
    private ImageView mLargePhotoImageView;
    private Toolbar mToolbar;
    private ProgressDialog mProgressDiaglog;
    private GoogleAPI googleAPI;
    private DetailResult mDetailLocation;

    private RecyclerView mPhotoListRecyclerView;
    private RecyclerView.LayoutManager mPhotoListManager;
    private RecyclerView.Adapter mPhotoListAdapter;

    private List<Photo> mPhotoList;

    private LatLng mInitialLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // get id location from bundle
        Intent intent = getIntent();
        mLocationID = intent.getStringExtra(Constants.LOCATION_ID);
        mInitialLocation = new LatLng(intent.getDoubleExtra(Constants.INITIAL_LAT_LOCATION,0.0),
                intent.getDoubleExtra(Constants.INITIAL_LNG_LOCATION,0.0));
        initializedView();
    }

    private void initializedView() {
        mNameTextView = (TextView) findViewById(R.id.text_view_name);

        mAddressTextView = (TextView) findViewById(R.id.text_view_address);
        mAddressLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_address);
        mPhoneTextView = (TextView) findViewById(R.id.text_view_phone);
        mPhoneLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_phone);
        mWebsiteTextView = (TextView) findViewById(R.id.text_view_website);
        mWebsiteLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_website);
        mRatingTextView = (TextView) findViewById(R.id.text_view_rating_number);
        mRatingLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_rating);

        mRatingBar = (RatingBar) findViewById(R.id.rating_bar_rating_start);

        mLargePhotoImageView = (ImageView) findViewById(R.id.image_view_large_photo);
        mPhotoListRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_photo_list);
        mPhotoListManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        //toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //photolist
        mPhotoListRecyclerView.setLayoutManager(mPhotoListManager);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mPhotoListRecyclerView);
        mPhotoList = new ArrayList<>();
        mPhotoListAdapter = new PhotoListAdapter(this,mPhotoList);
        mPhotoListRecyclerView.setAdapter(mPhotoListAdapter);

        mProgressDiaglog = new ProgressDialog(this);
        mProgressDiaglog.setTitle(getString(R.string.detailProgressTitle));
        mProgressDiaglog.setMessage(getString(R.string.detailProgressMessage));
        mProgressDiaglog.setCancelable(false);

        mProgressDiaglog.show();

        googleAPI = GoogleServiceGenerator.createService(GoogleAPI.class);
        Call<GoogleDetailResponse> call = googleAPI.searchDetailResult(mLocationID);
        call.enqueue(new Callback<GoogleDetailResponse>() {
            @Override
            public void onResponse(Call<GoogleDetailResponse> call, Response<GoogleDetailResponse> response) {

                mDetailLocation = response.body().getResult();
                mNameTextView.setText(mDetailLocation.getName());

                if(mDetailLocation.getFormattedAddress()!= null)
                {
                    mAddressLinearLayout.setVisibility(View.VISIBLE);
                    mAddressTextView.setText(mDetailLocation.getFormattedAddress());
                }

                if(mDetailLocation.getFormattedPhoneNumber() != null)
                {
                    mPhoneLinearLayout.setVisibility(View.VISIBLE);
                     mPhoneTextView.setText(mDetailLocation.getFormattedPhoneNumber());
                }

                if(mDetailLocation.getWebsite() != null)
                {
                    mWebsiteLinearLayout.setVisibility(View.VISIBLE);
                    mWebsiteTextView.setText(mDetailLocation.getWebsite());
                }

                if(mDetailLocation.getRating() != null)
                {
                    mRatingLinearLayout.setVisibility(View.VISIBLE);
                    mRatingTextView.setText(mDetailLocation.getRating().toString());
                    mRatingBar.setRating(Float.parseFloat(mDetailLocation.getRating().toString()));
                }

                if(mDetailLocation.getPhotos() != null  && mDetailLocation.getPhotos().get(0) != null)
                {
                    mPhotoListRecyclerView.setVisibility(View.VISIBLE);
                    //load photo sub list
                    mPhotoList.clear();
                    mPhotoList.addAll(mDetailLocation.getPhotos());
                    mPhotoListAdapter.notifyDataSetChanged();
                    //load main photo
                    Glide.with(getBaseContext())
                            .load(Constants.TEMP_DETAIL_IMAGE_URL + mDetailLocation.getPhotos().get(0).getPhotoReference())
                            .centerCrop()
                            .into(mLargePhotoImageView);
                }
                else
                {
                    //hide photo list
//                    mPhotoListRecyclerView.setVisibility(View.GONE);
                    //load default main photo when photo list is null
                    Glide.with(getBaseContext())
                            .load(R.drawable.ic_default_image)
                            .centerCrop()
                            .into(mLargePhotoImageView);
                }

                mProgressDiaglog.dismiss();
            }

            @Override
            public void onFailure(Call<GoogleDetailResponse> call, Throwable t) {
                mProgressDiaglog.dismiss();
                Log.e("Error: ", t.getMessage());
            }
        });
    }

    public void onDirectionCLicked(View view)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(Constants.INITIAL_LAT_LOCATION,mInitialLocation.latitude);
        intent.putExtra(Constants.INITIAL_LNG_LOCATION,mInitialLocation.longitude);
        intent.putExtra(Constants.MAP_FLAG,Constants.DETAIL_TO_MAP_FLAG);
        intent.putExtra(Constants.LOCATION_DETAIL,mDetailLocation);
        startActivity(intent);
    }

    @Subscribe
    public void onPhotoClickEvent(PhotoClickEvent event)
    {
        Glide.with(this)
                .load(Constants.TEMP_DETAIL_IMAGE_URL + event.getmPhotoUrl())
                .centerCrop()
                .into(mLargePhotoImageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
