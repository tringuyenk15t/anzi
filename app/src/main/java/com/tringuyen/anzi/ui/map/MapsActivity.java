package com.tringuyen.anzi.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tringuyen.anzi.Constants;
import com.tringuyen.anzi.R;
import com.tringuyen.anzi.model.google.google_detail_activity.DetailResult;
import com.tringuyen.anzi.model.google.google_direction_direction.DirectionResponse;
import com.tringuyen.anzi.model.google.google_direction_direction.Route;
import com.tringuyen.anzi.model.google.google_search_activity.Result;
import com.tringuyen.anzi.network.GoogleAPI;
import com.tringuyen.anzi.network.GoogleServiceGenerator;
import com.tringuyen.anzi.ui.detail_activity.DetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener{

    private LatLng mInitialLocation;
    private GoogleMap mMap;
    private Toolbar mToolbar;
    private int mMapFlag;
    private FloatingActionButton mFloatActionButton;
    private List<Result> mResultList;
    private DetailResult mDetailLocation;
    private LatLng mSelectedMarkerLatLng;
    private Polyline mCurrentPolyline;
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
        mFloatActionButton = (FloatingActionButton) findViewById(R.id.fab_direction);
        mFloatActionButton.setVisibility(View.INVISIBLE);

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
        mMap.setOnMapClickListener(this);
        //LagLngBounds builder for zoom camera
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //enable current location service on map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
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
                .snippet(r.getPlaceId())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
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
                    .snippet(mDetailLocation.getPlaceId())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            builder.include(detailLatLng);

            String origin = mInitialLocation.latitude + "," + mInitialLocation.longitude;
            String destination = mDetailLocation.getGeometry().getLocation().getLat() +
                    "," + mDetailLocation.getGeometry().getLocation().getLng();
            setPolyline(origin,destination);
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

    /**
     * Add polyline on maps
     * @param origin - start point
     * @param destination - destination user want to check
     */
    private void setPolyline(String origin, String destination)
    {
        GoogleAPI google_direction = GoogleServiceGenerator.createService(GoogleAPI.class);
        Call<DirectionResponse> call = google_direction.getDirection(origin,destination);
        call.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
                DirectionResponse directionResponse = response.body();

                List<LatLng> path = new ArrayList<LatLng>();
                //add route latlng
                for(Route singleRoute : directionResponse.getRoutes())
                {
                    String encodedPath = singleRoute.getOverviewPolyline().getPoints().toString();
                    path.addAll(decodePolyLine(encodedPath));
                }
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(path);
                mCurrentPolyline = mMap.addPolyline(polylineOptions
                                    .width(12)
                                    .color(ContextCompat
                                    .getColor(getApplicationContext(),R.color.colorPrimary)));
            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {

            }
        });
    }

    /**
     * Decode form direction points into LatLng list
     * @param poly - encoded string contains a list of polyline points
     * @return decoded - list of LatLng objects
     */
    private List<LatLng> decodePolyLine(String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }
        return decoded;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        String markerName,markerAddress,imageUrl;
        double rating = -1.0;
        imageUrl = null;
        //show direction button
        mFloatActionButton.setVisibility(View.VISIBLE);

        if(marker.getTitle().equals(Constants.NORMAL_MARKER))
        {
            Result tempResult = getResultById(marker.getSnippet());
            //set selected marker for onDirectionButtonClicked()
            mSelectedMarkerLatLng = new LatLng(tempResult.getGeometry().getLocation().getLat(),
                                        tempResult.getGeometry().getLocation().getLng());

            markerName = tempResult.getName();
            markerAddress = tempResult.getVicinity();
            if(tempResult.getPhotos() != null && tempResult.getPhotos().get(0) != null) {
                imageUrl = Constants.TEMP_IMAGE_URL + tempResult.getPhotos().get(0).getPhotoReference();
            }
            if(tempResult.getRating() != null)
            {
                rating = tempResult.getRating();
            }
        }
        else if (marker.getTitle().equals(Constants.DETAIL_MARKER))
        {
            //set selected marker for onDirectionButtonClicked()
            mSelectedMarkerLatLng = new LatLng(mDetailLocation.getGeometry().getLocation().getLat(),
                    mDetailLocation.getGeometry().getLocation().getLng());

            markerName = mDetailLocation.getName();
            markerAddress = mDetailLocation.getVicinity();
            if(mDetailLocation.getPhotos() != null && mDetailLocation.getPhotos().get(0) != null) {
                imageUrl = Constants.TEMP_IMAGE_URL + mDetailLocation.getPhotos().get(0).getPhotoReference();
            }
            if(mDetailLocation.getRating() != null)
            {
                rating = mDetailLocation.getRating();
            }
        }
        else
        {
            mFloatActionButton.setVisibility(View.INVISIBLE);
            return null;
        }
        return infoWindowBinding(marker,markerName,markerAddress,imageUrl,rating);
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

    @Override
    public void onMapClick(LatLng latLng) {
        //hide direction button when user click on anywhere else on the map
        mFloatActionButton.setVisibility(View.INVISIBLE);
    }

    public void onDirectionButtonClicked (View v)
    {
        //clear previous polyline
        if(mCurrentPolyline != null) {
            mCurrentPolyline.remove();
        }
        //draw polyline
        String origin = mInitialLocation.latitude +","+mInitialLocation.longitude;
        String destination = mSelectedMarkerLatLng.latitude + "," + mSelectedMarkerLatLng.longitude;
        setPolyline(origin,destination);
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

    private View infoWindowBinding (Marker marker,String name, String address,
                                    String photoUrl, double rating)
    {
        View v = getLayoutInflater().inflate(R.layout.adapter_marker_infowindow,null);
        TextView tvName = (TextView) v.findViewById(R.id.text_view_marker_name);
        TextView tvAddress = (TextView) v.findViewById(R.id.text_view_marker_address);
        ImageView imgvPhoto = (ImageView) v.findViewById(R.id.image_view_maker_photo);
        TextView tvRating = (TextView) v.findViewById(R.id.text_view_rating_number);
        RatingBar rtbRating = (RatingBar) v.findViewById(R.id.rating_bar_location_rating_start);

        tvName.setText(name);
        tvAddress.setText(getString(R.string.addressTitle)+" "+address);
        imgvPhoto.setImageResource(R.drawable.ic_default_image);

        if(rating == -1.0)
        {
            tvRating.setText("N/A");
            rtbRating.setVisibility(View.GONE);
        }
        else
        {
            rtbRating.setVisibility(View.VISIBLE);

            tvRating.setText(rating +"");
            rtbRating.setRating(Float.parseFloat(rating+""));
        }

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
