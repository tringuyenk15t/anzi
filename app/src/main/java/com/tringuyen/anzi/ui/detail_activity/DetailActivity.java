package com.tringuyen.anzi.ui.detail_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.tringuyen.anzi.Constants;
import com.tringuyen.anzi.LocalLibrary;
import com.tringuyen.anzi.R;
import com.tringuyen.anzi.model.google.google_detail_activity.GoogleDetailResponse;
import com.tringuyen.anzi.model.google.google_detail_activity.Result;
import com.tringuyen.anzi.network.GoogleAPI;
import com.tringuyen.anzi.network.GoogleServiceGenerator;
import com.tringuyen.anzi.ui.map.MapsActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private String mLocationID;
    private TextView tv_Name, tv_Address;
    private ImageView img_LocationImage;
    private Toolbar mToolbar;
    private ProgressDialog mProgressDiaglog;
    private GoogleAPI googleAPI;
    private Result mDetailLocation;

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
        tv_Name = (TextView) findViewById(R.id.text_view_name);
        tv_Address = (TextView) findViewById(R.id.text_view_address);
        img_LocationImage = (ImageView) findViewById(R.id.image_view_avatar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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
                tv_Name.setText(mDetailLocation.getName());
                tv_Address.setText(mDetailLocation.getVicinity());

                if(mDetailLocation.getPhotos().size() > 0)
                {
                    //TODO should show more image
                    Glide.with(getBaseContext())
                            .load(Constants.TEMP_DETAIL_IMAGE_URL + mDetailLocation.getPhotos().get(0).getPhotoReference())
                            .centerCrop()
                            .into(img_LocationImage);
                }
                else
                {
                    Glide.with(getBaseContext())
                            .load(R.drawable.ic_detail_default_image)
                            .centerCrop()
                            .into(img_LocationImage);
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
        //TODO implement mapview with direction to location
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
