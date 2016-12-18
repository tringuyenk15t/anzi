package com.tringuyen.anzi.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tringuyen.anzi.Constants;
import com.tringuyen.anzi.R;
import com.tringuyen.anzi.model.google.google_detail_activity.DetailResult;
import com.tringuyen.anzi.model.google.google_search_activity.Result;
import com.tringuyen.anzi.ui.detail_activity.DetailActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener{

    private LatLng mInitialLocation;
    private GoogleMap mMap;
    private Toolbar mToolbar;
    private int mMapFlag;

    private List<Result> mResultList;
    private DetailResult mDetailLocation;

    private final Map<Marker, Bitmap> images = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        intializedView();
    }

    private void intializedView()
    {
        //get the initial location from intent
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra(Constants.INITIAL_LAT_LOCATION,0.0);
        double lng = intent.getDoubleExtra(Constants.INITIAL_LNG_LOCATION,0.0);
        mInitialLocation = new LatLng(lat,lng);
        mMapFlag = intent.getIntExtra(Constants.MAP_FLAG,-1);

        //setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(Constants.TOOLBAR_TITLE);
        setSupportActionBar(mToolbar);

        //initialize list of result and detail location base on intent
        if(mMapFlag == Constants.SEARCH_TO_MAP_FLAG)
        {
            mResultList = intent.getParcelableArrayListExtra(Constants.LOCATION_LIST);
        }
        else if (mMapFlag == Constants.DETAIL_TO_MAP_FLAG)
        {
            mDetailLocation = intent.getParcelableExtra(Constants.LOCATION_DETAIL);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //setup map
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowClickListener(this);
        //LagLngBounds builder for zoom camera
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //enable current location service on map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }
        //add marker from list result
        if(mResultList != null)
        {
            for (Result r : mResultList)
            {
                LatLng latLng = new LatLng(r.getGeometry().getLocation().getLat(),
                        r.getGeometry().getLocation().getLng());
                mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(String.valueOf(Constants.NORMAL_MARKER))
                .snippet(r.getPlaceId()));
                builder.include(latLng);
            }
        }

        //add marker from detail location
        if(mDetailLocation != null)
        {
            LatLng detailLatLng = new LatLng(mDetailLocation.getGeometry().getLocation().getLat(),
                    mDetailLocation.getGeometry().getLocation().getLng());
            mMap.addMarker(new MarkerOptions()
                    .position(detailLatLng)
                    .title(Constants.DETAIL_MARKER)
                    .snippet(mDetailLocation.getPlaceId()));
            builder.include(detailLatLng);
        }

        //add initial location with difference color
        if(mInitialLocation != null)
        {
            mMap.addMarker(new MarkerOptions()
                    .position(mInitialLocation)
                    .title(Constants.INITIAL_LOCATION_TITLE)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            builder.include(mInitialLocation);
        }

        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        String markerName,markerAddress,imageUrl;
        imageUrl = null;
        if(marker.getTitle().equals(Constants.NORMAL_MARKER))
        {
            //TODO bind data into info window
            Result tempResult = getResultById(marker.getSnippet());
            markerName = tempResult.getName();
            markerAddress = tempResult.getVicinity();
            if(tempResult.getPhotos() != null && tempResult.getPhotos().get(0) != null) {
                imageUrl = Constants.TEMP_IMAGE_URL + tempResult.getPhotos().get(0).getPhotoReference();
            }
        }
        else if (marker.getTitle().equals(Constants.DETAIL_MARKER))
        {
            markerName = mDetailLocation.getName();
            markerAddress = mDetailLocation.getVicinity();
            if(mDetailLocation.getPhotos() != null && mDetailLocation.getPhotos().get(0) != null) {
                imageUrl = Constants.TEMP_IMAGE_URL + mDetailLocation.getPhotos().get(0).getPhotoReference();
            }
        }
        else
        {
            return null;
        }
        return infoWindowBinding(marker,markerName,markerAddress,imageUrl);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(!marker.getTitle().equals(Constants.INITIAL_LOCATION_TITLE)) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(Constants.LOCATION_ID, marker.getSnippet());
            intent.putExtra(Constants.INITIAL_LAT_LOCATION, mInitialLocation.latitude);
            intent.putExtra(Constants.INITIAL_LNG_LOCATION, mInitialLocation.longitude);
            this.startActivity(intent);
        }
    }

    public Result getResultById(String resultID)
    {
        Result item = null;
        for (Result result : mResultList)
        {
            if(result.getPlaceId().equals(resultID))
            {
                item = result;
                break;
            }
        }
        return item;
    }

    private View infoWindowBinding (Marker marker,String name, String address, String photoUrl)
    {
        View v = getLayoutInflater().inflate(R.layout.adapter_marker_infowindow,null);
        TextView tvName = (TextView) v.findViewById(R.id.text_view_marker_name);
        TextView tvAddress = (TextView) v.findViewById(R.id.text_view_marker_address);
        ImageView imgvPhoto = (ImageView) v.findViewById(R.id.image_view_maker_photo);


        tvName.setText(name);
        tvAddress.setText(address);
        //load photo
        Bitmap image = images.get(marker);
        if (image == null)
        {
            Glide.with(this)
                    .load(photoUrl)
                    .asBitmap()
                    .dontAnimate()
                    .centerCrop()
                    .into(new MarkerImageTarget(marker));
            return v;
        }
        else
        {
            imgvPhoto.setImageBitmap(image);
            return v;
        }
    }

    private class MarkerImageTarget extends SimpleTarget<Bitmap>
    {
        Marker marker;
        MarkerImageTarget (Marker marker)
        {
            super(100,100);// otherwise Glide will load original sized bitmap which is huge
            this.marker = marker;
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            images.put(marker,resource);
            marker.showInfoWindow();
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {
            images.remove(marker); // clean up previous image, it became invalid
            // don't call marker.showInfoWindow() to update because this is most likely called from Glide.into()
        }
    }

}
